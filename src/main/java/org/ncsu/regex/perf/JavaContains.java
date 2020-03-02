package org.ncsu.regex.perf;
import java.util.regex.Pattern;

public class JavaContains{

	public static void testRegexMatch(String s){
		Pattern.matches(".*abc.*", s);	
	}
	
	public static void testStringMatch(String s){
		s.matches(".*abc.*");	
	}
	
	public static void testStringContains(String s){
		s.contains("abc");	
	}
	
	
	
	public static void testRegexMatch(String[] strs){
		for(String s:strs){
			Pattern.matches(".*abc.*", s);	
		}
	}
	
	public static void testStringMatch(String[] strs){
		for(String s:strs){
			s.matches(".*abc.*");	
		}
	}
		
	public static void testStringContains(String[] strs){
		for(String s:strs){
			s.contains("abc");	
		}		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JavaContainsTest obj=new JavaContainsTest();
//		obj.testPerformance1(false);
//		obj.testPerformance1(true);
		
//		obj.testPerformance2(false);
//		obj.testPerformance2(true);
		
//		obj.testPerformance3(false);
//		obj.testPerformance3(true);
	}

}
