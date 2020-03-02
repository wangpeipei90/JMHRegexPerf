package org.ncsu.regex.perf;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;
public class JavaStarsWithTest {

	final int MAX_STR_LEN = 100000;
	final int COUNT = 1000;
	final int TIMES = 20;
	
	private void runPerf(String[] strs, String desc, int times,boolean onlyMatchStr){
		long[] times_str = new long[times];
		long[] times_regex = new long[times];
		
		long start, elapsedTime;
		for(int i=0; i<times; i++){		
//			start = System.nanoTime();
//			for(String s:strs){
//				JavaReplace.testStringReplace(s);
//			}			  		
//			elapsedTime = System.nanoTime() - start;
			
			start = System.currentTimeMillis();
			JavaStartsWith.testStringStartsWith(strs);
			elapsedTime = System.currentTimeMillis() - start;
			times_str[i]=elapsedTime;
			
			start = System.currentTimeMillis();   
			JavaStartsWith.testStringFirstMatch(strs);
			elapsedTime = System.currentTimeMillis() - start;
			times_regex[i]=elapsedTime;
		}
		
		System.out.println("---------------"+desc+" onlyMatchStr:"+onlyMatchStr+"---------------------------");
		System.out.println("Average run time in milli seconds of "+times+" times for String StarsWith is "+Arrays.stream(times_str).average());
		System.out.println("Average run time in milli seconds of "+times+" times for String FirstMatch is "+Arrays.stream(times_regex).average());
		System.out.println("details of String StarsWith: "+Arrays.toString(times_str));
		System.out.println("details of Regex FirstMatch: "+Arrays.toString(times_regex));
		System.out.println("---------------end---------------------------");
	}
	
//	@Test
	public void testPerformance1(boolean onlyMatchStr){
		RandomStringGenerator g=GenerateStrings.getGeneratorAlphaNumeric();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				strs[i]="abc"+s;
			}
		}
		this.runPerf(strs, "strings of alpha numeric",this.TIMES,onlyMatchStr);
	}

//	@Test
	public void testPerformance2(boolean onlyMatchStr) {
		RandomStringGenerator g=GenerateStrings.getGeneratorASCII();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				strs[i]="abc"+s;
			}
		}
		this.runPerf(strs, "strings of ASCII",this.TIMES,onlyMatchStr);
	}

//	@Test
	public void testPerformance3(boolean onlyMatchStr) {
		RandomStringGenerator g=GenerateStrings.getGeneratorUnicode();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				strs[i]="abc"+s;
			}
		}
		this.runPerf(strs, "strings of Unicode",this.TIMES,onlyMatchStr);
	}
}
