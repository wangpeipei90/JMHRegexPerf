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
 * before: with capturing groups
 * vs
 * after: without capturing groups
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 50, time = 1)
public class NonCapturingGroup {
	@Param({"^(https?|ftp):\\/\\/" +
            "(([a-z0-9$_\\.\\+!\\*\\'\\(\\),;\\?&=\\-]|%[0-9a-f]{2})+" +
            "(:([a-z0-9$_\\.\\+!\\*\\'\\(\\),;\\?&=\\-]|%[0-9a-f]{2})+)?" +
            "@)?(#?" +
            ")((([a-z0-9]\\.|[a-z0-9][a-z0-9-]*[a-z0-9]\\.)*" +
            "[a-z][a-z0-9-]*[a-z0-9]" +
            "|((\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
            "(\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])" +
            ")(:\\d+)?" +
            ")(((\\/([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=\\-]|%[0-9a-f]{2})*)*" +
            "(\\?([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=\\-\\/\\:]|%[0-9a-f]{2})*)" +
            "?)?)?" +
            "(#([a-z0-9$_\\.\\+!\\*\\'\\(\\),;:@&=\\-]|%[0-9a-f]{2})*)?" +
            "$", 
            "^(?:https?|ftp):\\/\\/" +
            "(?:(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+" +
            "(?::(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+)?" +
            "@)?#?" +
            "(?:(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*" +
            "[a-z][a-z0-9-]*[a-z0-9]" +
            "|(?:(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}" +
            "(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])" +
            ")(?::\\d+)?" +
            ")(?:(?:\\/(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)*" +
            "(?:\\?(?:[a-z0-9$_.+!*'(),;:@&=\\-\\/:]|%[0-9a-f]{2})*)?)?" +
            "(?:#(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)?" +
            "$"})
	private String regex;
	
	@Param({"ftp://#4.180.100.160", "ftp://411180.100.160"})
	private String val;
	
	private Pattern pattern;
	
	@Setup(Level.Trial)
	public void check(){
		pattern = Pattern.compile(regex); // extend to include more cases
		System.out.println(pattern.pattern());
		System.out.println(val);
		System.out.println(pattern.matcher(val).matches());
	}
	
	@Benchmark
	public void compile(Blackhole bh){
		Pattern p = Pattern.compile(regex);
		bh.consume(p);
	}
	

	@Benchmark
	public void regexMatchesPrecompiled(Blackhole bh){
		boolean res = pattern.matcher(val).matches();
		bh.consume(res);
	}
	
	@Benchmark
	public void regexMatches(Blackhole bh){
		boolean res = Pattern.matches(regex, val);
		bh.consume(res);
	}
	public static void main(String[] args) throws RunnerException {
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar benchmark.NonCapturingGroup -rf csv -rff reCapturingGroup.csv -o reCapturingGroup.log
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff reCapturingGroup_error_iter50.csv -o log/reCapturingGroup_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(StringContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();
		System.out.println(opt.getParameter("regex"));
		System.out.println(opt.getParameter("val"));
	}
}
