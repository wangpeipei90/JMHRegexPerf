/***
 * this is for testing performance between regex and startsWith with random generated strings
 */
package org.ncsu.regex.perf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringUtils {
	public static String directory;
	public static String logDir;
	static{
		String os_version=System.getProperty("os.name");
		if(os_version.startsWith("Mac OS")){
			directory="/Users/peipei/workspace/JMHRegexPerf/input/";
			logDir="/Users/peipei/workspace/JMHRegexPerf/log/";
		}else{
			directory="/home/pwang7/JMHRegexPerf/input/";
			logDir="/home/pwang7/JMHRegexPerf/log/";
		}
	}
	public static List<String> readData(String filename) throws FileNotFoundException, IOException{
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(directory+filename)))) {
		    list = br.lines().collect(Collectors.toList());    
		}
		return list;
	}
	
	
	public static float getMatchRatioStartsWithPattern(List<String> DATA_FOR_TESTING, String regex) {
		float total_string=DATA_FOR_TESTING.size();
		int matched=0;
		Pattern compiledRegex=Pattern.compile(regex);
		for(String testString:DATA_FOR_TESTING) {
			if(compiledRegex.matcher(testString).matches()) {
				matched++;
			}
		}
		return matched*100/total_string;			
	}
	
	public static float getMatchRatioStartsWithString(List<String> DATA_FOR_TESTING, String str) {
		float total_string=DATA_FOR_TESTING.size();
		int matched=0;
		
		for(String testString:DATA_FOR_TESTING) {
			if(testString.startsWith(str)) {
				matched++;
			}
		}
		return matched*100/total_string;			
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String method_name=args[0];
		String file_name=args[1];
		List<String> data=StringUtils.readData(file_name);
		Method m;
		try {
			m = StringUtils.class.getMethod(method_name, List.class, String.class);
			System.out.println(m.invoke(data,args[2]));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
