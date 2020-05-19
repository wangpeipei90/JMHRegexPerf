package org.ncsu.regex.perf3;
import java.util.Collection;
import java.util.List;
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
@Warmup(iterations = 10,batchSize=10)
@Measurement(iterations = 100,batchSize=10)
public class RegexPreCompiledMethod {
	@Param({".*error.*"})
	private String regex;
	
	@Param({"error"})
	private String str;
	
//	@Param({"abcccerrordefg"})
//	private String testString;
	
	@Param("test3.csv")
	private String filename;
	
	@Param("true")
	private boolean expectation;
	
	private static List<String> DATA_FOR_TESTING;
	private Pattern p;
	
	@Setup(Level.Trial)
	public void check(){
		p=Pattern.compile(regex);
		DATA_FOR_TESTING = Utils.readData(filename, expectation,10);
	}
	

//	boolean result;
	
	@State(Scope.Thread)
    public static class StringState {
        public String testString;
        private int cnt=0;
        
        @Setup(Level.Iteration)
    	public void next() {
    		testString=DATA_FOR_TESTING.get(cnt++);
    		System.out.println(testString);
    	}
    }
	
	@Benchmark
	public boolean compiledRegexFullMatching(StringState state){
		return p.matcher(state.testString).matches();
	}
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar org.ncsu.regex.perf2.JavaContains
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff contains_error_iter50.csv -o log/contains_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(RegexPreCompiledMethod.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();
	}
}
