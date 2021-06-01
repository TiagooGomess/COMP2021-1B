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

import java.awt.*;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static pt.up.fe.comp.jmm.report.ReportType.ERROR;

public class Main implements JmmParser {

    public static void main(String[] args) {
        String fileStr;

        try {
            fileStr = Files.readString(Path.of(args[0]));
        } catch (Exception e) {
            System.err.println("--> File not found");
            return;
        }

        System.out.println("--> Parsing file...");

        Main m = new Main();
        JmmParserResult parserResult = m.parse(fileStr);
        try {
            System.out.println("--> Generating AST...");
            Files.writeString(Path.of("results/ast.json"), parserResult.toJson());
        } catch (Exception e) {
            System.err.println("--> File not found");
            return;
        }

        System.out.println("--> Generating OLLIR Code...");

        AnalysisStage analysisStage = new AnalysisStage();
        JmmSemanticsResult semanticResult = analysisStage.semanticAnalysis(parserResult);
        if (semanticResult == null)
            return;
        List<Report> reports = semanticResult.getReports();
        if (!reports.isEmpty()) {
            return;
        }


        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticResult);
        if (ollirResult == null || !ollirResult.getReports().isEmpty())
            return;

        System.out.println("--> Generating Jasmin Code...");

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);
        if (!jasminResult.getReports().isEmpty())
            return;

        System.out.println("--> Success");
        try {
            //File file = Path.of(".").toFile();
            //file.delete();
            //file.mkdirs();
            //jasminResult.compile(file);
            // jasminResult.compile();
            Files.write(Path.of(args[0].split("\\.")[0] + ".j"), jasminResult.getJasminCode().getBytes());
        } catch (Exception e) {
            System.out.println("Could not create .j file");
        }

        jasminResult.run();
    }

    @Override
    public JmmParserResult parse(String jmmCode) {
        try {
            JmmCompiler jmmCompiler = new JmmCompiler(new StringReader(jmmCode));
            SimpleNode root = jmmCompiler.Program(); // returns reference to root node
            return new JmmParserResult(root, jmmCompiler.getReports());
        } catch (Exception e) {
            System.err.println("--> Error catch in main");
            return null;
        }
    }
}