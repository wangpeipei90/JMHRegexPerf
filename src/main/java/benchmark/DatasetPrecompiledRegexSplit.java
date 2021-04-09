package benchmark;
import java.io.FileReader;
import java.io.IOException;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


/**
 * return Arrays.asList(trimmed.split("\\s*,\\s*", -1));
 * vs
 * private static final Pattern COMMA_WITH_WHITESPACE = Pattern.compile("\\s*,\\s*");
 * return Arrays.asList(COMMA_WITH_WHITESPACE.split(trimmed, -1));
 * 
 * https://github.com/apache/kafka/pull/5168
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 5, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
public class DatasetPrecompiledRegexSplit {
	@Param({"\\s*,\\s*"})
	private String regex;
	
	@Param("http_0.1_0.json")
	private String json_filename;
	
	private List<String> DATA_FOR_TESTING;
	private int count_match = 0;
	
	@Setup(Level.Trial)
	public void check() throws IOException{
		DATA_FOR_TESTING = new ArrayList<String>(1000);
		JsonReader reader = new JsonReader(new FileReader(json_filename));
		JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
	    for (int i = 0; i < jsonArray.size(); i++) {
	    	JsonArray arr = jsonArray.get(i).getAsJsonArray();
	    	String str = arr.get(0).getAsString();
	    	int length = Integer.parseInt(arr.get(1).toString());
	    	String type = arr.get(2).getAsString();
	    	
	    	if(str.length() != length) {
	    		System.out.println(i + "th " + str + " str_length: " + str.length() + " " + length + " " + type);
	    		throw new IOException("string length is not correct");	
	    	}
	    	if(type.equals("M")) {
	    		count_match += 1;
	    	}
    		if(str.length() == length) {
	    		DATA_FOR_TESTING.add(str);
    		}
	    }
	    System.out.println("Among "+ jsonArray.size() +" strings, " + DATA_FOR_TESTING.size() + " are added, "+ count_match + " strings are matching strings.");
	}

	@Benchmark
	public void stringSplit(Blackhole bh){
		for(String trimmed: DATA_FOR_TESTING){
			List<String> res = Arrays.asList(trimmed.split("\\s*,\\s*", -1));
			bh.consume(res);
		}
	}
	
	@Benchmark
	public void regexSplit(Blackhole bh){
		Pattern COMMA_WITH_WHITESPACE = Pattern.compile("\\s*,\\s*");
		for(String trimmed: DATA_FOR_TESTING){
			List<String> res = Arrays.asList(COMMA_WITH_WHITESPACE.split(trimmed, -1));
			bh.consume(res);
		}
	}
	
	public static void main(String[] args) throws CommandLineOptionException, RunnerException {
		String csv_filename = args[args.length-4];
		String log_filename = args[args.length-3];
		String split_regex = args[args.length-2];
		String str_val = args[args.length-1];
		CommandLineOptions cmdOptions = new CommandLineOptions(Arrays.copyOfRange(args, 0, args.length-4));
		ChainedOptionsBuilder optBuilder = new OptionsBuilder()
				.parent(cmdOptions)
				.include(DatasetPrecompiledRegexSplit.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(false)
				.param("regex",split_regex)
				.param("trimmed", str_val)
				.resultFormat(ResultFormatType.CSV)
				.result(csv_filename)
				.output(log_filename)
				.shouldFailOnError(true);
		System.out.println("output file: "+((OptionsBuilder)optBuilder).getOutput());
		System.out.println("result file: "+((OptionsBuilder)optBuilder).getResult());
		new Runner(optBuilder.build()).run();
	}

}
