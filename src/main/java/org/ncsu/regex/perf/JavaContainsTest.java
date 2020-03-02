package org.ncsu.regex.perf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
public class JavaContainsTest {
	
	@Param({ "10", "100", "1000", "10000" })
	private int N;  //size of the dataset
	
	@Param({"1","5","10","15","20","50","200","1000"})
	private int str_len; //the fixed size of generated string
//	private int max_str_len; //the max size of generated string with varied length
	
	@Param({"AlphaNumeric","ASCII","Unicode"})
	private String generator_type; // type of generator
	
	private List<String> DATA_FOR_TESTING;
	
	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}
	
	private List<String> createData() {
		
		
		RandomStringGenerator g;
		switch(generator_type){
		case "AlphaNumeric": g=GenerateStrings.getGeneratorAlphaNumeric();break;
		case "ASCII": g=GenerateStrings.getGeneratorASCII();break;
		case "Unicode": g=GenerateStrings.getGeneratorUnicode();break;
		default: throw new RuntimeException("Not valid Generator type!! only AlphaNumeric, ASCII, and Unicode are allowed");
		}
		
		List<String> data = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			data.add(g.generate(1,maxLen);
		}
		return data;
		
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			Random random = new Random();
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				int index=random.nextInt(s.length());
				strs[i]=s.substring(0, index)+"abc"+s.substring(index);
			}
		}
		this.runPerf(strs, "strings of alpha numeric",this.TIMES,onlyMatchStr);
	}

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
//				JavaContains.testStringContains(s);
//			}
//			elapsedTime = System.nanoTime() - start;
			
			start = System.currentTimeMillis();  
			JavaContains.testStringContains(strs);
			elapsedTime = System.currentTimeMillis() - start;
			times_str[i]=elapsedTime;
			
			start = System.currentTimeMillis();
			JavaContains.testStringMatch(strs);
			elapsedTime = System.currentTimeMillis() - start;
			times_regex[i]=elapsedTime;
		}
		
		System.out.println("---------------"+desc+" onlyMatchStr:"+onlyMatchStr+"---------------------------");
		System.out.println("Average run time in milli seconds of "+times+" times for String Contains is "+Arrays.stream(times_str).average());
		System.out.println("Average run time in milli seconds of "+times+" times for String match is "+Arrays.stream(times_regex).average());
		System.out.println("details of String Contains: "+Arrays.toString(times_str));
		System.out.println("details of String match: "+Arrays.toString(times_regex));
		System.out.println("---------------end---------------------------");
	}
	
//	@Test
	public void testPerformance1(boolean onlyMatchStr){
		RandomStringGenerator g=GenerateStrings.getGeneratorAlphaNumeric();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			Random random = new Random();
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				int index=random.nextInt(s.length());
				strs[i]=s.substring(0, index)+"abc"+s.substring(index);
			}
		}
		this.runPerf(strs, "strings of alpha numeric",this.TIMES,onlyMatchStr);
	}

//	@Test
	public void testPerformance2(boolean onlyMatchStr) {
		RandomStringGenerator g=GenerateStrings.getGeneratorASCII();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			Random random = new Random();
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				int index=random.nextInt(s.length());
				strs[i]=s.substring(0, index)+"abc"+s.substring(index);
			}
		}
		this.runPerf(strs, "strings of ASCII",this.TIMES,onlyMatchStr);
	}

//	@Test
	public void testPerformance3(boolean onlyMatchStr) {
		RandomStringGenerator g=GenerateStrings.getGeneratorUnicode();
		String[] strs=GenerateStrings.generateStrings(g,this.COUNT,this.MAX_STR_LEN);
		if(onlyMatchStr){
			Random random = new Random();
			for(int i=0;i<strs.length;i++){
				String s=strs[i];
				int index=random.nextInt(s.length());
				strs[i]=s.substring(0, index)+"abc"+s.substring(index);
			}
		}
		this.runPerf(strs, "strings of Unicode",this.TIMES,onlyMatchStr);
	}
}
