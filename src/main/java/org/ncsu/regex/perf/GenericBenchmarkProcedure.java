package org.ncsu.regex.perf;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


public class GenericBenchmarkProcedure {
	private static void readJSONConf(String conf_file) {
		JSONParser jsonParser = new JSONParser();
	    try (FileReader reader = new FileReader(conf_file))
	    {
	        //Read JSON file
	        Object obj = jsonParser.parse(reader);
	        JSONArray configs = (JSONArray) obj;
	        System.out.println(configs.size());
	        configs.forEach( conf -> parseConfigObject( (JSONObject) conf ) );
//	        JSONObject configs=(JSONObject)obj;
//	        System.out.println(configs.get("benchmark"));
	        System.out.println(configs);
	        
//	        OptionsBuilder optBuild=new OptionsBuilder();
	    } catch (FileNotFoundException|ParseException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
//	    cmd=["java -jar target/regexbenchmarks.jar"]
//	    	    cmd.append(data["benchmark"])
//	    	    if "jvmArgs" in data:
//	    	        cmd.append("-jvmArgs "+"'"+data["jvmArgs"]+"'")
//	    	     -f 1 -wi 2 -i 5 >log/log_JavaContains_Fork1_warmup2_iter5.log"
//	    	    configs=[]
	}
	
	private static void parseConfigObject(JSONObject conf) 
    {
        //Get employee object within list
        JSONObject employeeObject = (JSONObject) conf.get("benchma");
         
        //Get employee first name
        String firstName = (String) employeeObject.get("firstName");    
        System.out.println(firstName);
         
        //Get employee last name
        String lastName = (String) employeeObject.get("lastName");  
        System.out.println(lastName);
         
        //Get employee website name
        String website = (String) employeeObject.get("website");    
        System.out.println(website);
    }
	public static void main(String[] args) throws RunnerException {
		// TODO Auto-generated method stub
		System.out.println(args.length);
		System.out.println(args[0]);
		String conf_file=args[0];
		readJSONConf(conf_file);
		
//		Options opt = new OptionsBuilder()
//				.include(JavaContainsBenchmark.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
////				.forks(1)
////				.output("/home/peipei/workspace/first-benchmark/log/" + Instant.now().getEpochSecond())
////				.warmupIterations(warmups)
////				.measurementIterations(measurements)
////				.shouldDoGC(false)
//				.build();
//
//		 new Runner(opt).run();
		
	}

}
