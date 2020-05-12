package org.ncsu.regex.perf2;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10,time=1)
@Measurement(iterations = 50,time=1)


public class JavaContains {
	@Param({".*error.*"})
	private String regex;
	
	@Param({"error"})
	private String str;
	
	@Param({"abcccerrordefg"})
	private String testString;
	
	@Param("true")
	private boolean expectation;
	
//	boolean result;
	
	@Benchmark
    public boolean wellHelloThere() {
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
	public boolean stringContains(){
		return testString.contains(str);	
//		result=testString.contains(str);
		//assert result==expectation: "Wrong String Ops of Regex matching";
	}
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf2.JavaContains
		 * -f 1 -gc true -wi 10 -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff contains_error_iter50.csv -o log/contains_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(JavaContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.measurementTime(TimeValue.milliseconds(100))
				.build();

		new Runner(opt).run();
	}
}
