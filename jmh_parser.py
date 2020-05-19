import pandas as pd
import os
import re
import sys
import argparse

# Benchmark Modes
SAMPLING = 1
AVG = 2
THR = 3

def parseParameters(params, extractParam=False):
    dic = {}

    if extractParam:
        params = params.replace('(', '')
        params = params.replace(')', '')
        params = params.split(', ')

        for p in params:
            values = p.split(' = ')
            dic['param_' + values[0]] = values [1]
    dic['Full params'] = params

    return dic


def parseAverageAndThroughput(line):
    # Regex from https://stackoverflow.com/questions/4703390/how-to-extract-a-floating-number-from-a-string
    numbers = re.findall(r"[-+]?\d*\.\d+|\d+", line)

    if len(numbers) == 2:
        iteration = numbers[0]
        score = numbers[1]
        tokens = line.split(' ')
        measurement_unit = tokens[-1]  # Last token is the measurement unit


    else:
        iteration = numbers[0]
        score = None
        measurement_unit = ''
    #         print('Unexpected Line format: %s' % line)

    dic = {}
    dic['Iteration'] = iteration
    dic['Score'] = score
    dic['Measurement Unit'] = measurement_unit
    dic['Iteration Type'] = 'warmup' if 'Warmup' in line else 'measured'

    return dic


def parseSampling(line):
    # Regex from https://stackoverflow.com/questions/4703390/how-to-extract-a-floating-number-from-a-string
    numbers = re.findall(r"[-+]?\d*\.\d+|\d+", line)
    iteration = numbers[0]

    n = re.findall(r" n = (\d+)", line)[0]
    # Regex to get
    mean_token = re.findall(r",( mean = \d+ .+?),", line)[0].split(' ')

    mean = mean_token[-2]
    measurement_unit = mean_token[-1]

    dic = {}
    dic['Iteration'] = iteration
    dic['Score'] = mean
    dic['n (Sample)'] = n
    dic['Measurement Unit'] = measurement_unit
    dic['Iteration Type'] = 'warmup' if 'Warmup' in line else 'measured'

    return dic


def parseIteration(line, mode):

    dic = {}

    if mode == SAMPLING:
        dic = parseSampling(line)
    elif mode == AVG or mode == THR:
        dic = parseAverageAndThroughput(line)

    return dic


def parseBenchmark(line):

    tokens = line.split('.')
    method = tokens[-1]
    clazz = tokens[-2]

    # Reformat the package
    pkg = '.'.join(tokens[0:-2])

    dic = {}
    dic['Method'] = method
    dic['Class'] = clazz
    dic['Package'] = pkg
    dic['Full Bench'] = line

    return dic


def parseBenchmarkMode(line):
    dic = {}
    dic['Benchmark Mode'] = line
    possible_modes = {"Sampling time": SAMPLING, "Average time, time/op": AVG, "Throughput, ops/time": THR}
    return dic, possible_modes[line]


def parseThreads(line):
    numbers = re.findall(r"[-+]?\d*\.\d+|\d+", line)

    dic = {}
    dic['Threads'] = numbers[0]
    return dic


def parseFork(line):

    if line.startswith('# Fork: N/A'):
        fork = 1
        total = 0
    else:
        fork_tokens = re.findall(r"[-+]?\d*\.\d+|\d+", line)
        fork = fork_tokens[0]
        total = fork_tokens[1]

    dic = {}
    dic['Fork'] = int(fork)
    dic['Total Fork'] = total
    return dic


def parselines(lines):

    df = pd.DataFrame()

    bench = ''
    params = {}
    params['Full params'] = '' # Default
    for l in lines:

        # Get rid of newline
        l = l.replace('\n', '')

        # Header
        if l.startswith("# Benchmark:"):
            bench = parseBenchmark(l.split("# Benchmark:")[1])

        if l.startswith("# Benchmark mode:"):
            mode_dic, mode = parseBenchmarkMode(l.split("# Benchmark mode: ")[1])

        if l.startswith("# Fork:"):
            fork = parseFork(l)

        if l.startswith("# Parameters:"):
            params = parseParameters(l.split("# Parameters:")[1])

        if (l.startswith("# Threads:")):
            threads = parseThreads(l)

        # Each row of the dataframe corresponds a one measured/warmup iteration ---
        if l.startswith("# Warmup Iteration") or l.startswith("Iteration"):
            try:
                iteration = parseIteration(l, mode)
                # Create the formatted row
                new_row = {}
                new_row.update(mode_dic)
                new_row.update(fork)
                new_row.update(bench)
                new_row.update(params)
                new_row.update(iteration)
                new_row.update(threads)

                # Append to the Dataframe
                df = df.append(new_row, ignore_index=True)
            except IndexError:
                print('Error while parsing the line %s' % l)

    return df



def parseFile(file_path):
    '''https://github.com/DiegoEliasCosta/badJMHpractices-study/RQ2. Impact of bad JMH practices/scripts/jmh_parser.py '''
    # file_path = os.path.join('..', 'Artifacts', 'druid benchmark', 'out.out')
    print('Parsing file %s' % file_path)

    #with open(file_path, "r", encoding='utf-8') as f:
    with open(file_path, "r") as f:
        lines = f.readlines()
        df = parselines(lines)

    # Save to disk
    new_file_path = file_path.replace('.log', '_parsed.csv')
    print('Saving the parsed jmh results in file = %s' % new_file_path)
    df.to_csv(new_file_path)
    return df


def extractStringAndExecutionTimeFromIterations(df_expertment_time,file_str_gen,csv_output,batch_size):
    print(type(df_expertment_time))
    df_measurement = df_expertment_time.drop(
        columns=['Benchmark Mode', 'Class', 'Fork', 'Full Bench', 'Full params', 'Method', 'Package', 'Threads',
                 'Total Fork'])[10:]

    df = pd.read_csv(file_str_gen)
    result=df[:len(df_measurement)]

    result["Score"] = df_measurement["Score"].to_numpy()
    result["Unit"] = df_measurement["Measurement Unit"].to_numpy()
    result['Batch']= batch_size
    print(result[:10])
    result.to_csv(csv_output)
    return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Parse JMH output files (.out) into structured csv file '
                                                 'and extract the measured time to generated strings.')
    parser.add_argument('--log')
    parser.add_argument('--file')
    parser.add_argument('--output')
    parser.add_argument('--batchsize')

    args = parser.parse_args()

    file_genStr,result_log,csv_output,batch_size=args.file,args.log,args.output,args.batchsize

    # csv_output="out.csv"
    # file_genStr="test3.csv"
    # result_log="log/regex_precompiled_warm10_iter100.log"
    extractStringAndExecutionTimeFromIterations(parseFile(result_log),file_genStr,csv_output,batch_size)
