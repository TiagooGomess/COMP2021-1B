
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

    private void testFile(String file, String expectedOutput) {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/" + file + ".jmm"));
        TestUtils.noErrors(result.getReports());

        System.out.println(result.getJasminCode());
        var output = result.run();
        assertEquals(expectedOutput, output.trim());
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
    public void testFindMaximum() {
        // Putfield com array
        // testFile("FindMaximum", "28");
    }

    @Test
    public void testLazySort() {
        // Simular stack para ver o erro
        // testFile("Lazysort", "");
    }

    @Test
    public void testLife() {
        // invalid arguments for trIdx
        // testFile("Life", "");
    }

    @Test
    public void testMonteCarloPi() {
        // logic error
        // testFile("MonteCarloPi", "");
    }

    @Test
    public void testQuickSort() {
        // Simular stack para ver o erro
        // testFile("QuickSort", "");
    }

    @Test
    public void testTicTacToe() {
        // Putfield com array
        // testFile("TicTacToe", "");
    }

    @Test
    public void testWhileAndIf() {
        testFile("WhileAndIF", "10\n10\n10\n10\n10\n10\n10\n10\n10\n10");
    }

    @Test
    public void testTuring() {
        // Putfield com array
        // testFile("../private/Turing", "");
    }

    @Test
    public void testTest() {
        testFile("Test", "10");
    }
}