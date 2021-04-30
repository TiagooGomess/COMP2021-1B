package semantic;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

import static org.junit.Assert.assertEquals;

public class SemanticTests {

    @Test
    public void test1() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/arr_index_not_int.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test2() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/arr_size_not_int.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test3() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/badArguments.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(3, numErrors);
    }

    @Test
    public void test4() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/binop_incomp.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test5() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/funcNotFound.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(2, numErrors);
    }

    @Test
    public void test6() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/simple_length.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test7() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/var_exp_incomp.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test8() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/var_lit_incomp.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test9() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/var_undef.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void test10() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Fac.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test11() {
        String jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test12() {
        String jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test13() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test14() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Life.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test15() {
        String jmmCode = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test16() {
        String jmmCode = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test17() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Simple.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test18() {
        /*String jmmCode = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());*/
    }

    @Test
    public void test19() {
        String jmmCode = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test20() {
        /*String jmmCode = SpecsIo.getResource("fixture/private/Turing.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.noErrors(semanticsResult.getReports());*/
    }

    @Test
    public void testX() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/var_undef.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }

    @Test
    public void extra() {
        String jmmCode = SpecsIo.getResource("fixtures/public/fail/semantic/extra/miss_type.jmm");
        JmmParserResult parseResult = TestUtils.parse(jmmCode);
        JmmSemanticsResult semanticsResult = TestUtils.analyse(parseResult);
        TestUtils.mustFail(semanticsResult.getReports());
        long numErrors = TestUtils.getNumErrors(semanticsResult.getReports());
        assertEquals(1, numErrors);
    }


}
