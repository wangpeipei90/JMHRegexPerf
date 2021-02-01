package benchmark;

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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


/**
 * this.compiledRegex = Pattern.compile(pattern);
 * compiledRegex.matcher(val).matches()
 * vs
 * val.startsWith(prefix)
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 50, time = 1)
public class StringStartsWith {

	@Param({"error"})
	private String prefix;
	
	@Param({"errorstring"})
	private String val;
	
	
	private Pattern pattern;
	
	@Setup(Level.Trial)
	public void check(){
		pattern = Pattern.compile(prefix + ".*"); // extend to include more cases
	}
	

	@Benchmark
	public void stringPrefix(Blackhole bh){
		boolean res = val.startsWith(prefix);
		bh.consume(res);
	}
	
	@Benchmark
	public void regexMatches(Blackhole bh){
		boolean res = pattern.matcher(val).matches();
		bh.consume(res);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar benchmark.StringStartsWith -rf csv -rff stringstartswith.csv -o stringstartswith.log
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff startswith_error_iter50.csv -o log/startswith_error_iter50.log
		 */
//		Options opt = new OptionsBuilder()
//				.include(StringStartsWith.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.shouldDoGC(true)
//				.build();
//		Pattern p = Pattern.compile(".*error.csv");
//		Pattern p2 = Pattern.compile("\\.*error.csv");
//		Pattern p3 = Pattern.compile("\\.*error\\.csv");
//		Pattern[] patterns = {p,p2,p3};
//		String[] strings = {"error.csv","derrordcsv",".errordcsv",".error.csv"};
//		for(Pattern pattern: patterns) {
//			for(String s: strings) {
//				System.out.println(pattern + " " + s + " " + pattern.matcher(s).matches());
//			}
//			System.out.println();
//		}

	}

}
