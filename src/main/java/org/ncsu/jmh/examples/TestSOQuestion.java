package org.ncsu.jmh.examples;

import java.time.Instant;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class TestSOQuestion {
	@Param({"1", "2"})
	public int testNumbers;

	@Benchmark
	public void method1(Blackhole bh) {
	    bh.consume(someMethodCall(testNumbers));
	}

	@Benchmark
	public void method2(Blackhole bh) {
	    bh.consume(someMethodCall2(3));
	}
	
	public int someMethodCall(int x){
		return x+1;
	}
	
	public int someMethodCall2(int x){
		return x+2;
	}
	
	public static void main(String[] args) throws RunnerException {
		// TODO Auto-generated method stub
		Options opt = new OptionsBuilder()
				.include(TestSOQuestion.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
				.output("/home/peipei/workspace/first-benchmark/log/" + Instant.now().getEpochSecond())
				.warmupIterations(5)
				.measurementIterations(2)
				.build();

		 new Runner(opt).run();
	}

}

