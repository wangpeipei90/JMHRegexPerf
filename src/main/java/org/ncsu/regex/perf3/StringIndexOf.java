package org.ncsu.regex.perf3;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10,batchSize=10)
@Measurement(iterations = 100,batchSize=10)
public class StringIndexOf {
	@Param("abc.*")
	private String regex;
	@Param("abc")
	private String str;

	
	@Param("test3.csv")
	private String filename;
	
	@Param("0")
	private int pos;
	
	private static List<String> DATA_FOR_TESTING;
	private Pattern p;
	
	@Setup(Level.Trial)
	public void check(){
		p=Pattern.compile(regex);
		DATA_FOR_TESTING = Utils.readData(filename, pos,10);
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
	public int stringStartsWith(StringState state){
		return state.testString.indexOf(str);	
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
				.include(StringIndexOf.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();
	}
}