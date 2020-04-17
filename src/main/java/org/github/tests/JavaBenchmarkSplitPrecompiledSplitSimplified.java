package org.github.tests;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.text.RandomStringGenerator;
import org.github.tests.utils.StringUtils;
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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 1) performance single character
 * 2) performance >3 character
 * 3) performance 2 characters, special 2 characters
 * 
 * @author peipei
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) 
@State(Scope.Benchmark)

@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, batchSize = 500)
@Measurement(iterations = 1000, batchSize = 500)

public class JavaBenchmarkSplitPrecompiledSplitSimplified {
	@Param({",",";","="," ","/","&","\\."})
	private String regex;
	@Param("1")
	private int strLen;
	
	RandomStringGenerator g=GenerateStrings.getGeneratorAlphaNumeric();
	private String testString;
	private Pattern compiledRegex;
	
	@Setup(Level.Iteration)
	public void setup(){
		testString=g.generate(strLen);
		compiledRegex = Pattern.compile(regex);
	}
	
	@TearDown(Level.Iteration)
	public void reset(){
		System.out.println(testString);
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JavaBenchmarkSingleIteration.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
//				.warmupIterations(5)
//				.measurementIterations(1000)
//				.resultFormat(ResultFormatType.CSV)
//				.result(StringUtils.logDir + Instant.now().getEpochSecond()+".csv")
				.shouldDoGC(false)
				.build();

		new Runner(opt).run();
	}
	
	@Benchmark
	public void testPrecompiledRegexSplit(Blackhole bh){
		bh.consume(compiledRegex.split(testString));	
	}
	
	@Benchmark
	public void testStringSplit(Blackhole bh){
		bh.consume(testString.split(regex));	
	}
}
