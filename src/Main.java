import analysis.AnalysisStage;
import analysis.symbol.SymbolTable;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import analysis.JmmVisitor;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main implements JmmParser {

    public static void main(String[] args) {
        System.out.println("Compiling the code...\n\n");

        String fileStr;

        try {
            fileStr = Files.readString(Path.of("test/fixtures/public/" + args[0] + ".jmm"));
        } catch (Exception e) {
            System.err.println("File not found");
            return;
        }

        Main m = new Main();
        JmmParserResult parserResult = m.parse(fileStr);
        try {
            Files.writeString(Path.of("results/ast.json"), parserResult.toJson());
        } catch (Exception e) {
            System.err.println("File not found");
            return;
        }

        AnalysisStage analysisStage = new AnalysisStage();
        JmmSemanticsResult semanticResult = analysisStage.semanticAnalysis(parserResult);

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticResult);

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);


    }

    @Override
    public JmmParserResult parse(String jmmCode) {
        try {
            JmmCompiler jmmCompiler = new JmmCompiler(new StringReader(jmmCode));
            SimpleNode root = jmmCompiler.Program(); // returns reference to root node
            return new JmmParserResult(root, jmmCompiler.getReports());
        } catch (Exception e) {
            System.err.println("Error catch in main");
            return null;
        }
    }
}