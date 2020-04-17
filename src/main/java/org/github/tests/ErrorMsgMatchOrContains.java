package org.github.tests;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.ncsu.regex.perf.StringUtils;
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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) 
@State(Scope.Benchmark)

@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, batchSize = 50)
@Measurement(iterations = 100, batchSize = 50)

public class ErrorMsgMatchOrContains {
	@Param({"error"})
	private String errorString;
	
	@Param({"shortError.log","cloud-intergration-test-failure.log"})
	private String fileName;
	
	private String testString;
	private Pattern compiledRegex;
	
	@Setup(Level.Trial)
	public void setup() throws FileNotFoundException, IOException{
		testString = new String(Files.readAllBytes(Paths.get(StringUtils.directory+fileName)));
		compiledRegex=Pattern.compile(".*" + errorString + ".*", Pattern.DOTALL);
	}
	
	@TearDown(Level.Trial)
	public void reset(){
		System.out.println(fileName);
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JavaBenchmarkSingleIteration.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
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
	public void testPrecompiledRegexMatches(Blackhole bh){
		bh.consume(compiledRegex.matcher(testString).matches());	
	}
	
	@Benchmark
	public void testStringSplit(Blackhole bh){
		bh.consume(testString.contains(errorString));	
	}
	
}
