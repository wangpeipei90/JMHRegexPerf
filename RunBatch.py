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
    cmd_opt = int(cmd_opt)
    if cmd_opt>2:
        raise Exception('Not supported characterset yet!!!')
    character_type=character_set[cmd_opt]
    options.append(character_type)

    matching_types=["startsWith", "notStartsWith", "contains", "notContains"]
    cmd_opt = input('''Enter the matching type for string generation:  
                    0 startsWith; 
                    1 notStartsWith; 
                    2 contains; 
                    3 notContains;
                    4 other;
                    ''')
    cmd_opt = int(cmd_opt)
    if cmd_opt > 3:
        raise Exception('Not supported matching type yet!!!')
    options.append(matching_types[cmd_opt])

    substring=input('''Enter the string used for string generation: ''')
    options.append(substring)
    print(substring)


    genSize = input('''Enter the number of strings to be generated: ''')
    options.append(genSize)
    genSize=int(genSize)
    print(genSize)


    maxLen = input('''Enter the maximum length of strings to be generated: ''')
    options.append(maxLen)
    maxLen=int(maxLen)
    print(maxLen)


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

    print("-----Starting----------")
    res = genFuncs[cmd_opt](substring, genSize, 0, maxLen, character_type)
    StringGenerator.asserted(res,assertion_funcs[cmd_opt])
    if cmd_opt%2==0: ## match options
        StringGenerator.save_to_file2(res, output_filename)
    else:
        StringGenerator.save_to_file(res, output_filename)

    print("-----Finished----------")

def stringSampling():
    genStr_filename = input('''Enter the csv filename for stratified sampling: ''')
    sampling_percentage = input('''Enter the sampling percentage in float (1% is 0.01): ''')
    sampling_rand = input('''Enter a random number for sampling random seed: (integer 0, 1, 2, ...): ''')

    default_sampling_output=genStr_filename[:-4]+"_sampling"+sampling_percentage+"_rand"+str(sampling_rand)+".csv"
    sampling_csv_output = input("Enter the output csv filename of stratified ampling results (default output file name is"
                                + default_sampling_output+"): ")

    print("-----Starting----------")
    stratifiedSampling(genStr_filename, sampling_csv_output, float(sampling_percentage), int(sampling_rand))
    print("-----Finished----------")

    # parser = argparse.ArgumentParser(description='Stratified sampling of generated strings.')
    # parser.add_argument('--file')
    # parser.add_argument('--output')
    # parser.add_argument('--samplingPercent')
    # parser.add_argument('--randomSeed')
    #
    # args = parser.parse_args()
    #
    # file_genStr, sampling_csv_output, sampling_percentage, sampling_rand = args.file, args.output, float(
    #     args.samplingPercent), int(args.randomSeed)
    # print(file_genStr, sampling_csv_output, sampling_percentage)
    #
    # stratifiedSampling(file_genStr, sampling_csv_output, sampling_percentage, sampling_rand)

def runExperiment():
    print(os.getcwd())
    assert os.path.exists("target/regexbenchmarks.jar"), "jmh jar file not found!!"

    package = "org.ncsu.regex.perf3."
    class_methods = ["BaseLineMethod", "JavaIndexOf", "RegexNotCompiledFullMatchingMethod",
                     "RegexPreCompiledFullMatchingMethod", "StringContainsMethod", "StringIndexOf",
                     "StringMatchesMethod", "StringStartsWith"]
    for idx, classMethod in enumerate(class_methods):
        print(idx,classMethod)
    cmd_opt = input("Enter the index from the above methods:")
    benchmark_class = package+class_methods[int(cmd_opt)]
    print(class_methods[int(cmd_opt)])

    regex = input("Enter the regex for performance measurement:")
    print(regex)

    substring = input("Enter the string which have equivalent operations of regex matching")
    print(substring)

    genStr_filename = input("Enter the csv filename for performance measurement:")
    print(substring)

    expectation = input("Enter the expectation of the method is true or false (lowercase required): ")
    print(expectation)

    iterations = input("Enter the number of strings been used from input file or the measurement iterations: ")
    print(iterations)
    iterations = int(iterations)

    log_filename="log/"+genStr_filename[:-4]+".log"
    print("output log name: "+log_filename)

    result_filename="result/"+genStr_filename
    print("result csv filename: "+result_filename)
    cmd = [
        "java", "-jar", "target/regexbenchmarks.jar", benchmark_class,
        "-f", "1", "-gc", "true" "-wi", "10", "-i", iterations, "-wbs", 20, "-bs", 20,
        '-p', 'regex="' + regex + '"', '-p', 'str="' + substring + '"',
        "-p", 'expectation="' + expectation + '"', "-p", 'filename="' + genStr_filename + '"'
        "-rf", "csv", "-rff", result_filename, "-o", log_filename
    ]

    command=(' '.join(cmd))
    print(command)

    print("-----Starting----------")
    os.system(command)
    print("-----Finished----------")


def performAnalysis():
    print("You typed four")
    pass

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


