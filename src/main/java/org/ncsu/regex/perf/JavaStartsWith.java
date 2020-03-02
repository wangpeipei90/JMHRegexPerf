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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JavaStarsWithTest obj=new JavaStarsWithTest();
//		obj.testPerformance1(false);
//		obj.testPerformance1(true);
		
		obj.testPerformance2(false);
		obj.testPerformance2(true);
//		
//		obj.testPerformance3(false);
//		obj.testPerformance3(true);
	}

}
