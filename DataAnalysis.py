import pandas as pd
import numpy as np
import pickle
from StringContains import ContainedStringCase
import seaborn as sns
import matplotlib.pyplot as plt

plt.style.use('seaborn-paper')
import os
from scipy.stats import ks_2samp, ttest_rel, f, mannwhitneyu, pearsonr, spearmanr, kendalltau
from jmh_parser import parseFile
from benchmarkutils import get_data_frame_from_csv, get_ks_2sample_test_log, get_mann_whitney_u_test_test_log, read_log
from statsmodels.formula.api import ols
import statsmodels.api as sm
import glob
import math
import re
from datetime import datetime, timedelta
import pathlib

re_exec_time = re.compile(r"^# Run complete. Total time: (\d{2}:\d{2}:\d{2}).*")

def getCreationTime(file_path: str) -> datetime:
    '''
    :param file_path:
    :return: Time of most recent content modification expressed in seconds.
    '''

    fname = pathlib.Path(file_path)
    mtime = datetime.fromtimestamp(fname.stat().st_mtime)
    return mtime

def getExecutionTime(file_path: str) -> timedelta:
    '''
    Returns the execution time reported from the log file.
        Parameters:
                file_path (string): absolution path of the log file
        Returns:
                duration time(timedelta)
    '''
    for line in open(file_path, 'r').readlines():
        t = re_exec_time.match(line)
        if t is not None:
            dur = t.group(1)
            break
    else:
        # not found duration information
        raise TypeError(f"Time not found in {file_path}")
        # return None
    t = datetime.strptime(dur, "%H:%M:%S")
    delta = timedelta(hours=t.hour, minutes=t.minute, seconds=t.second)
    return delta


def getFromLog(file_path: str):
    '''
    Returns the statistics of all measured iterations in a log file.

        Parameters:
                file_path (string): absolution path of the log file
        Returns:
                stat_df (pd.DataFrame): float numbers, columns are ['Method,'min', 'max', 'mean', 'std', 'duration', 'finish time']
    '''
    delta, finish_time = getExecutionTime(file_path), getCreationTime(file_path)
    t = parseFile(file_path)
    t = t.loc[t["Iteration Type"] == "measured", :]
    t = t.astype({"Score": "float"})
    stat_df = t.groupby("Method").apply(lambda x: pd.Series({"min": x['Score'].min(), "max": x['Score'].max(),
                      "mean": x['Score'].mean(), "std": x['Score'].std(), 'duration':delta, 'finish time':finish_time}))
    stat_df.reset_index(drop=False, inplace=True)
    # print(type(df))
    # print(df.dtypes)
    # print(list(df.columns))
    # print(list(df.index))
    return stat_df


def getMetricsFromFileName1(file_name: str, output_dir: str):
    '''
    Get regex, input string length, match position ratio, and sample index for http_singleStrings experiment
        Parameters:
                file_path (string): absolution path of the log file
                output_dir (string): "http_singleStrings/"
        Returns:
                s (pd.Series): types: object, int, float, int, datetime, deltatime
    '''
    regex, input_len, match_pos_ratio, sample = file_name[len(output_dir):-4].split("_")
    s = pd.Series(
        {"regex": regex, "input string length": int(input_len), "matching position ratio": float(match_pos_ratio),
         "sample index": int(sample)})
    # print(s.apply(type))
    return s


def get_data_frame_from_csv(output_dir: str, func_metrics):
    files = glob.glob(output_dir + "*[0-9].csv", recursive=False)
    data = []
    for file_name in files:
        metrics = func_metrics(file_name, output_dir)
        stat_df = getFromLog(file_name[:-4] + ".log")
        df = pd.read_csv(file_name)
        df['Method'] = df["Benchmark"].map(lambda x: x.split(".")[-1])
        df['data points'] = df['Samples']
        df['confidence interval (99.9%)'] = df["Score Error (99.9%)"]

        for index, value in metrics.items():
            df[index] = value
        df = df.loc[:, list(metrics.index) + ["Method", 'Mode', 'data points', "Unit", "confidence interval (99.9%)"]]

        df = df.merge(stat_df, on="Method")
        data.append(df)
    df = pd.concat(data, ignore_index=True)
    return df


def process_dir(output_dir, output_df_file, func_metrics=getMetricsFromFileName1):
    if (os.path.exists(output_df_file+".pkl")):
        df = pd.read_pickle(output_df_file+".pkl") # df = pd.read_csv(output_df_file+".csv")
    else:
        df = get_data_frame_from_csv(output_dir, func_metrics)
        df.to_csv(output_df_file+".csv")
        df.to_pickle(output_df_file+".pkl")


if __name__ == '__main__':
    value = input("Please enter the process you want (process_dir: 1) :\n")
    if int(value) == 1:
        output_dir = input("Please enter the experiment result folder ending with /: \n")
        df_file_name = input("Please enter the file name to save the returned data frame without file extension: \n")
        print(f"process_dir({output_dir},{df_file_name})")
        process_dir(output_dir, df_file_name)
