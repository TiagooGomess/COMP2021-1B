package analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import analysis.symbol.SymbolTable;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.examples.ExamplePostorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePrintVariables;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class AnalysisStage implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0) {
            return null;
        }
        if (parserResult.getRootNode() == null) {
            return null;
        }

        JmmNode node = parserResult.getRootNode().sanitize();

        JmmVisitor visitor = new JmmVisitor();
        visitor.visit(node, null);
        SymbolTable symbolTable = visitor.getSymbolTable();
        visitor.analyseMethodValues();

        List<Report> reports = parserResult.getReports();
        reports.addAll(symbolTable.getReports());

        for (Report report : reports) {
            System.out.println(report.toString());
        }

        return new JmmSemanticsResult(node, symbolTable, reports);

    }

}