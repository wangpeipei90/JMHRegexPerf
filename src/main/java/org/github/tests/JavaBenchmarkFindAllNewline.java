/**
 * this is to test performance among regex findall, replace+split, and string charAt
 */
package org.github.tests;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS) 
@State(Scope.Benchmark)

@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" }) // heap size
@Warmup(iterations = 5, batchSize = 50)
@Measurement(iterations = 1000, batchSize = 50)

public class JavaBenchmarkFindAllNewline {
	@Param("\\r\\n|[\\s\\S]")
	private String regex;
	
	private String testString;
	private Pattern compiledRegex;
	private Pattern REGEX_NEWLINE = Pattern.compile("\\r?\\n");
	
	@Setup(Level.Trial)
	public void setup(){
		compiledRegex = Pattern.compile(regex);
		
		String[] arr = new String[100];
		Arrays.fill(arr, "alskhalsdfkahsdlk\r\nlaskdjflaksj\r\n");
		testString=Arrays.toString(arr);	
	}
	
	@TearDown(Level.Trial)
	public void reset(){
		System.out.println(testString);
	}
	
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(JavaBenchmarkFindAllNewline.class.getSimpleName()) //// .include("JMHF.*") 可支持正则
//				.forks(1)
//				.warmupIterations(5)
//				.measurementIterations(1000)
//				.resultFormat(ResultFormatType.CSV)
//				.result(StringUtils.logDir + Instant.now().getEpochSecond()+".csv")
				.shouldDoGC(true)
				.build();

		new Runner(opt).run();
	}
	
	@Benchmark
	/**
	 * Find all matches
	 * var REGEX_CHARACTER = /\r\n|[\s\S]/g; chunk.match(REGEX_CHARACTER)
	 * REGEX_NEWLINE = /(\r?\n)/
	 */
	public void testRegexFindAllGlobal(Blackhole bh){
		Matcher matcher = compiledRegex.matcher(testString);
	    while (matcher.find()) {  // Get the matching string
	    		bh.consume(REGEX_NEWLINE.matcher(matcher.group()).find());
	    }
	}
	
	@Benchmark
	/**
	 * for (var idx = 0, length = chunk.length; idx < length; idx++) {
	 * 		if (chunk.charCodeAt(idx) === NEWLINE_CODE) {
	 */
	public void testStringCharAt(Blackhole bh){
		for(int i=0,len=testString.length();i<len;i++){
			bh.consume(testString.charAt(i)=='\n');
		}	
	}
	
	@Benchmark
	/**
	 * chunk.replace("\r\n","\n").split('').forEach(function (ch, idx, array) {
        if (ch === "\r\n" || ch === "\n")
        or
        if (ch === "\n")
        String.replace(CharSequence target, CharSequence replacement) is equal to
        replaceAll(Matcher.quoteReplacement(replacement.toString()))
        while replace(char oldChar, char newChar) o(n)
	 */
	public void testStringReplace(Blackhole bh){
		String[] splits=testString.replace("\\r\\n", "\\n").split("");
		for(int i=0,len=splits.length;i<len;i++){
			bh.consume(splits[i]=="\\n");
		}
	}
	@Benchmark
	/**
	 * var REGEX_WIN_NEWLINE = /\r\n/g; //use only once this regex
	 * chunk.replace(REGEX_WIN_NEWLINE, "\n").split('').forEach(function (ch, idx, array) {
        if (ch === "\r\n" || ch === "\n") {
	 * @param bh
	 */
	public void testPrecompiledRegexReplace(Blackhole bh){ 
		Pattern p=Pattern.compile("\\r\\n");
		String[] splits=p.matcher(testString).replaceAll("\\n").split("");
		for(int i=0,len=splits.length;i<len;i++){
			bh.consume(splits[i]=="\\n");
		}
	}
}
