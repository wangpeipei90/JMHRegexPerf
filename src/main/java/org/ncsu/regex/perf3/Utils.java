package org.ncsu.regex.perf3;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.RandomStringGenerator;
import org.ncsu.regex.perf.GenerateStrings;

enum FileType {
	CSV("csv");
	
	private String suffix;
	private FileType(String suffix){
		this.suffix=suffix;
	}
}

public class Utils {

	private static String dir_file;
	static {
		String os_version = System.getProperty("os.name");
		if (os_version.startsWith("Windows")) {
			dir_file = "D:\\workspace_java\\JMHRegexPerf\\";
		} else if (os_version.startsWith("Mac OS")) {
			dir_file = "/Users/peipei/workspace/JMHRegexPerf/";
		} else {
			dir_file = "/home/pwang7/JMHRegexPerf/";
		}
	}

	public static List<String> readData(String filename, boolean isMatch, int warmupIterations){
		List<String> data=new ArrayList<>();
		RandomStringGenerator g=GenerateStrings.getGeneratorAlphaNumeric();//GenerateStrings.getGeneratorASCII()
		String testString=g.generate(1,1024);
		while(warmupIterations-->0) {
			data.add(testString);
			testString=g.generate(1,1024);
		}
//		System.out.println(data.size());
		/***
		 * Dynamically generate matching or non-matching strings for warmup iterations
		 * if(!isMatch) {
		 * }
		 */
		FileType type= FileType.valueOf(filename.split("\\.")[1].toUpperCase());
		readData(filename, type, isMatch,data);
//		System.out.print(data.size());
		return data;
	}
	
	static void readData(String filename, FileType type, boolean isMatch,List<String> data) {
		switch (type) {
		case CSV:
			readCSVFile(filename, data);break;
		}
	}

	static void readCSVFile(String filename,List<String> data) {
		Reader in;
		try {
			in = new FileReader(dir_file + filename);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				data.add(record.get("string"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String os_version = System.getProperty("os.name");
//		System.out.println(os_version);
		
		List<String> data =readData("test3.csv",true,10);
		for(String s:data) {
			System.out.println(s);
		}
	}

}
