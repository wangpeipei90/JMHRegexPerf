package benchmark;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * Pattern.compile(".*" + errorString + ".*", Pattern.DOTALL)
 * for(String s: string_dataset):
 * 	 pattern.matcher(s).matches())
 * vs
 * for(String s: string_dataset):
 *   s.contains(errorString)
 * @author pw
 *
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS) // TimeUnit.MICROSECONDS
@State(Scope.Benchmark)

@Fork(value = 5, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)

public class StringContainsDataset {
	@Param("http_0.1_0.json")
	private String json_filename;
	
	@Param("http")
	private String regex;
	
	private Pattern pattern;
	
	private List<String> DATA_FOR_TESTING;
	
	private int count_match = 0;
	
	@Setup(Level.Trial)
	public void check() throws FileNotFoundException{
		pattern = Pattern.compile(".*" + regex + ".*" , Pattern.DOTALL);
		DATA_FOR_TESTING = new ArrayList<String>(1000);
		JsonReader reader = new JsonReader(new FileReader(json_filename));
		JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
	    for (int i = 0; i < jsonArray.size(); i++) {
	    	JsonArray arr = jsonArray.get(i).getAsJsonArray();
	    	String str = arr.get(0).toString();
	    	int length = Integer.parseInt(arr.get(1).toString());
	    	String type = arr.get(2).toString();
	    	
	    	if(type.equals("M")) {
	    		count_match += 1;
	    	}
	    	if(str.length() == length) {
	    		DATA_FOR_TESTING.add(str);
	    	}
	    }
	    System.out.println("Among "+ jsonArray.size() +" strings, " + DATA_FOR_TESTING.size() + " are added, "+ count_match + " strings are matching strings.");
	}

	public static void main(String[] args) throws RunnerException, CommandLineOptionException {
		String csv_filename = args[args.length-4];
		String log_filename = args[args.length-3];
		String regex = args[args.length-2];
		String str = args[args.length-1];
		CommandLineOptions cmdOptions = new CommandLineOptions(Arrays.copyOfRange(args, 0, args.length-4));
		ChainedOptionsBuilder optBuilder = new OptionsBuilder()
				.parent(cmdOptions)
				.include(StringContainsDataset.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(false)
				.param("regex",regex)
				.param("json_filename", str)
				.resultFormat(ResultFormatType.CSV)
				.result(csv_filename)
				.output(log_filename)
				.shouldFailOnError(true);
		System.out.println("output file: "+((OptionsBuilder)optBuilder).getOutput());
		System.out.println("result file: "+((OptionsBuilder)optBuilder).getResult());
		new Runner(optBuilder.build()).run();
//		new Runner(new OptionsBuilder().include(StringContainsDataset.class.getSimpleName()).build()).run();
	}
	
//	@Benchmark
//	public void StringMatchMultipleStrs(Blackhole bh){
//		for(String s: DATA_FOR_TESTING){
//			bh.consume(s.matches(regex));	
//		}
//	}
	
	@Benchmark
	public void stringContains(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			boolean res = s.contains(regex);
			bh.consume(res);	
		}
	}
	
//	@Benchmark
//	public void RegexMatchMultipleStrsNoPrecompile(Blackhole bh){
//		for(String s: DATA_FOR_TESTING){
//			bh.consume(Pattern.matches(regex, s));	
//		}
//	}

	@Benchmark
	public void regexMatches(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			boolean res = pattern.matcher(s).matches();
			bh.consume(res);	
		}
	}
}
