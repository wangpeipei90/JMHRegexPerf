'''
Created on Dec 8, 2020
Pattern.compile(".*" + errorString + ".*", Pattern.DOTALL)
pattern.matcher(res.stdout).matches())
vs
res.stdout.contains(errorString)
@author: pw
'''
import re
import math
import subprocess
from dataclasses import dataclass
import pickle
import os.path
import os
from benchmarkutils import (
    generate_random_nonmatching_str,
    get_class_path, CharacterSetType,
    generate_mismatch_str_by_edit, generate_mismatch_str_by_remove,
    generate_match_str,
)

MAX_STR_LEN = 131072
power_two = int(math.log2(MAX_STR_LEN))
match_pos_ratios = [0, 0.25, 0.5, 0.75, 1]

@dataclass
class ContainedStringCase:
    index: int
    escaped_regex: str
    regex_len: int
    str_to_match: dict
    
        
def produce_case(index: int, regex_len: int) -> ContainedStringCase:
    regex_literal = CharacterSetType.Printable.generate_random_str(regex_len)
    regex_literal = re.escape(regex_literal)
    composed_regex = ".*" + regex_literal + ".*"
    
    try:
        composed = re.compile(composed_regex, re.RegexFlag.DOTALL)
    except:
        print("Invalid regex being composed")
        raise
    
    match_wildcard_len = [1, 10, 100, 1000, 10000, 100000]
    regex_search = re.compile(regex_literal)
    
    str_to_match = dict()
    for match_len in match_wildcard_len:
        s = generate_match_str(composed_regex, match_len)
        mutation_remove = generate_mismatch_str_by_remove(regex_search, composed, s)
        mutation_edit = generate_mismatch_str_by_edit(regex_search, composed, s)
        str_to_match[s] = (mutation_remove, mutation_edit)
    
    return ContainedStringCase(index, regex_literal, regex_len, str_to_match)


def produce(regex_lens: list, file_name: str):
    cases = [produce_case(idx, regex_len) for idx, regex_len in enumerate(regex_lens)]
    pickle.dump(cases, open(file_name, "wb"))
    
def get_cmd(java_class_name: str, file_name_prefix: str, regex: str, str_exec: str) -> list:
    return ["java", "-Dfile.encoding=UTF-8", "-classpath", class_path, java_class_name, 
            file_name_prefix+".csv",
            file_name_prefix+".log",
            regex.encode('utf-8'),
            str_exec.encode('utf-8')]

def get_result(case: ContainedStringCase):
    string_count = 0
#     str(case.index)+"_"+str(string_count)+"_dismatching_rm.csv", 
#     str(case.index)+"_"+str(string_count)+"_dismatching_edit.csv",
    
    csv_names = []
    for s, (mis_rm, mis_edit) in case.str_to_match.items():
        csv_name = str(case.index)+"_"+str(string_count)+"_matching.csv"
        string_count += 1
        if os.path.exists(csv_name):
            csv_names.append(csv_name)
        df = pd.concat(map(pd.read_csv, glob.glob(os.path.join('', "my_files*.csv"))))
        
def generate(substr_literal, substr_regex):
    data = []
    substr_len = len(substr_literal)
    for i in range(2, power_two):
        string_len = 2 ** i
        for match_pos_ratio in match_pos_ratios[:-1]:
            non_matching_prefix_len = int(string_len * match_pos_ratio)
            if non_matching_prefix_len + substr_len > string_len:
                print(f"Could not generate with string of length {string_len} and matching position ratio of {match_pos_ratio}")
                continue
            non_matching_prefix = generate_random_nonmatching_str(non_matching_prefix_len, substr_regex, CharacterSetType.Printable)
            non_matching_suffix = generate_random_nonmatching_str(string_len - non_matching_prefix_len - substr_len, substr_regex, CharacterSetType.Printable)
            gen_str = non_matching_prefix + substr_literal + non_matching_suffix
            data.append((gen_str, string_len, match_pos_ratio)) # matching strings
            
        gen_str_non_matching = generate_random_nonmatching_str(string_len, substr_regex, CharacterSetType.Printable)
        data.append((gen_str_non_matching, string_len, match_pos_ratios[-1]))
    
    return data
            
            
        
