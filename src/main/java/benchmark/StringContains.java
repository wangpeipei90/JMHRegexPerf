package benchmark;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.ncsu.regex.perf3.Utils;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Pattern.compile(".*" + errorString + ".*", Pattern.DOTALL)
 * pattern.matcher(res.stdout).matches())
 * vs
 * res.stdout.contains(errorString)
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 50, time = 1)
public class StringContains {
	@Param({"errorstring"})
	private String regex;
	
	@Param({"error"})
	private String str;
	
	
	private Pattern pattern;
	
	@Setup(Level.Trial)
	public void check(){
		pattern = Pattern.compile(".*" + regex + ".*" , Pattern.DOTALL);
	}
	

	@Benchmark
	public void stringContains(Blackhole bh){
		boolean res = str.contains(regex);
		bh.consume(res);
	}
	
	@Benchmark
	public void regexMatches(Blackhole bh){
		boolean res = pattern.matcher(str).matches();
		bh.consume(res);
	}
	
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar benchmark.StringContains -rf csv -rff stringcontains.csv -o stringcontains.log
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff contains_error_iter50.csv -o log/contains_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(StringContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();
	}
}