package org.ncsu.regex.perf;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;

import com.mifmif.common.regex.Generex;

public class GenerateStrings {

	public static RandomStringGenerator getGeneratorAlphaNumeric(){
		// using only the letters a-zA-Z0-9
		 return new RandomStringGenerator.Builder()
				 .selectFrom("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray())
				 .usingRandom(new SecureRandom()::nextInt) // .usingRandom(rng::nextInt) // uses Java 8 syntax ??
				 .build();
		     //.withinRange('a', 'z').build();
	}
	public static RandomStringGenerator getGeneratorASCII(){
		//using code points 0-127, ASCII characters
		return new RandomStringGenerator.Builder()
				 .withinRange(0, 127)
				 .usingRandom(new SecureRandom()::nextInt) // .usingRandom(rng::nextInt) // uses Java 8 syntax ??
				 .build();
	}
	
	public static RandomStringGenerator getGeneratorUnicode(){
		//using all unicode characters
		return new RandomStringGenerator.Builder()
				 .usingRandom(new SecureRandom()::nextInt)
				 .build();
	}
		
	
	public static List<String> generateMatchedStrings(String regex, int min_len, int max_len, int size) {
		List<String> data = new ArrayList<>();
		Generex generex = new Generex(regex);
		System.out.println("is infinite: "+generex.isInfinite());
		data=generex.getMatchedStrings(size);
		System.out.println(data);
		return data;
		
//		String str=generex.random(min_len, max_len);
//		for(int i=0;i<size;i++) {
//			System.out.println(regex+" "+min_len+" "+max_len+" "+str+" "+str.length());
//			data.add(str);
//			
//			str=generex.random(min_len, max_len);
//		}
//		return data;
	}
//	public static  createData(String generator_type,int nStrings, int str_len, boolean sameStrLen, boolean matchOnly) {				
//		RandomStringGenerator g;
//		switch(generator_type){
//		case "AlphaNumeric": g=GenerateStrings.getGeneratorAlphaNumeric();break;
//		case "ASCII": g=GenerateStrings.getGeneratorASCII();break;
//		case "Unicode": g=GenerateStrings.getGeneratorUnicode();break;
//		default: throw new RuntimeException("Not valid Generator type!! only AlphaNumeric, ASCII, and Unicode are allowed");
//		}
//		
//		
//		if(sameStrLen){
//			for (int i = 0; i < nStrings; i++) {
//				data.add(g.generate(str_len));
//			}
//		}else{
//			for (int i = 0; i < nStrings; i++) {
//				data.add(g.generate(str_len));
//			}
//		}
//		
//		if(matchOnly){
//			/**
//			 * ########################TO FILL######################################
//			 * Another way to generate data while all strings match the given regex
//			 * #####################################################################
//			 */
//			Random random = new Random();
//			for (int i = 0; i < nStrings; i++) {
//				String s=g.generate(str_len);
//				int index=random.nextInt(s.length());
//				data.add(s.substring(0, index)+"abc"+s.substring(index));
//			}
//		}		
//		return data;
//	}
	
	public static void main(String[] args) throws IOException{
		GenerateStrings.generateMatchedStrings("abc.*", 5, 10, 10);
		
	}
}
