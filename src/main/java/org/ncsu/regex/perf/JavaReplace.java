package org.ncsu.regex.perf;
import java.util.regex.Pattern;

public class JavaReplace {
	
	//https://github.com/apache/incubator-tamaya/pull/21
	public static void testStringReplace(String s){
		s.replace(".", "_");
	}
	public static void testRegexReplace(String s){
		s.replaceAll("\\.", "_");	
	}
	
	public static void testStringReplace(String[] strs){
		for(String s:strs){
			s.replace(".", "_");
		}
	}
	public static void testRegexReplace(String[] strs){
		for(String s:strs){
			s.replaceAll("\\.", "_");
		}	
	}
	

}
