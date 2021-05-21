
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

public class BackendTest {

    private void testFile(String file, long numErrors) {
        long numberOfErrors;
        var parseResult = TestUtils.parse(SpecsIo.getResource("fixtures/public/" + file + ".jmm"));

        var analysisResult = TestUtils.analyse(parseResult);
        numberOfErrors = analysisResult.getReports().size();
        if (numberOfErrors > 0) {
            assertEquals(numberOfErrors, numErrors);
            return;
        }

        var optimizationResult = TestUtils.optimize(analysisResult, false);
        numberOfErrors = optimizationResult.getReports().size();
        if (numberOfErrors > 0) {
            assertEquals(numberOfErrors, numErrors);
            return;
        }

        var backendResult = TestUtils.backend(optimizationResult);
        numberOfErrors = backendResult.getReports().size();
        assertEquals(numberOfErrors, numErrors);
    }

    private void testFile(String file, String expectedOutput) {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/" + file + ".jmm"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        if (expectedOutput != null)
            assertEquals(expectedOutput, output.trim());
    }

    private void testOurFile(String name, int expectedErrors) {
        testFile("../ourTests/" + name, expectedErrors);
    }

    private void testOurFile(String name, String expectedOutput) {
        testFile("../ourTests/" + name, expectedOutput);
    }

    @Test
    public void testHelloWorld() {
        testFile("HelloWorld", "Hello, World!");
    }

    @Test
    public void testSimple() {
        testFile("Simple", "30");
    }

    @Test
    public void testFac() {
        testFile("Fac", "3628800");
    }

    @Test
    public void testWhileAndIf() {
        testFile("WhileAndIF", "10\n10\n10\n10\n10\n10\n10\n10\n10\n10");
    }

    @Test
    public void testFindMaximum() {
        testFile("FindMaximum", "Result: 28");
    }

    @Test
    public void testQuickSort() {
        testFile("QuickSort", "1\n2\n3\n4\n5\n6\n7\n8\n9\n10");
    }

    @Test
    public void testTuring() {
        testFile("../private/Turing", "111111111111111111\n000000000000000000");
    }

    @Test
    public void testTicTacToe() {
        testFile("TicTacToe", "0|0|0\n" +
                "- - -\n" +
                "0|0|0\n" +
                "- - -\n" +
                "0|0|0\n" +
                "\n" +
                "Congratulations, -1, you have won the game.");
    }

    @Test
    public void testLazySort() {
        // impossible to predict
        testFile("Lazysort", null);
    }

    @Test
    public void testMonteCarloPi() {
        // the result is not always the same
        testFile("MonteCarloPi", null);
    }

    @Test
    public void testLife() {
        // too slow to test
        // testFile("Life", "");
    }

    @Test
    public void testOverloading() {
        testOurFile("Overloading", "3\n-1\n6\n-3");
    }

    @Test
    public void testReturnTypes() {
        testOurFile("ReturnTypes", 1);
    }

    @Test
    public void testStaticMethods() {
        testOurFile("StaticMethods", 3);
    }

    @Test
    public void testUninitialized() {
        testOurFile("Uninitialized", 1);
    }
}