import analysis.AnalysisStage;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.jasmin.JasminUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main implements JmmParser {

    public static void main(String[] args) {
        String fileStr;
        String className = Path.of(args[0]).getFileName().toString().split("\\.")[0];

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
            Files.writeString(Path.of("results/" + className + ".ast.json"), parserResult.toJson());
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

        try {
            Files.writeString(Path.of("results/" + className + ".table.txt"), semanticResult.getSymbolTable().print());
        } catch (Exception e) {
            System.err.println("--> File not found");
            return;
        }

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticResult);
        if (ollirResult == null || !ollirResult.getReports().isEmpty())
            return;

        try {
            Files.writeString(Path.of("results/" + className + ".ollir"), ollirResult.getOllirCode());
        } catch (Exception e) {
            System.err.println("--> File not found");
            return;
        }

        System.out.println("--> Generating Jasmin Code...");

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);
        if (!jasminResult.getReports().isEmpty())
            return;

        try {
            File file = new File("results/" + className + ".j");
            (new FileOutputStream(file)).write(jasminResult.getJasminCode().getBytes());
            JasminUtils.assemble(file, Path.of("results").toFile());
            jasminResult.compile(new File("results"));
        } catch (Exception e) {
            System.err.println("--> Could not assemble file");
            return;
        }

        System.out.println("--> Running code\n");
        jasminResult.run(Collections.emptyList(), Arrays.asList(TestUtils.getLibsClasspath() + ":./results"));
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