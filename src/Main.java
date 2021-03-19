
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.StringReader;

public class Main implements JmmParser {


	public JmmParserResult parse(String jmmCode) {
		
		try {
		    JmmCompiler jmmCompiler = new JmmCompiler(new StringReader(jmmCode));
    		SimpleNode root = jmmCompiler.Program(); // returns reference to root node
            	
    		return new JmmParserResult(root, new ArrayList<Report>());
		} catch(ParseException e) {
			throw new RuntimeException("Error while parsing", e);
		}
	}

    public static void main(String[] args) {
        System.out.println("Executing with args: " + Arrays.toString(args));
        if (args.length > 0 && args[0].contains("fail")) {
            throw new RuntimeException("It's supposed to fail");
        }


    }

	
}