if __name__ == '__main__':
    cur_path, home_path = os.getcwd(), os.getenv("HOME")
    class_path = get_class_path(cur_path, home_path)
    print(cur_path, home_path)
    SUBSTR_LITERAL = "some" # http
    substr_regex = re.compile(".*"+SUBSTR_LITERAL+".*", re.RegexFlag.DOTALL)
    pickle.dump(generate(SUBSTR_LITERAL, substr_regex), open("some_strings.input1","wb"))
    pickle.dump(generate(SUBSTR_LITERAL, substr_regex), open("some_strings.input2","wb"))
    print("generation over")
    
    JAVA_CLASS_NAME = "benchmark.StringContains"
    for idx, file_name in enumerate(["some_strings.input1", "some_strings.input2"]):
        data = pickle.load(open(file_name, "rb"))
        for gen_str, str_len, match_pos_ratio in data:
            cmd = get_cmd(JAVA_CLASS_NAME, '_'.join([str(idx), SUBSTR_LITERAL, str(str_len), str(match_pos_ratio)]), re.escape(SUBSTR_LITERAL), gen_str)
            print(f"Verifying Java Benchmark: string length {str_len}, matching position ratio {match_pos_ratio}", cmd)
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
#         rgx = re.escape(regex_literal)
#         for s1, s2 in str_list: # s1, s2 have same length
#             for cmd in [
#                 get_cmd(len(regex_literal), str(len(s1))+"_match_pos_0", rgx, s1),
#                 get_cmd(len(regex_literal), str(len(s2))+"_match_pos_half", rgx, s2)
#                 ]:
#                 print("verifying matching in Java Benchmark:", cmd)
#                 result = subprocess.run(cmd, stdout=subprocess.PIPE)
        
#     file_name = "string_contains.input"
# #     produce([5, 10, 50, 100, 500, 1000], file_name)
#     cases = pickle.load(open(file_name, "rb"))
#     for case in cases:
#         string_count = 0
#         for s, (mis_rm, mis_edit) in case.str_to_match.items():
#             for cmd in [get_cmd(case.index, str(string_count)+"_matching", case.escaped_regex, s),
#                         get_cmd(case.index, str(string_count)+"_dismatching_rm", case.escaped_regex, mis_rm),
#                         get_cmd(case.index, str(string_count)+"_dismatching_edit", case.escaped_regex, mis_edit)
#                     ]:
#                 print("verifying matching in Java Benchmark:", cmd)
#                 result = subprocess.run(cmd, stdout=subprocess.PIPE)
#             string_count += 1
            
#     file_name = "string_contains_match.input"
#     data = pickle.load(open(file_name, "rb"))
#     for regex_literal, str_list in data.items():
#         rgx = re.escape(regex_literal)
#         for s1, s2 in str_list: # s1, s2 have same length
#             for cmd in [
#                 get_cmd(len(regex_literal), str(len(s1))+"_match_pos_0", rgx, s1),
#                 get_cmd(len(regex_literal), str(len(s2))+"_match_pos_half", rgx, s2)
#                 ]:
#                 print("verifying matching in Java Benchmark:", cmd)
#                 result = subprocess.run(cmd, stdout=subprocess.PIPE)
#      
#     file_name = "string_contains_nonmatch.input"
#     data = pickle.load(open(file_name, "rb"))
#     for regex_literal, str_list in data.items():
#         rgx = re.escape(regex_literal)
#         i = 0 
#         for s in str_list:
#             cmd = get_cmd(len(regex_literal), str(len(s))+"_nonmatch_"+str(i), rgx, s)
#             print("verifying non matching in Java Benchmark:", cmd)
#             result = subprocess.run(cmd, stdout=subprocess.PIPE)
#             i = (i+1) % 5                  
