package org.ncsu.regex.perf;
import java.security.SecureRandom;
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
	
//	public static String[] generateStrings(RandomStringGenerator g, int count, int maxLen){
//		String[] strs=new String[count];
//		for(int i=0;i<count;i++){
//			strs[i]=g.generate(1,maxLen);
//		}
//		return strs;
//	}

}
