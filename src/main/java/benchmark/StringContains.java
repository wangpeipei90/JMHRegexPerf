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
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptions;
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

@Fork(value = 5, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
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
	
	public static void main(String[] args) throws Exception {
		String csv_filename = args[args.length-4];
		String log_filename = args[args.length-3];
		String regex = args[args.length-2];
		String str = args[args.length-1];
		CommandLineOptions cmdOptions = new CommandLineOptions(Arrays.copyOfRange(args, 0, args.length-4));
		ChainedOptionsBuilder optBuilder = new OptionsBuilder()
				.parent(cmdOptions)
				.include(StringContains.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
				.shouldDoGC(true)
				.param("regex",regex)
				.param("str", str)
				.resultFormat(ResultFormatType.CSV)
				.result(csv_filename)
				.output(log_filename)
				.shouldFailOnError(true);
		System.out.println("output file: "+((OptionsBuilder)optBuilder).getOutput());
		System.out.println("result file: "+((OptionsBuilder)optBuilder).getResult());
		new Runner(optBuilder.build()).run();
	}

}