import pickle
import subprocess
import os
import sys
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
import math
import argparse
import StringGenerator


def stratifiedSampling(csv_filename, output_filename, percentage, rand=1):
    Meta = pd.read_csv(csv_filename, sep=', ')
    rows, cols = Meta.shape
    column_names = Meta.columns.tolist()
    print(rows, cols, column_names)

    y = Meta.pop('length')
    X = Meta
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=math.floor(rows * percentage),
                                                        random_state=rand, stratify=y)
    # print(X_test, y_test)
    print(X_test.columns, y_test.name)  ##y_test a Series

    X_test['length'] = y_test.values
    sampling = X_test  # .append(y_test)
    print(sampling.columns)
    sampling = sampling.reindex(columns=column_names)
    print(sampling.columns)
    sampling.to_csv(output_filename, sep=",", index=False, header=True, encoding='utf-8')


def perString():
    print(os.getcwd())
    assert os.path.exists("target/regexbenchmarks.jar"), "jmh jar file not found!!"

    benchmark_class = "org.ncsu.regex.perf2.JavaContains"
    regex = ".*error.*"
    contain_str = "error"

    matches = pickle.load(open("test3.p", "rb"))
    not_matches = pickle.load(open("test4.p", "rb"))

    cmd = [
        "java", "-jar target/regexbenchmarks.jar", benchmark_class,
        "-f", "1", "-gc", "true" "-wi", "10", "-i", "50", "-rf", "csv",
        '-p', 'regex="' + regex + '"', '-p', 'str="' + contain_str + '"'
    ]

    for idx, (match, *rest) in enumerate(matches):
        cmd2 = list(cmd)
        cmd2.append('-p')
        cmd2.append('testString="' + match + '"')
        cmd2.append('-p')
        cmd2.append('expectation="true"')
        cmd2.append("-rff")
        cmd2.append("result/contains_error_iter50_match_" + str(idx) + ".csv")
        cmd2.append("-o")
        cmd2.append("log/contains_error_iter50_match_" + str(idx) + ".log")

        print(' '.join(cmd2))
        os.system(' '.join(cmd2))
        # subprocess.run(cmd2)

    for idx, (not_match, *rest) in enumerate(not_matches):
        cmd2 = list(cmd)
        cmd2.append('-p testString="' + not_match + '"')
        cmd2.append('-p expectation="false"')
        cmd2.append("-rff result/contains_error_iter50_not_match_" + str(idx) + ".csv")
        cmd2.append("-o log/contains_error_iter50_not_match_" + str(idx) + ".log")

        print(' '.join(cmd2))
        os.system(' '.join(cmd2))
        # subprocess.call(cmd2)


def generateString():
    options=[]

    character_set=["AlphaNumeric","Printable","ASCII","Unicode"]
    cmd_opt = input('''Enter the characterset number for string generation:  
                0 AlphaNumeric; 
                1 Printable; 
                2 string sampling; 
                3 ASCII;
                4 Unicode;
                ''')
    options.append(cmd_opt)
    cmd_opt = int(cmd_opt)
    if cmd_opt>2:
        raise Exception('Not supported characterset yet!!!')
    character_type=character_set[cmd_opt]

    cmd_opt = input('''Enter the matching type for string generation:  
                    0 startsWith; 
                    1 notStartsWith; 
                    2 contains; 
                    3 notContains;
                    4 other;
                    ''')
    options.append(cmd_opt)
    cmd_opt = int(cmd_opt)
    if cmd_opt > 3:
        raise Exception('Not supported matching type yet!!!')

    substring=input('''Enter the string used for string generation: ''')
    print(substring)
    options.append(substring)

    genSize = int(input('''Enter the number of strings to be generated: '''))
    print(genSize)
    options.append(genSize)

    maxLen = int(input('''Enter the maximum length of strings to be generated: '''))
    print(maxLen)
    options.append(maxLen)

    default_filename="_".join(options)+".csv"
    output_filename=input('''Enter the csv filename where generated strings to be stored ( 
    default name is '''+default_filename+'''):''')
    print(output_filename)

    genFuncs = {
        0: StringGenerator.genStartsWith,
        1: StringGenerator.genNotStartsWith,
        2: StringGenerator.genContains,
        3: StringGenerator.genNotContains}
    assertion_funcs = {
        0: lambda x: x[:len(substring)] == substring,
        1: lambda x: x[:len(substring)] != substring,
        2: lambda x: substring in x,
        3: lambda x: substring not in x
    }

    res = genFuncs[cmd_opt](substring, genSize, 0, maxLen, character_type)
    StringGenerator.asserted(res,assertion_funcs[cmd_opt])
    if cmd_opt%2==0: ## match options
        StringGenerator.save_to_file2(res, output_filename)
    else:
        StringGenerator.save_to_file(res, output_filename)

    print("-----Finished----------")

def stringSampling():
    print("You typed two")
    pass
def runExperiment():
    print("You typed three")
    pass
def performAnalysis():
    print("You typed four")
    pass

def batchProcess():
    try:
        options={1:generateString, 2:stringSampling, 3:runExperiment, 4:performAnalysis}
        while True:
            cmd_opt = input('''Enter the option number to perform a task:  
            0 exit; 
            1 generate string; 
            2 string sampling; 
            3 run a measurement experiment; 
            4 process experiment result
            ''')

            cmd_opt=int(cmd_opt)
            if cmd_opt == 0:
                break
            else:
                options[cmd_opt]()
    except KeyboardInterrupt:
        print('interrupted!')


if __name__ == "__main__":
    batchProcess()
    sys.exit(0)

    parser = argparse.ArgumentParser(description='Stratified sampling of generated strings.')
    parser.add_argument('--file')
    parser.add_argument('--output')
    parser.add_argument('--samplingPercent')
    parser.add_argument('--randomSeed')

    args = parser.parse_args()

    file_genStr, sampling_csv_output, sampling_percentage, sampling_rand = args.file, args.output, float(
        args.samplingPercent), int(args.randomSeed)
    print(file_genStr, sampling_csv_output, sampling_percentage)

    stratifiedSampling(file_genStr, sampling_csv_output, sampling_percentage, sampling_rand)
