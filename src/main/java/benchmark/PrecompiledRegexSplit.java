package benchmark;
import java.util.Arrays;
import java.util.List;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.OptionsBuilder;


/**
 * return Arrays.asList(trimmed.split("\\s*,\\s*", -1));
 * vs
 * private static final Pattern COMMA_WITH_WHITESPACE = Pattern.compile("\\s*,\\s*");
 * return Arrays.asList(COMMA_WITH_WHITESPACE.split(trimmed, -1));
 * 
 * https://github.com/apache/kafka/pull/5168
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 5, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
public class PrecompiledRegexSplit {
	@Param({"\\s*,\\s*"})
	private String regex;
	
	@Param({"error,string"})
	private String trimmed;

	@Benchmark
	public void stringSplit(Blackhole bh){
		List res = Arrays.asList(trimmed.split("\\s*,\\s*", -1));
		bh.consume(res);
	}
	
	@Benchmark
	public void regexSplit(Blackhole bh){
		Pattern COMMA_WITH_WHITESPACE = Pattern.compile("\\s*,\\s*");
		List res = Arrays.asList(COMMA_WITH_WHITESPACE.split(trimmed, -1));
		bh.consume(res);
	}
	
	public static void main(String[] args) throws CommandLineOptionException, RunnerException {
		String csv_filename = args[args.length-4];
		String log_filename = args[args.length-3];
		String split_regex = args[args.length-2];
		String str_val = args[args.length-1];
		CommandLineOptions cmdOptions = new CommandLineOptions(Arrays.copyOfRange(args, 0, args.length-4));
		ChainedOptionsBuilder optBuilder = new OptionsBuilder()
				.parent(cmdOptions)
				.include(PrecompiledRegexSplit.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(false)
				.param("regex",split_regex)
				.param("trimmed", str_val)
				.resultFormat(ResultFormatType.CSV)
				.result(csv_filename)
				.output(log_filename)
				.shouldFailOnError(true);
		System.out.println("output file: "+((OptionsBuilder)optBuilder).getOutput());
		System.out.println("result file: "+((OptionsBuilder)optBuilder).getResult());
		new Runner(optBuilder.build()).run();
	}

}
