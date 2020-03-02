package org.ncsu.jmh.examples;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.Statistics;

//@BenchmarkMode(Mode.AverageTime)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // TimeUnit.MICROSECONDS
@State(Scope.Benchmark)
//@Fork(value = 2, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 3)
@Measurement(iterations = 8)
public class BenchmarkLoop {

	@Param({ "10", "100", "1000", "10000" })
	private int N;

	private List<String> DATA_FOR_TESTING;

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()
				.include(BenchmarkLoop.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
				.output("/home/peipei/workspace/first-benchmark/log/" + Instant.now().getEpochSecond())
				.warmupIterations(5)
				.measurementIterations(2)
				.build();

		// new Runner(opt).run();
		
		Collection<RunResult> results = new Runner(opt).run();
		for (RunResult result : results) {
			Statistics inputSizeStats = result.getSecondaryResults().get("inputSize").getStatistics();
			Statistics outputSizeStats = result.getSecondaryResults().get("outputSize").getStatistics();
			double compressionRatio = 1.0 * inputSizeStats.getSum() / outputSizeStats.getSum();
			String compression = result.getParams().getParam("compression");
			String fileFormat = result.getParams().getParam("fileFormat");
			String dataSet = result.getParams().getParam("dataSet");
			System.out.printf("  %-10s  %-30s  %-10s  %-25s  %2.2f  %10s ± %11s (%5.2f%%) (N = %d, \u03B1 = 99.9%%)\n",
					result.getPrimaryResult().getLabel(), dataSet, compression, fileFormat, compressionRatio,
					(long) inputSizeStats.getMean(), (long) inputSizeStats.getMeanErrorAt(0.999),
					inputSizeStats.getMeanErrorAt(0.999) * 100 / inputSizeStats.getMean(), inputSizeStats.getN());
		}
	}

	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}

	@Benchmark
	/**
	 * JMH 不会自动实施对冗余代码的消除 无用代码消除（Dead Code Elimination）
	 * JMH 提供了专门的 API — Blockhole 来避免死码消除问题。
	 * 你意识到 DCE 现象后，应当有意识的去消费掉这些孤立的代码，例如 return
	 * JMH本身已经对这种情况做了处理，你只要记住：
	 * 1.永远不要写void方法；
	 * 2.在方法结束返回你的计算结果。有时候如果需要返回多于一个结果，可以考虑自行合并计算结果，
	 * 3.使用JMH提供的BlackHole对象：
	 * @param bh
	 */
	public void loopFor(Blackhole bh) {
		for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
			String s = DATA_FOR_TESTING.get(i); // take out n consume, fair with
												// foreach
			bh.consume(s);
		}
	}

	@Benchmark
	public void loopWhile(Blackhole bh) {
		int i = 0;
		while (i < DATA_FOR_TESTING.size()) {
			String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
			i++;
		}
	}

	@Benchmark
	public void loopForEach(Blackhole bh) {
		for (String s : DATA_FOR_TESTING) {
			bh.consume(s);
		}
	}

	@Benchmark
	public void loopIterator(Blackhole bh) {
		Iterator<String> iterator = DATA_FOR_TESTING.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();
			bh.consume(s);
		}
	}

	private List<String> createData() {
		List<String> data = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			data.add("Number : " + i);
		}
		return data;
	}

    @Benchmark
    @OperationsPerInvocation(10)
    public void loopForEach_sink() {
    	for (String s : DATA_FOR_TESTING) {
			sink(s);
		}
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    /**
     * 
     * @param v
     */
    public static void sink(String v) {
        // IT IS VERY IMPORTANT TO MATCH THE SIGNATURE TO AVOID AUTOBOXING.
        // The method intentionally does nothing.
    }
}
