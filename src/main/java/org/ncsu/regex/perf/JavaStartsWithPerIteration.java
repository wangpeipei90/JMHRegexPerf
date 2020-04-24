package org.ncsu.regex.perf;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.text.RandomStringGenerator;
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
@Warmup(iterations = 5, batchSize = 500)
@Measurement(iterations = 1000, batchSize = 500)

public class JavaStartsWithPerIteration {
	
	@Param("abc.*")
	private String regex;
	@Param("abc")
	private String str;
	@Param("10")
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
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf.JavaStartsWithPerIteration 
		 * -f 1 -gc true -wi 5 -i 1000 -p regex="abc.*" -p str="abc" -p strLen=10
		 * -rf csv -rff startsWithPerIter_iter1000_batch500.csv -o log/startsWithPerIter_iter1000_batch500.log >input/startsWithPerIter_iter1000_batch500_genStr
		 */
		Options opt = new OptionsBuilder()
				.include(JavaStartsWithPerIteration.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
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
	public void compiledRegexFullMatching(Blackhole bh){
		bh.consume(compiledRegex.matcher(testString).matches());	
	}
	
	@Benchmark
	public void notCompiledRegexFullMatching(Blackhole bh){
		bh.consume(Pattern.matches(regex,testString));	
	}
	
	@Benchmark
	public void compiledRegexFind(Blackhole bh){
		bh.consume(compiledRegex.matcher(testString).find());	
	}
	
	@Benchmark
	public void stringFullMatching(Blackhole bh){
		bh.consume(testString.matches(regex));		
	}
	
	@Benchmark
	public void stringStartsWith(Blackhole bh){
		bh.consume(testString.startsWith(str));	
	}
	
	@Benchmark
	public void stringIndexOf(Blackhole bh){
		bh.consume(testString.indexOf(str));	
	}
}
