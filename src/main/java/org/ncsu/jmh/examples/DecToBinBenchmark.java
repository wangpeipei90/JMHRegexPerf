import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.Random;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class DecToBinBenchmark {

      @State(Scope.Benchmark)
      public static class Data {
            Integer testValue;

            public Data() {
                  this.testValue = new Random().nextInt();
            }
      }

      @Warmup(iterations = 10)
      @Measurement(iterations = 10)
      @Benchmark
      public String decToBinSDKWay(Data data) {
            return Integer.toBinaryString(data.testValue);
      }

      @Warmup(iterations = 10)
      @Measurement(iterations = 10)
      @Benchmark
      public String decToBinBitwiseWay(Data data) {
            char[] bin = new char[32];

            for(int i = 31; i > -1; --i) {
                  bin[i] = (char) ((data.testValue&1)+48);
                  data.testValue >>= 1;
            }

            return new String(bin);
      }
}