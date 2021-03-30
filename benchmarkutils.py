'''
Created on Feb 1, 2021

@author: pw
'''

import random
import string
from enum import Enum
import exrex
import re
import glob
from scipy.stats import ks_2samp, ttest_rel, f, mannwhitneyu
from jmh_parser import parseFile
import pandas as pd


class CharacterSetType(Enum):
    AlphaNumeric = string.ascii_letters + string.digits 
    Printable = string.printable
    ASCII = string.ascii_letters
    Unicode = None
    
    def generate_random_str(self, length: int) -> str:
        if length > 0 and self.value is not None:
            return ''.join([random.choice(self.value) for _ in range(length)])
        elif length == 0:
            return ''
        else:
            raise Exception('Input is negative: {length} or character set type {self.name} is not supported yet')

def get_class_path(cur_path, home_path):
    return ":".join([cur_path+"/target/classes",
                      home_path+"/.m2/repository/org/apache/commons/commons-csv/1.8/commons-csv-1.8.jar",
                      home_path+"/.m2/repository/org/openjdk/jmh/jmh-core/1.29/jmh-core-1.29.jar",
                      home_path+"/.m2/repository/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar",
                      home_path+"/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar",
                      home_path+"/.m2/repository/org/openjdk/jmh/jmh-generator-annprocess/1.26/jmh-generator-annprocess-1.26.jar",
                      home_path+"/.m2/repository/org/apache/commons/commons-lang3/3.7/commons-lang3-3.7.jar",
                      home_path+"/.m2/repository/org/apache/commons/commons-text/1.2/commons-text-1.2.jar",
                      home_path+"/.m2/repository/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar",
                      home_path+"/.m2/repository/junit/junit/4.10/junit-4.10.jar",
                      home_path+".m2/repository/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar",
                      home_path+"/.m2/repository/com/github/mifmif/generex/1.0.2/generex-1.0.2.jar",
                      home_path+"/.m2/repository/dk/brics/automaton/automaton/1.11-8/automaton-1.11-8.jar",
                      home_path+"/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar"])
    

    
def generate_match_str(regex, str_len):
    return exrex.getone(regex, str_len)

def generate_mismatch_str_by_remove(regex_search, regex_match, match_str):
        m = regex_search.search(match_str)
        while m:
            begin, end = m.start(), m.end()
            mut_remove = match_str[:begin] + match_str[end:]
            if regex_match.fullmatch(mut_remove) is None:
                return mut_remove  
            m = regex_search.search(mut_remove)

def generate_mismatch_str_by_edit(regex_search, regex_match, match_str):
        m = regex_search.search(match_str)
        while m:
            begin, end = m.start(), m.end()
            
            idx = random.randint(begin, end-1)
            mut_change = match_str[:idx] + random.choice(string.printable) + match_str[idx + 1:]
            
            if regex_match.fullmatch(mut_change) is None:
                return mut_change  
            m = regex_search.search(mut_change)

def generate_random_nonmatching_str(str_len: int, ver_regex: re.Pattern, character_type: CharacterSetType):
    s = character_type.generate_random_str(str_len)
    while ver_regex.match(s):
        s = character_type.generate_random_str(str_len)
    return s

def get_data_frame_from_csv(output_dir: str):
#     os.chdir(output_dir)
    files = glob.glob(output_dir + "*[0-9].csv", recursive=False)
#     print(f"Getting the information from {len(files)}")
    data = []
    for file_name in files:
#         print(file_name)
        *data_index, regex, input_len, match_pos_ratio = file_name[:-4].split("_")
#         print(file_name[:-4].split("_"))
#         break
        df = pd.read_csv(file_name)
        df = df.loc[:, ["Benchmark", "Score", "Unit"]]
        df['Benchmark'] = df["Benchmark"].map(lambda x: x.split(".")[-1])
        df['str_len'] = input_len
        df['match_pos_ratio'] = match_pos_ratio
        df['Score'] = df['Score'].round().astype(int) ## Score from float to int
        data.append(df)
#         print(file_name, df.shape)
        
    df = pd.concat(data, ignore_index=True)
    df = df.pivot_table(values = "Score", index=["str_len","match_pos_ratio"], columns="Benchmark")
    df.reset_index(drop=False, inplace=True) 
    df['substr'] = regex
    df['data_index'] = "_".join(data_index)
    df['improve_ratio'] = df["regexMatches"] / df['stringContains']
    return df

