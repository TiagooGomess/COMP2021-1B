package syntactical;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

public class NoErrors {

    @Test
    public void testHelloWorld() {
        String jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testFindMaximum() {
        String jmmCode = SpecsIo.getResource("fixtures/public/FindMaximum.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testLazySort() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Lazysort.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testLife() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Life.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testMonteCarloPi() {
        String jmmCode = SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testQuickSort() {
        String jmmCode = SpecsIo.getResource("fixtures/public/QuickSort.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testSimple() {
        String jmmCode = SpecsIo.getResource("fixtures/public/Simple.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testTicTacToe() {
        String jmmCode = SpecsIo.getResource("fixtures/public/TicTacToe.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }

    @Test
    public void testWhileAndIF() {
        String jmmCode = SpecsIo.getResource("fixtures/public/WhileAndIF.jmm");
        TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
    }
}
