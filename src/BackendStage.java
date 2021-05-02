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
    private class MethodLimits {
        int stackLimit = 99;
        int locals = 99;

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
            case OBJECTREF, CLASS -> type + ";";
        };
    }

    private boolean isIntOrBooleanType(ElementType elementType) {
        return elementType == INT32 || elementType == BOOLEAN;
    }

    private String pushToStack(Element singleOperand) {
        StringBuilder builder = new StringBuilder();
        String thirdElemLiteralString;
        if (singleOperand instanceof Operand)
            thirdElemLiteralString = ((Operand) singleOperand).getName();
        else
            thirdElemLiteralString = ((LiteralElement) singleOperand).getLiteral();

        if (singleOperand.getType().getTypeOfElement() == INT32) {
            int thirdElemInteger = Integer.parseInt(thirdElemLiteralString);
            if (thirdElemInteger >= 0 && thirdElemInteger <= 5)
                builder.append("iconst_").append(thirdElemLiteralString);
            else
                builder.append("bipush ").append(thirdElemLiteralString);
        }
        else if (singleOperand.getType().getTypeOfElement() == BOOLEAN) { // TODO: deal with non-literal elements
            if (thirdElemLiteralString.equals("true"))
                builder.append("iconst_1"); // true
            else
                builder.append("iconst_0"); // false
        }
        // TODO: deal with arrays and other objects

        return builder.toString();
    }

    private String getJasminInstruction(Instruction instruction) {
        // TODO: update stackLimit and locals
        // TODO: [CHECKPOINT2] verify assignments, arithmetic Expressions and method Calls

        InstructionType instructionType = instruction.getInstType();

        StringBuilder builder = new StringBuilder();

        switch (instructionType) {
            case ASSIGN -> {
                AssignInstruction assignInstruction = (AssignInstruction) instruction;
                Element dest = assignInstruction.getDest();
                Instruction rhs = assignInstruction.getRhs();

                builder.append(this.getJasminInstruction(rhs));

                builder.append("Assign instruction\n");
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
                        builder.append("invokevirtual");
                        // ...
                    }
                    case invokespecial -> {
                        builder.append("invokespecial");
                        // ...
                    }
                    case invokestatic -> {
                        builder.append("invokestatic");
                        // ...
                    }
                    case NEW -> {
                        builder.append("NEW");
                        // ...
                    }
                    case arraylength -> {
                        builder.append("arraylength");
                        // ...
                    }
                }


                builder.append("\n");
            }
            case GOTO -> { // Just for checkpoint 3
                builder.append("Goto instruction\n");
            }
            case BRANCH -> { // Just for checkpoint 3
                builder.append("Branch instruction\n");
            }
            case RETURN -> {
                ReturnInstruction returnInstruction = (ReturnInstruction) instruction;
                boolean hasReturnValue = returnInstruction.hasReturnValue();
                Element operand = returnInstruction.getOperand();
                ElementType elementType = VOID;
                if (operand != null)
                    elementType = operand.getType().getTypeOfElement();

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

                builder.append(this.pushToStack(thirdOperand));

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

                builder.append("getfield ");
                builder.append(((Operand) firstOperand).getName());
                builder.append("/").append(((Operand) secondOperand).getName()).append(" ");
                builder.append(this.getJasminReturnType(secondOperand.getType()));
                builder.append("\n");
            }
            case UNARYOPER -> {
                UnaryOpInstruction unaryOpInstruction = (UnaryOpInstruction) instruction;
                Element rightOperand = unaryOpInstruction.getRightOperand();
                Operation operation = unaryOpInstruction.getUnaryOperation();

                System.out.println("right operand: " + rightOperand.toString());
                System.out.println("operation: " + operation.toString());


                builder.append("Unaryoper instruction\n");
            }
            case BINARYOPER -> {
                BinaryOpInstruction binaryOpInstruction = (BinaryOpInstruction) instruction;
                Element rightOperand = binaryOpInstruction.getRightOperand();
                Operation operation = binaryOpInstruction.getUnaryOperation();
                Element leftOperand = binaryOpInstruction.getLeftOperand();


                builder.append("Binaryoper instruction\n");
            }
            case NOPER -> {
                SingleOpInstruction singleOpInstruction = (SingleOpInstruction) instruction;
                Element singleOperand = singleOpInstruction.getSingleOperand();

                builder.append(this.pushToStack(singleOperand));

                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private String getJasminCode() {
        StringBuilder jasminCode = new StringBuilder();

        jasminCode.append(".class public ");
        jasminCode.append(this.classUnit.getClassName()).append("\n");

        String superClassName = this.symbolTable.getSuper();
        jasminCode.append(".super ");
        if (superClassName != null) {
            jasminCode.append(this.symbolTable.getClass(superClassName).getImportName().replace(".", "/"));
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

        jasminCode.append("method public <init>()V\n");
        jasminCode.append("  aload_0\n");
        jasminCode.append("  invokenonvirtual java/lang/Object/<init>()V\n");
        jasminCode.append("  return\n");
        jasminCode.append(".end method\n\n");

        List<Method> methods = this.classUnit.getMethods();

        for (Method method : methods) {
            this.methodLimits.put(method, new MethodLimits());

            if (method.isConstructMethod()) // already defined
                continue;

            jasminCode.append(".method public ");
            if (method.isStaticMethod())
                jasminCode.append("static ");

            jasminCode.append(method.getMethodName()).append("(");

            for (Element parameter : method.getParams())
                jasminCode.append(this.getJasminReturnType(parameter.getType()));

            jasminCode.append(")");
            Type returnType = method.getReturnType();
            jasminCode.append(this.getJasminReturnType(returnType));
            jasminCode.append("\n  ");

            StringBuilder methodBuilder = new StringBuilder();

            for (Instruction instruction : method.getInstructions()) {
                methodBuilder.append(getJasminInstruction(instruction));
            }

            methodBuilder.insert(0, ".limit locals " + this.methodLimits.get(method).getLocals() + "\n\n");
            methodBuilder.insert(0, ".limit stack " + this.methodLimits.get(method).getStackLimit() + "\n");

            jasminCode.append(methodBuilder.toString().trim().replace("\n", "\n  "));
            jasminCode.append("\n.end method\n");
        }

        try {
            Files.writeString(Path.of("results/code.j"), jasminCode);
        } catch (Exception e) {
            System.out.println(jasminCode);
        }

        return jasminCode.toString();
    }

}
