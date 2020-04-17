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
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.Statistics;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) 
@State(Scope.Benchmark)

@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 1)
@Measurement(iterations = 3)

public class JavaContainsBenchmark {
	@Param("AlphaNumeric_str10000_len20_randomLen_genericStrs.txt")
	private String filename;
	
	@Param(".*abc.*")
	private String regex;
	@Param("abc")
	private String strOP;
	
	private List<String> DATA_FOR_TESTING;
	
	@Setup(Level.Trial) //setup is done once when the benchmark code initializes
	public void setup() throws FileNotFoundException, IOException {
		DATA_FOR_TESTING = StringUtils.readData(filename);
		System.out.println(filename);
	}

	@TearDown(Level.Trial)
	public void reset(){
		DATA_FOR_TESTING.clear();
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JavaContainsBenchmark.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.forks(1)
				.warmupIterations(1)
				.measurementIterations(3)
				.resultFormat(ResultFormatType.CSV)
				.result(StringUtils.logDir + Instant.now().getEpochSecond()+".csv")
//				.output(StringUtils.logDir + Instant.now().getEpochSecond())
//				.shouldDoGC(false)
				.build();

		// new Runner(opt).run();
		Collection<RunResult> results = new Runner(opt).run();
		System.out.println("-----customized results---");
		for (RunResult result : results) {
			System.out.println(result.getParams());
			System.out.println(result.getAggregatedResult());
			for(BenchmarkResult br: result.getBenchmarkResults()){
				for(IterationResult ir: br.getIterationResults()){
					System.out.println(ir.getPrimaryResult());
				}
			}
		}
	}
	
	@Benchmark
	public void StringMatchMultipleStrs(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(s.matches(regex));	
		}
	}
	
	@Benchmark
	public void StringContainsMultipleStrs(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(s.contains(strOP));	
		}
	}
	
	@Benchmark
	public void RegexMatchMultipleStrsNoPrecompile(Blackhole bh){
		for(String s: DATA_FOR_TESTING){
			bh.consume(Pattern.matches(regex, s));	
		}
	}

	@Benchmark
	public void RegexMatchMultipleStrsPrecompiled(Blackhole bh){
		Pattern p=Pattern.compile(regex);
		for(String s: DATA_FOR_TESTING){
			bh.consume(p.matcher(s).matches());	
		}
	}
}