package org.ncsu.regex.perf;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.text.RandomStringGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
//@BenchmarkMode({Mode.AverageTime,Mode.SingleShotTime,Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS) // TimeUnit.MICROSECONDS MILLISECONDS
@State(Scope.Benchmark)
public class JavaContainsBenchmark {
	
	@Param({ "10", "100", "1000"})//, "10000" })
	private int nStrings;  //size of the dataset
	@Param({"AlphaNumeric","ASCII"})//,"Unicode"})
	private String generator_type; // type of generator
	
	@Param({"1","5","10","15","20","50"})//,"200","1000"})
	private int str_len; //the fixed size of generated string
//	@Param({"100","500","1000","5000","10000","50000"})
//	private int max_str_len; //the max size of generated string with varied length
	
	private List<String> DATA_FOR_TESTING;
	
	@Setup(Level.Trial) //setup is done once when the benchmark code initializes
//	@Setup(Level.Iteration) //setup is done before each iteration of the benchmark
//	@Setup(Level.Invocation) //setup is done before each time the test method is executed
	public void setup() {
		DATA_FOR_TESTING = createData(false);
	}

	@TearDown(Level.Trial)
	public void reset(){
		DATA_FOR_TESTING.clear();
	}
//	@Param({"1","3","5","20"})
//	private int warmups;	
////	@Param({"5","10","20","50"})
//	private int measurements;
	
//	@Param({"1","2","4"})
//	private static int threads;
//	@Param({"1","2","4"})
//	private static int forks;
	
	
	public static void main(String[] args) throws RunnerException {
		int warmups=Integer.valueOf(args[0]);
		int measurements=Integer.valueOf(args[1]);
		System.out.println(warmups+""+measurements);
		Options opt = new OptionsBuilder()
				.include(JavaContainsBenchmark.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
//				.output("/home/peipei/workspace/first-benchmark/log/" + Instant.now().getEpochSecond())
//				.warmupIterations(warmups)
//				.measurementIterations(measurements)
//				.shouldDoGC(false)
				.build();

		 new Runner(opt).run();
	}
	
	@Benchmark
	public void StringMatchMultipleStrs(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(s.matches(".*abc.*"));	
		}
	}
	
	@Benchmark
	public void StringContainsMultipleStrs(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(s.contains("abc"));	
		}
	}
	
	@Benchmark
	public void RegexMatchMultipleStrsNoPrecompile(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(Pattern.matches(".*abc.*", s));	
		}
	}

	@Benchmark
	public void RegexMatchMultipleStrsPrecompiled(Blackhole bh){
		Pattern p=Pattern.compile(".*abc.*");
		for(String s: DATA_FOR_TESTING){
			bh.consume(p.matcher(s).matches());	
		}
	}
	
	private List<String> createData(boolean matchOnly) {				
		RandomStringGenerator g;
		switch(generator_type){
		case "AlphaNumeric": g=GenerateStrings.getGeneratorAlphaNumeric();break;
		case "ASCII": g=GenerateStrings.getGeneratorASCII();break;
		case "Unicode": g=GenerateStrings.getGeneratorUnicode();break;
		default: throw new RuntimeException("Not valid Generator type!! only AlphaNumeric, ASCII, and Unicode are allowed");
		}
		
		List<String> data = new ArrayList<>();
		for (int i = 0; i < nStrings; i++) {
			data.add(g.generate(str_len));
//			data.add(g.generate(1,max_str_len);
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
}
