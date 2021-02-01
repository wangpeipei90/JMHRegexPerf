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
 * Pattern formatPattern= Pattern.compile(	
            "D+|E+|F+|G+|H+|K+|M+|S+|W+|X+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++");
 * final Matcher patternMatcher= formatPattern.matcher(pattern);   
 * if(!patternMatcher.lookingAt()) {  .... }
 * currentFormatField= patternMatcher.group();
 * Strategy currentStrategy= getStrategy(currentFormatField, definingCalendar);
 * vs
 * if(currentIdx >= pattern.length()) {
        return null;
    }

    char c = pattern.charAt(currentIdx);
    if( isFormatLetter(c)) {
        return letterPattern(c);
    }
    else {
        return literal();
    }
 * @author pw
 *
 */
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS) 
@State(Scope.Thread)

@Fork(value = 1, jvmArgs = { "-server", "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 50, time = 1)
public class BreakDownRegex {
	@Param({"D+|E+|F+|G+|H+|K+|M+|S+|W+|X+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++"})
	private String format_pattern;
	
	@Param({"yyyy-MM-dd'T'HH:mm:ss.SSS Z", "yyyy-MM-dd'T'HH:mm:ss.SSS"})
	private String date_format;
	
	private Pattern formatpattern;
	
	@Setup(Level.Trial)
	public void check(){
		formatpattern = Pattern.compile(format_pattern); // extend to include more cases
	}
	
//	@Benchmark
//	public void stringParse(Blackhole bh){
//		patterns = new ArrayList<StrategyAndWidth>();
//		StrategyParser fm = new StrategyParser(pattern, definingCalendar);
//        for(;;) {
//            StrategyAndWidth field = fm.getNextStrategy();
//            if(field==null) {
//                break;
//            }
//            patterns.add(field);
//        }
//        bh.consume(res);
//	}
//	
//	 StrategyAndWidth getNextStrategy() {
//         if(currentIdx >= pattern.length()) {
//             return null;
//         }
//
//         char c = pattern.charAt(currentIdx);
//         if( isFormatLetter(c)) {
//             return letterPattern(c);
//         }
//         else {
//             return literal();
//         }   
//	 }
//	 
//	 private StrategyAndWidth letterPattern(char c) {
//         int begin = currentIdx;
//         while( ++currentIdx<pattern.length() ) {
//             if(pattern.charAt(currentIdx) != c) {
//                 break;
//             }
//         }
//
//         int width = currentIdx - begin;
//         return new StrategyAndWidth(getStrategy(c, width, definingCalendar), width);
//     }
//	@Benchmark
//	public void regexParse(Blackhole bh){
//		final StringBuilder regex= new StringBuilder();
//        final List<Strategy> collector = new ArrayList<Strategy>();
//        final Matcher patternMatcher= formatPattern.matcher(pattern);
//        if(!patternMatcher.lookingAt()) {
//            throw new IllegalArgumentException(
//                    "Illegal pattern character '" + pattern.charAt(patternMatcher.regionStart()) + "'");
//		bh.consume(res);
//	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
