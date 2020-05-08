package org.ncsu.regex.perf2;
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
@Measurement(iterations = 10)

public class JavaIndexOf{

	@Param("abc.*")
	private String regex;
	@Param("abc")
	private String str;
	
	@Param("abcccc")
	private String testString;
	@Param("0")
	private int expectation;
	
	
	int result;
	
	
	@TearDown
	public void check(){
		assert result==expectation: "Wrong String Ops of Regex matching";
	}
	
	
	
	@Benchmark
	public void stringIndexOf(Blackhole bh){
		result=testString.indexOf(str);	
	}
	
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf.JavaStartsWithPerTrial
		 * -f 1 -gc true -wi 5 -i 1000 -p regex="abc.*" -p str="abc" -p strLen=10
		 * -rf csv -rff startsWithPerTrial_iter500_str1000.csv -o log/startsWithPerTrial_iter500_str1000
		 */
		Options opt = new OptionsBuilder()
				.include(JavaIndexOf.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();

		 new Runner(opt).run();
	}

	

}
