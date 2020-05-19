package org.ncsu.regex.perf2;
import java.util.Collection;
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
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10)
@Measurement(iterations = 50)


public class JavaContains {
	@Param({".*error.*"})
	private String regex;
	
	@Param({"error"})
	private String str;
	
	@Param({"abcccerrordefg"})
	private String testString;
	
	@Param("true")
	private boolean expectation;
	
	private Pattern p;
	@Setup(Level.Trial)
	public void check(){
		p=Pattern.compile(regex);
	}
	
//	boolean result;
	
	@Benchmark
    public boolean baselineOverhead() {
		return expectation;
        // this method was intentionally left blank to measure the infrastructure overheads
		//assert result==expectation: "Wrong String Ops of Regex matching";
    }
	
	@Benchmark
	public boolean notCompiledRegexFullMatching(){
		return Pattern.matches(regex,testString);
		//result=Pattern.matches(regex,testString);
		//assert result==expectation: "Wrong String Ops of Regex matching";
	}
	
	@Benchmark
	public boolean compiledRegexFullMatching(){
		return p.matcher(testString).matches();
		//result=Pattern.matches(regex,testString);
		//assert result==expectation: "Wrong String Ops of Regex matching";
	}
	
	@Benchmark
	public boolean stringContains(){
		return testString.contains(str);	
//		result=testString.contains(str);
		//assert result==expectation: "Wrong String Ops of Regex matching";
	}
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf2.JavaContains
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff contains_error_iter50.csv -o log/contains_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(JavaContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.warmupTime(TimeValue.milliseconds(10))
				.measurementTime(TimeValue.milliseconds(10))
				.build();

//		new Runner(opt).run();
		Collection<RunResult> res=new Runner(opt).run();
		for(RunResult run_res:res) {
			BenchmarkResult bench_res=run_res.getAggregatedResult();
//			Collection<BenchmarkResult> data=run_res.getBenchmarkResults();
			System.out.println(bench_res.getPrimaryResult());
		}
	}
}
