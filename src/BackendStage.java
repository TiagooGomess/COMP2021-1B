import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import analysis.symbol.SymbolTable;
import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

import static org.specs.comp.ollir.ElementType.*;

/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class BackendStage implements JasminBackend {
    private int currentVariableRegister;
    private Map<String, Integer> variableRegisterMap;
    private String actualNewClass = null;

    private class MethodLimits {
        int stackLimit = 105;
        int locals = 105;

        public int getStackLimit() {
            return stackLimit;
        }

        public void addStackLimit() {
            this.stackLimit++;
        }

        public int getLocals() {
            return locals;
        }

        public void addLocals() {
            this.locals++;
        }
    }

    ClassUnit classUnit;
    SymbolTable symbolTable;
    final Map<Method, MethodLimits> methodLimits = new HashMap<>();
    private int actualCompareLabel = 0;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        this.classUnit = ollirResult.getOllirClass();
        this.symbolTable = (SymbolTable) ollirResult.getSymbolTable();

        try {

            // Example of what you can do with the OLLIR class
            this.classUnit.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            this.classUnit.buildCFGs(); // build the CFG of each method
            this.classUnit.outputCFGs(); // output to .dot files the CFGs, one per method
            this.classUnit.buildVarTables(); // build the table of variables for each method
            // this.classUnit.show(); // print to console main information about the input OLLIR
            // Convert the OLLIR to a String containing the equivalent Jasmin code
            String jasminCode = this.getJasminCode(); // Convert node ...

            // More reports from this stage
            List<Report> reports = ollirResult.getReports();

            return new JasminResult(ollirResult, jasminCode, reports);

        } catch (OllirErrorException e) {
            return new JasminResult(this.classUnit.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }
    }

    private String getJasminReturnType(Type type) {
        return switch (type.getTypeOfElement()) {
            case INT32 -> "I";
            case BOOLEAN -> "Z";
            case ARRAYREF -> "[I";
            case THIS -> "this;";
            case STRING -> "Ljava/lang/String;";
            case VOID -> "V";
            case OBJECTREF, CLASS -> "L" + type + ";";
        };
    }

    private boolean isIntOrBooleanType(ElementType elementType) {
        return elementType == INT32 || elementType == BOOLEAN;
    }

    private String pushPrefix(ElementType type) {
        if (this.isIntOrBooleanType(type))
            return "i";
        return "a";
    }

    private void addRegister(StringBuilder builder, Element element) {
        int register = getRegister(((Operand) element).getName());
        if (register >= 0 && register < 4)
            builder.append("_");
        else
            builder.append(" ");
        builder.append(register);
    }

    private void pushToStack(StringBuilder builder, Element element) {
        pushToStack(builder, element, false);
    }

    private void pushToStack(StringBuilder builder, Element element, boolean loadReference) {
        if (element.isLiteral()) {
            int value = Integer.parseInt(((LiteralElement) element).getLiteral());
            if (value > 32767)
                builder.append("ldc ");
            else if (value > 127)
                builder.append("sipush ");
            else if (value > 5 || value < 0)
                builder.append("bipush ");
            else
                builder.append(pushPrefix(element.getType().getTypeOfElement())).append("const_");
            builder.append(value);
        } else {
            if (element instanceof ArrayOperand && !loadReference) {
                ArrayOperand operand = (ArrayOperand) element;
                // load array
                builder.append("aload");
                addRegister(builder, element);
                builder.append("\n");
                // load indexes
                for (Element index : operand.getIndexOperands())
                    pushToStack(builder, index);
                // load the array element
                builder.append("iaload");
            } else {
                if (element instanceof ArrayOperand)
                    builder.append("a");
                else
                    builder.append(pushPrefix(element.getType().getTypeOfElement()));
                builder.append("load");
                addRegister(builder, element);
            }
        }
        builder.append("\n");
    }

    private void storeFromStack(StringBuilder builder, Element element) {
        if (element instanceof ArrayOperand) {
            builder.append("iastore");
        } else {
            builder.append(pushPrefix(element.getType().getTypeOfElement()));
            builder.append("store");
            addRegister(builder, element);
        }
        builder.append("\n");
    }

    private String getJasminInstruction(Instruction instruction) {
        // TODO: update stackLimit and locals
        // TODO: [CHECKPOINT2] verify assignments, arithmetic Expressions and method Calls

        InstructionType instructionType = instruction.getInstType();

        StringBuilder builder = new StringBuilder();
        // builder.append("; ").append(instructionType.toString().toLowerCase()).append("\n");

        switch (instructionType) {
            case ASSIGN -> {
                AssignInstruction assignInstruction = (AssignInstruction) instruction;
                Element dest = assignInstruction.getDest();
                Instruction rhs = assignInstruction.getRhs();

                if (dest instanceof ArrayOperand) {
                    ArrayOperand array = (ArrayOperand) dest;
                    pushToStack(builder, array, true); // array reference
                    for (Element position : array.getIndexOperands()) // array position
                        pushToStack(builder, position);
                }

                builder.append(this.getJasminInstruction(rhs));
                storeFromStack(builder, dest);
            }
            case CALL -> {
                CallInstruction callInstruction = (CallInstruction) instruction;
                int numOperands = callInstruction.getNumOperands();
                CallType invocationType = callInstruction.getInvocationType();
                Element firstArg = callInstruction.getFirstArg();
                Element secondArg = callInstruction.getSecondArg();
                ArrayList<Element> listOfOperands = callInstruction.getListOfOperands();
                Type returnType = callInstruction.getReturnType();

                switch (invocationType) {
                    case invokevirtual -> {
                        String className = ((Operand) firstArg).getName().equals("this") ? classUnit.getClassName() : ((ClassType) firstArg.getType()).getName();
                        pushToStack(builder, firstArg);
                        StringBuilder argumentTypes = new StringBuilder();
                        for (Element argument : listOfOperands) {
                            pushToStack(builder, argument);
                            argumentTypes.append(getJasminReturnType(argument.getType()));
                        }

                        builder.append("invokevirtual ").append(className).append(".").append(((LiteralElement) secondArg).getLiteral().replace("\"", ""));
                        builder.append("(").append(argumentTypes).append(")");
                        builder.append(getJasminReturnType(returnType));
                    }
                    case invokestatic -> {
                        StringBuilder argumentTypes = new StringBuilder();
                        for (Element argument : listOfOperands) {
                            pushToStack(builder, argument);
                            argumentTypes.append(getJasminReturnType(argument.getType()));
                        }

                        builder.append("invokestatic ").append(((Operand) firstArg).getName());
                        builder.append(".").append(((LiteralElement) secondArg).getLiteral().replace("\"", ""));
                        builder.append("(").append(argumentTypes).append(")");
                        builder.append(getJasminReturnType(returnType));
                    }
                    case NEW -> {
                        actualNewClass = ((Operand) firstArg).getName();
                        if (actualNewClass.equals("array")) {
                            pushToStack(builder, listOfOperands.get(0));
                            builder.append("newarray int").append("\n");
                            // builder.append("dup\n");
                        } else {
                            builder.append("new ").append(actualNewClass).append("\n");
                            builder.append("dup\n");
                        }
                    }
                    case invokespecial -> {
                        if (actualNewClass == null)
                            actualNewClass = classUnit.getClassName();
                        builder.append("invokespecial ").append(actualNewClass).append(".");
                        builder.append("<init>()");
                        builder.append(getJasminReturnType(returnType));
                        actualNewClass = null;
                    }
                    case arraylength -> {
                        pushToStack(builder, firstArg);
                        builder.append("arraylength");
                    }
                }
                builder.append("\n");
                /*if (returnType.getTypeOfElement() != VOID) {
                    builder.append(pushPrefix(returnType.getTypeOfElement()));
                    builder.append("store_<<LOCAL>>");
                }*/
            }
            case GOTO -> {
                GotoInstruction gotoInstruction = (GotoInstruction) instruction;
                String label = gotoInstruction.getLabel();
                builder.append("goto ").append(label).append("\n");
            }
            case BRANCH -> {
                CondBranchInstruction branchInstruction = (CondBranchInstruction) instruction;
                Element rightOperand = branchInstruction.getRightOperand();
                Element leftOperand = branchInstruction.getLeftOperand();
                String label = branchInstruction.getLabel();

                pushToStack(builder, leftOperand);
                pushToStack(builder, rightOperand);
                builder.append("iand").append("\n");
                builder.append("iconst_1").append("\n");
                builder.append("if_icmpeq ").append(label).append("\n");
            }
            case RETURN -> {
                ReturnInstruction returnInstruction = (ReturnInstruction) instruction;
                boolean hasReturnValue = returnInstruction.hasReturnValue();
                Element operand = returnInstruction.getOperand();
                ElementType elementType = VOID;

                if (operand != null) {
                    elementType = operand.getType().getTypeOfElement();
                    pushToStack(builder, operand);
                }

                if (hasReturnValue && operand != null)
                    builder.append(this.isIntOrBooleanType(elementType) ? "i" : "a");

                builder.append("return");

                builder.append("\n");
            }
            case PUTFIELD -> {
                PutFieldInstruction putFieldInstruction = (PutFieldInstruction) instruction;
                Element firstOperand = putFieldInstruction.getFirstOperand();
                Element secondOperand = putFieldInstruction.getSecondOperand();
                Element thirdOperand = putFieldInstruction.getThirdOperand();

                this.pushToStack(builder, thirdOperand);

                builder.append("\nputfield ");
                builder.append(((Operand) firstOperand).getName());
                builder.append("/").append(((Operand) secondOperand).getName()).append(" ");
                builder.append(this.getJasminReturnType(thirdOperand.getType()));
                builder.append("\n");

                // NON-STATIC METHOD -> THIS IS IN STACK POSITION 0

            }
            case GETFIELD -> {
                GetFieldInstruction getFieldInstruction = (GetFieldInstruction) instruction;
                Element firstOperand = getFieldInstruction.getFirstOperand();
                Element secondOperand = getFieldInstruction.getSecondOperand();
                String name = ((Operand) firstOperand).getName();
                /*if (name.equals("this"))
                    builder.append("aload_0");
                else {*/
                builder.append("aload_0\n");
                builder.append("getfield ");
                builder.append(this.getJasminReturnType(secondOperand.getType()));
                builder.append(" ").append(((Operand) secondOperand).getName());

                //}
                builder.append("\n");
            }
            case BINARYOPER -> {
                BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) instruction;
                Element rightOperand = binaryOpInstruction.getRightOperand();
                Operation operation = binaryOpInstruction.getUnaryOperation();
                Element leftOperand = binaryOpInstruction.getLeftOperand();

                this.pushToStack(builder, leftOperand);
                this.pushToStack(builder, rightOperand);
                //builder.append(operation.getOpType().name());

                OperationType operationType = operation.getOpType();

                switch (operationType) {
                    case AND, ANDB -> {
                        builder.append("iand");
                    }
                    case LTH, LTHI32 -> {
                        String label = "less" + actualCompareLabel;
                        String elseLabel = "greater" + actualCompareLabel++;
                        builder.append("if_icmpge ").append(label).append("\n");
                        builder.append("iconst_1").append("\n");
                        builder.append("goto ").append(elseLabel).append("\n");
                        builder.append(label).append(":\n");
                        builder.append("iconst_0").append("\n");
                        builder.append(elseLabel).append(":");
                    }
                    case ADD, ADDI32 -> {
                        builder.append("iadd");
                    }
                    case SUB, SUBI32 -> {
                        builder.append("isub");
                    }
                    case MUL, MULI32 -> {
                        builder.append("imul");
                    }
                    case DIV, DIVI32 -> {
                        builder.append("idiv");
                    }
                    case NOT, NOTB -> {
                        builder.append("ineg");
                    }
                }
                builder.append("\n");
            }
            case NOPER -> {
                SingleOpInstruction singleOpInstruction = (SingleOpInstruction) instruction;
                Element singleOperand = singleOpInstruction.getSingleOperand();
                this.pushToStack(builder, singleOperand);
            }
            default -> {
                builder.append("\n;-----------------------------;\n");
                builder.append(instruction);
                builder.append("\n;-----------------------------;\n");
            }
        }

        return builder.toString();
    }

    private void resetRegisters() {
        currentVariableRegister = 0;
        variableRegisterMap = new HashMap<>();
        addVariable("this");
    }

    private void addVariable(String variableName) {
        variableRegisterMap.put(variableName, currentVariableRegister++);
    }

    private int getRegister(String variableName) {
        if (!variableRegisterMap.containsKey(variableName)) {
            addVariable(variableName);
        }
        return variableRegisterMap.get(variableName);
    }

    private String getJasminCode() {
        StringBuilder jasminCode = new StringBuilder();

        jasminCode.append(".class public ");
        jasminCode.append(this.classUnit.getClassName()).append("\n");

        String superClassName = this.symbolTable.getSuper();
        jasminCode.append(".super ");
        if (superClassName != null) {
            jasminCode.append(this.symbolTable.getClass(superClassName).getImportName().replaceAll("^.", "").replace(".", "/"));
        } else
            jasminCode.append("java/lang/Object");
        jasminCode.append("\n\n");

        for (Field field : this.classUnit.getFields()) {
            jasminCode.append(".field ");
            jasminCode.append(field.getFieldName()).append(" ");
            jasminCode.append(this.getJasminReturnType(field.getFieldType())).append(" ");
            if (field.isInitialized())
                jasminCode.append(" = ").append(field.getInitialValue());
            jasminCode.append("\n");
        }

        jasminCode.append(".method public <init>()V\n");
        jasminCode.append("  aload_0\n");
        jasminCode.append("  invokenonvirtual java/lang/Object/<init>()V\n");
        jasminCode.append("  return\n");
        jasminCode.append(".end method\n\n");

        List<Method> methods = this.classUnit.getMethods();

        for (Method method : methods) {
            resetRegisters();

            this.methodLimits.put(method, new MethodLimits());

            if (method.isConstructMethod()) // already defined
                continue;

            jasminCode.append(".method public ");
            if (method.isStaticMethod())
                jasminCode.append("static ");

            jasminCode.append(method.getMethodName()).append("(");

            if (method.getMethodName().equals("main")) {
                jasminCode.append("[Ljava/lang/String;");
            } else {
                for (Element parameter : method.getParams()) {
                    jasminCode.append(this.getJasminReturnType(parameter.getType()));
                    addVariable(((Operand) parameter).getName());
                }
            }

            jasminCode.append(")");
            Type returnType = method.getReturnType();
            jasminCode.append(this.getJasminReturnType(returnType));
            jasminCode.append("\n  ");

            StringBuilder methodBuilder = new StringBuilder();


            for (Instruction instruction : method.getInstructions()) {
                List<String> instructionLabels = method.getLabels(instruction);
                for (String label : instructionLabels)
                    methodBuilder.append(label).append(":\n");
                methodBuilder.append(getJasminInstruction(instruction));
            }

            methodBuilder.insert(0, ".limit locals " + this.methodLimits.get(method).getLocals() + "\n\n");
            methodBuilder.insert(0, ".limit stack " + this.methodLimits.get(method).getStackLimit() + "\n");
            if (method.getReturnType().getTypeOfElement() == VOID)
                methodBuilder.append("return\n");

            jasminCode.append(methodBuilder.toString().trim().replace("\n", "\n  "));
            jasminCode.append("\n.end method\n\n");
        }

        try {
            Files.writeString(Path.of("results/code.j"), jasminCode);
        } catch (Exception e) {
            System.out.println(jasminCode);
        }

        return jasminCode.toString();
    }

}
