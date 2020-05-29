import pickle
import subprocess
import os
from sklearn.model_selection import train_test_split
import pandas as pd
import numpy as np
import math
import argparse

def stratifiedSampling(csv_filename,percentage):

    Meta = pd.read_csv(csv_filename, sep=', ')
    rows,cols=Meta.shape
    print(rows,cols)
    y = Meta.pop('length')
    X = Meta
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=math.floor(rows*percentage), random_state=42,stratify=y)
    print(X_test,y_test)

def perString():
    print(os.getcwd())
    assert os.path.exists("target/regexbenchmarks.jar"), "jmh jar file not found!!"

    benchmark_class="org.ncsu.regex.perf2.JavaContains"
    regex=".*error.*"
    contain_str="error"

    matches=pickle.load(open("test3.p", "rb" ))
    not_matches = pickle.load(open("test4.p", "rb"))

    cmd=[
        "java", "-jar target/regexbenchmarks.jar",benchmark_class,
        "-f", "1", "-gc", "true" "-wi", "10", "-i", "50", "-rf", "csv",
         '-p', 'regex="'+regex+'"', '-p', 'str="'+contain_str+'"'
         ]

    for idx,(match,*rest) in enumerate(matches):
        cmd2=list(cmd)
        cmd2.append('-p')
        cmd2.append('testString="'+match+'"')
        cmd2.append('-p')
        cmd2.append('expectation="true"')
        cmd2.append("-rff")
        cmd2.append("result/contains_error_iter50_match_"+str(idx)+".csv")
        cmd2.append("-o")
        cmd2.append("log/contains_error_iter50_match_"+str(idx) + ".log")

        print(' '.join(cmd2))
        os.system(' '.join(cmd2))
        #subprocess.run(cmd2)

    for idx, (not_match,*rest) in enumerate(not_matches):
        cmd2 = list(cmd)
        cmd2.append('-p testString="'+not_match+'"')
        cmd2.append('-p expectation="false"')
        cmd2.append("-rff result/contains_error_iter50_not_match_" + str(idx) + ".csv")
        cmd2.append("-o log/contains_error_iter50_not_match_" + str(idx) + ".log")

        print(' '.join(cmd2))
        os.system(' '.join(cmd2))
        #subprocess.call(cmd2)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Stratified sampling of generated strings.')
    parser.add_argument('--file')
    parser.add_argument('--output')
    parser.add_argument('--samplingPercent')

    args = parser.parse_args()

    file_genStr, sampling_csv_output, sampling_percentage = args.file, args.output, float(args.samplingPercent)
    print(file_genStr,sampling_csv_output,sampling_percentage)
    stratifiedSampling(file_genStr,sampling_percentage)