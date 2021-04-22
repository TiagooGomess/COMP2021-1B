import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import analysis.symbol.SymbolTable;
import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

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

    private String getJasminInstruction(Instruction instruction) {
        // TODO: update stackLimit and locals
        // TODO: [CHECKPOINT2] verify assignments, arithmetic Expressions and method Calls

        return instruction.toString() + "\n";
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

        System.out.println(jasminCode);
        return jasminCode.toString();
    }

}
