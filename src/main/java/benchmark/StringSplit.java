package benchmark;
import java.util.Arrays;
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
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
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
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 10, time = 1)
public class StringSplit {
	@Param({"error"})
	private String separator;
	
	@Param({"errorstring"})
	private String val;

	@Benchmark
	public void stringSplit(Blackhole bh){
		String[] res = val.split(separator);
		bh.consume(res);
	}
	
	@Benchmark
	public void regexSplit(Blackhole bh){
		String[] res = Pattern.compile(separator).split(val);
		bh.consume(res);
	}
	public static void main(String[] args) throws CommandLineOptionException, RunnerException {
		String csv_filename = args[args.length-4];
		String log_filename = args[args.length-3];
		String separator = args[args.length-2];
		String val = args[args.length-1];
		CommandLineOptions cmdOptions = new CommandLineOptions(Arrays.copyOfRange(args, 0, args.length-4));
		ChainedOptionsBuilder optBuilder = new OptionsBuilder()
				.parent(cmdOptions)
				.include(StringContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.param("separator",separator)
				.param("val", val)
				.resultFormat(ResultFormatType.CSV)
				.result(csv_filename)
				.output(log_filename)
				.shouldFailOnError(true);
		System.out.println("output file: "+((OptionsBuilder)optBuilder).getOutput());
		System.out.println("result file: "+((OptionsBuilder)optBuilder).getResult());
		new Runner(optBuilder.build()).run();
	}

}
