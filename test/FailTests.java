import org.junit.Test;
import static org.junit.Assert.*;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;

import java.util.List;

public class FailTests {

    @Test
    public void testBlowUp() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/BlowUp.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(3, numErrors);
    }

    @Test
    public void testCompleteWhile() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(11, numErrors); // verificar de novo o número de erros
    }

    @Test
    public void testLengthError() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/LengthError.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void testMissingRightPar() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/MissingRightPar.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void testMultipleSequential() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/MultipleSequential.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(2, numErrors);
    }

    @Test
    public void testNestedLoop() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/syntactical/NestedLoop.jmm");
        TestUtils.mustFail(TestUtils.parse(jmmCode).getReports());
        long numErrors = TestUtils.getNumErrors(TestUtils.parse(jmmCode).getReports());
        assertEquals(2, numErrors);
    }

}