package benchmark;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Test {

	public static void main(String[] args) {
		System.out.println(args[0] + "," + args[1]);
		try{
			Pattern pattern = Pattern.compile(".*" + args[0] + ".*" , Pattern.DOTALL);
			System.out.println(pattern.toString());
			System.out.println("matches? "+pattern.matcher(args[1]).matches());
		}catch(PatternSyntaxException e) {
			System.out.println("Regex Syntax is Invalid!");
		}
	}
		
}
