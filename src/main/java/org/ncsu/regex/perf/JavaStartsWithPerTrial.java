package org.ncsu.regex.perf;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) 
@State(Scope.Benchmark)

@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5)
@Measurement(iterations = 500)
public class JavaStartsWithPerTrial {

	//@Param("AlphaNumeric_str10000_len20_randomLen_genericStrs.txt")
	@Param("input/startsWithPerIter_iter1000_batch500_genStr")
	private String filename;

	@Param("abc.*")
	private String regex;
	@Param("abc")
	private String str;
	
	private List<String> DATA_FOR_TESTING;
	private Pattern compiledRegex;
	
	@Setup(Level.Trial) //setup is done once when the benchmark code initializes
	public void setup() throws FileNotFoundException, IOException {
		DATA_FOR_TESTING = StringUtils.readData(filename);
		compiledRegex = Pattern.compile(regex);
	}

	@TearDown(Level.Trial)
	public void reset(){
		DATA_FOR_TESTING.clear();
	}
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf.JavaStartsWithPerTrial
		 * -f 1 -gc true -wi 5 -i 1000 -p regex="abc.*" -p str="abc" -p strLen=10
		 * -rf csv -rff startsWithPerTrial_iter500_str1000.csv -o log/startsWithPerTrial_iter500_str1000
		 */
		Options opt = new OptionsBuilder()
				.include(JavaStartsWithPerTrial.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
//				.warmupIterations(1)
//				.measurementIterations(3)		
//				.resultFormat(ResultFormatType.CSV)
//				.result(StringUtils.logDir + Instant.now().getEpochSecond()+".csv")
				.output(StringUtils.logDir + Instant.now().getEpochSecond())
				.shouldDoGC(false)
				.build();

		 new Runner(opt).run();
	}

	
	@Benchmark
	public void compiledRegexFullMatching(Blackhole bh){
		for(String testString:DATA_FOR_TESTING) {
			bh.consume(compiledRegex.matcher(testString).matches());	
		}
	}
	
	@Benchmark
	public void notCompiledRegexFullMatching(Blackhole bh){
		for(String testString:DATA_FOR_TESTING){
			bh.consume(Pattern.matches(regex,testString));	
		}
	}
	
	@Benchmark
	public void compiledRegexFind(Blackhole bh){
		for(String testString:DATA_FOR_TESTING){
			bh.consume(compiledRegex.matcher(testString).find());	
		}
	}
	
	@Benchmark
	public void stringFullMatching(Blackhole bh){
		for(String testString:DATA_FOR_TESTING){
			bh.consume(testString.matches(regex));		
		}
	}
	
	@Benchmark
	public void stringStartsWith(Blackhole bh){
		for(String testString:DATA_FOR_TESTING){
			bh.consume(testString.startsWith(str));	
		}
	}
	
	@Benchmark
	public void stringIndexOf(Blackhole bh){
		for(String testString:DATA_FOR_TESTING){
			bh.consume(testString.indexOf(str));	
		}
	}
}
