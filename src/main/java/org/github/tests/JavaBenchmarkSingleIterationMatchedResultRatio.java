package org.github.tests;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.ncsu.regex.perf.StringUtils;

import org.apache.commons.text.RandomStringGenerator;
import org.ncsu.regex.perf.GenerateStrings;
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
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
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
@Warmup(iterations = 5, batchSize = 5000)
@Measurement(iterations = 1000, batchSize = 5000)

public class JavaBenchmarkSingleIterationMatchedResultRatio {
	@Param("c")
	private String regex;
	@Param("c")
	private String str;
	@Param("1")
	private int strLen;
	
	RandomStringGenerator g=GenerateStrings.getGeneratorAlphaNumeric();
	private String testString;
	private Pattern compiledRegex;
	
	@Setup(Level.Iteration)
	public void setup(){
		compiledRegex = Pattern.compile(regex);
		testString=g.generate(strLen);
		while(compiledRegex.matcher(testString).matches()){
			testString=g.generate(strLen);
		}
	}
	
	@TearDown(Level.Iteration)
	public void reset(){
		System.out.println(testString);
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JavaBenchmarkSingleIterationMatchedResultRatio.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
//				.warmupIterations(5)
//				.measurementIterations(1000)
//				.resultFormat(ResultFormatType.CSV)
//				.result(StringUtils.logDir + Instant.now().getEpochSecond()+".csv")
				.shouldDoGC(true)
				.build();

		new Runner(opt).run();
	}
	
	@Benchmark
	public void testRegexFilter(Blackhole bh){
		bh.consume(compiledRegex.matcher(testString).matches());	
	}
	
	@Benchmark
	public void testPrefixFilter(Blackhole bh){
		bh.consume(testString.startsWith(regex));	
	}
}
