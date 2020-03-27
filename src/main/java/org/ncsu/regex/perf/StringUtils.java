package org.ncsu.regex.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static String directory;
	public static String logDir;
	static{
		String os_version=System.getProperty("os.name");
		if(os_version.startsWith("Mac OS")){
			directory="/Users/peipei/workspace/JMHRegexPerf/generatedStrs/";
			logDir="/Users/peipei/workspace/JMHRegexPerf/log/";
		}else{
			directory="/home/pwang7/JMHRegexPerf/generatedStrs/";
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
