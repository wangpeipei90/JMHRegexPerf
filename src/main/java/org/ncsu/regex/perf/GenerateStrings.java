package org.ncsu.regex.perf;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;

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
		
	public static List<String> createData(String generator_type,int nStrings, int str_len, boolean sameStrLen, boolean matchOnly) {				
		RandomStringGenerator g;
		switch(generator_type){
		case "AlphaNumeric": g=GenerateStrings.getGeneratorAlphaNumeric();break;
		case "ASCII": g=GenerateStrings.getGeneratorASCII();break;
		case "Unicode": g=GenerateStrings.getGeneratorUnicode();break;
		default: throw new RuntimeException("Not valid Generator type!! only AlphaNumeric, ASCII, and Unicode are allowed");
		}
		
		List<String> data = new ArrayList<>();
		if(sameStrLen){
			for (int i = 0; i < nStrings; i++) {
				data.add(g.generate(str_len));
			}
		}else{
			for (int i = 0; i < nStrings; i++) {
				data.add(g.generate(str_len));
			}
		}
		
		if(matchOnly){
			/**
			 * ########################TO FILL######################################
			 * Another way to generate data while all strings match the given regex
			 * #####################################################################
			 */
			Random random = new Random();
			for (int i = 0; i < nStrings; i++) {
				String s=g.generate(str_len);
				int index=random.nextInt(s.length());
				data.add(s.substring(0, index)+"abc"+s.substring(index));
			}
		}		
		return data;
	}
	
	public static void main(String[] args) throws IOException{
		System.out.println(args[0]);
		if(args.length<5){
			System.err.println("need arguments: generator type, number of strings, length of strings, if string lengths are same, if matches only!!");
		}
		String generator_type=args[0];
		int nStrings=Integer.valueOf(args[1]);
		int str_len=Integer.valueOf(args[2]);
		boolean sameStrLen=Boolean.valueOf(args[3]);
		boolean matchOnly=Boolean.valueOf(args[4]);
		List<String> data=createData(generator_type,nStrings,str_len,sameStrLen,matchOnly);
		
		String filename=generator_type+"_str"+nStrings+"_len"+str_len;
		if(sameStrLen)
			filename=filename+"_sameLen";
		else
			filename=filename+"_randomLen";
		if(matchOnly)
			filename=filename+"_matchOnlyStrs.txt";
		else
			filename=filename+"_genericStrs.txt";
		
		FileWriter writer = new FileWriter(filename); 
		for(String str: data) {
		  writer.write(str + System.lineSeparator());
		}
		writer.close();
		
	}
		

}
