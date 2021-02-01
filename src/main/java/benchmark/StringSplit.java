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
 * final String[] values = StringUtils.split(packagesAsCsv, ",");
 * static {        
        String[] patterns = {"/", " ", ":", ",", ";", "=", "\\.", "\\+"};        
        for (String p : patterns) {        
            PATTERN_MAP.put(p, Pattern.compile(p));        
        }        
    }
 * StringUtils.split(entry.getValue(), " "))
 * vs
 * final String[] values = packagesAsCsv.split(",");
 * String[] patterns = {"/", " ", ":", ",", ";", "=", "\\.", "\\+"};
 * Pattern p = PATTERN_MAP.computeIfAbsent(regex, key -> Pattern.compile(key));
 * entry.getValue().split(" ")
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 50, time = 1)
public class StringSplit {
	@Param({"error"})
	private String separator;
	
	@Param({"errorstring"})
	private String val;
	
	
	private Pattern pattern;
	
	@Setup(Level.Trial)
	public void check(){
		pattern = Pattern.compile(separator); // extend to include more cases
	}
	

	@Benchmark
	public void stringSplit(Blackhole bh){
		String[] res = val.split(separator);
		bh.consume(res);
	}
	
	@Benchmark
	public void regexSplit(Blackhole bh){
		String[] res = pattern.split(val);
		bh.consume(res);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * commands:
		 * java -jar target/regexbenchmarks.jar benchmark.StringSplit -rf csv -rff stringsplit.csv -o stringsplit.log
		 * -f 1 -gc true -wi 10 -w 100ms -i 50 -r 100ms -p regex=".*error.*" -p str="error" -p testString="abcccerrordefg" -p expectation="true"
		 * -rf csv -rff split_error_iter50.csv -o log/split_error_iter50.log
		 */
		Options opt = new OptionsBuilder()
				.include(StringSplit.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.build();
	}

}
