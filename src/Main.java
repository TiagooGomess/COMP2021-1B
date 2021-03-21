import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.SpecsIo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.ArrayList;

public class Main implements JmmParser {


	public JmmParserResult parse(String jmmCode) {
		try {
			JmmCompiler jmmCompiler = new JmmCompiler(new StringReader(jmmCode));
    		SimpleNode root = jmmCompiler.Program(); // returns reference to root node
			return new JmmParserResult(root, jmmCompiler.getReports());
		} catch(Exception e){
			System.err.println("Error catch in main");
			return null;
		}
	}

    public static void main(String[] args) {
		System.out.println("Compiling the code...\n\n");

		/* InputStream jmmStream = new ByteArrayInputStream(SpecsIo.getResource("public/HelloWorld.jmm").getBytes()); */

		FileInputStream file;
		try {
			file = new FileInputStream("test/fixtures/public/" + args[0] + ".jmm");
		} catch (Exception e) {
			System.err.println("File not found");
			return;
		}

		JmmCompiler myJmmCode = new JmmCompiler(file);
		SimpleNode root;

		try {
			root = myJmmCode.Program(); // returns reference to root node
			//System.out.println(root.toJson());
			root.dump("");

		} catch(ParseException exception){
			System.err.println("Errors were found while parsing file.\n" + exception.getMessage());
		}
				
    }
}