import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.io.StringReader;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.specs.util.SpecsIo;

public class ExampleTest {


  @Test
  public void testExpression() {		
		//assertEquals("Expression", TestUtils.parse("2+3\n").getRootNode().getKind());		
	}

  @Test
  public void testHelloWorld() {
    String jmmCode = SpecsIo.getResource("fixtures/public/HelloWorld.jmm");
    TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
  }

  @Test
  public void testTest() {
    String jmmCode = SpecsIo.getResource("fixtures/public/Test.jmm");
    TestUtils.noErrors(TestUtils.parse(jmmCode).getReports());
  }
  
}