def read_log(file_path):
    df = parseFile(file_path)
    df = df[["Iteration Type","Method","Score"]]
    df = df.loc[df['Iteration Type'] == "measured", ["Method","Score"]]
    df = df.astype({"Score":"float"})
    df['Score'] = df['Score'].round().astype(int) ## Score from float to int
#     print(df.dtypes)
    return df

def get_paired_logs(df0, df1):
    def get_prefixes(df):
        return df.loc[:,["data_index","substr","str_len","match_pos_ratio"]].apply(lambda x: "_".join(map(str, x)) , axis = 1)
    return [(x+".log", y+".log") for x, y in zip(get_prefixes(df0), get_prefixes(df1))]

def get_paired_iteration(pair_files, keys, value):
    def get_items(file_name):
        groups = read_log(file_name).groupby("Method")
        return [groups.get_group(key)[value] for key in keys]
    pairs_dict = dict()
    for file_name0, file_name1 in pair_files:
#         print(file_name0, file_name1)
        for key, value0, value1 in zip(keys, get_items(file_name0), get_items(file_name1)):
            if key not in pairs_dict:
                pairs_dict[key] = []
            pairs_dict[key].append((value0, value1, file_name0, file_name1))
    return pairs_dict

def get_ks_2sample_test_log(df0, df1):
    keys = ["regexMatches", "stringContains"]
    value = "Score"
    pairs_dict = get_paired_iteration(get_paired_logs(df0, df1), keys, value)
    for key in keys:
        count, total = 0, len(pairs_dict[key])
        for s0, s1, file_name0, file_name1 in pairs_dict[key]:
            t = ks_2samp(s0, s1, mode = "asymp")
            if t[1] > 0.01:
                count += 1
#                 print(t)
        print(f"For method {key} among {total} pairs of input, {count} has p-value > 0.01 with k-s two sample tests")

#https://blog.csdn.net/tszupup/article/details/108433430
def get_tandf_test_log(df0, df1):
    keys = ["regexMatches", "stringContains"]
    value = "Score"
    pairs_dict = get_paired_iteration(get_paired_logs(df0, df1), keys, value)
    for key in keys:
        count_t, count_f, total = 0, 0, len(pairs_dict[key])
        for s0, s1, file_name0, file_name1 in pairs_dict[key]:
            l0, l1 = s0.to_list(), s1.to_list()
            t_test_result = ttest_rel(l0, l1)
            if t_test_result[1] > 0.01:
                count_t += 1
#             else:
#                 print("mean", np.mean(l0), np.mean(l1))
            
            var1 = np.var(l0, ddof=1)
            var2 = np.var(l1, ddof=1)
            F = var1 / var2
            # 计算自由度
            df1 = len(l0) - 1
            df2 = len(l1) - 1
            # 计算p值
            f_p_value = 1 - 2 * abs(0.5 - f.cdf(F, df1, df2))
            
            if f_p_value > 0.01:
                count_f += 1
#             else:
#                 print("variance", var1, var2)

        print(f"For method {key} among {total} pairs of input, {count_t} has p-value > 0.01 with t-test, {count_f} has p-value > 0.01 with f-test")

def get_mann_whitney_u_test_test_log(df0, df1):
    keys = ["regexMatches", "stringContains"]
    value = "Score"
    pairs_dict = get_paired_iteration(get_paired_logs(df0, df1), keys, value)
    for key in keys:
        count, total = 0, len(pairs_dict[key])
        for s0, s1, file_name0, file_name1 in pairs_dict[key]:
            data1, data2 = s0.to_list(), s1.to_list()
            # compare samples
            try:
                stat, p = mannwhitneyu(data1, data2)
                print('Statistics=%.3f, p=%.3f' % (stat, p))
                # interpret
                alpha = 0.05
                if p > alpha:
                    print('Same distribution (fail to reject H0)')
                    count += 1
    #             else:
    #                 print('Different distribution (reject H0)')
            except ValueError as e:
                print(key, file_name0, file_name1)
                raise e
        print(f"For method {key} among {total} pairs of input, {count} has p-value > 0.01 with mann whitney u tests")     
if __name__ == '__main__':
    pass
