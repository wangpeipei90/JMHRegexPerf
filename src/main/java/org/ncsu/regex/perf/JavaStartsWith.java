package org.ncsu.regex.perf;
import java.util.regex.Pattern;

public class JavaStartsWith {

	public static void testRegexFirstMatch(String s){
		Pattern.matches("abc.*", s);	
	}
	
	public static void testStringFirstMatch(String s){
		s.matches("abc.*");	
	}
	
	public static void testStringStartsWith(String s){
		s.startsWith("abc");	
	}
	
	public static void testRegexFirstMatch(String[] strs){
		for(String s:strs){
			Pattern.matches("abc.*", s);	
		}
	}
	
	public static void testStringFirstMatch(String[] strs){
		for(String s:strs){
			s.matches("abc.*");	
		}
			
	}
	
	public static void testStringStartsWith(String[] strs){
		for(String s:strs){
			s.startsWith("abc");	
		}
			
	}
	

}
