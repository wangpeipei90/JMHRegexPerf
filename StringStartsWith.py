'''
Created on Dec 8, 2020
Pattern.compile(prefix + ".*", Pattern.DOTALL)
pattern.matcher(val).matches())
vs
val.contains(prefix)
@author: pw
'''
from benchmarkutils import generate_mismatch_str_by_edit, generate_mismatch_str_by_remove, generate_match_str, generate_random_str, CharacterSetType
import re
from collections import defaultdict
import subprocess
import exrex
from dataclasses import dataclass
import pickle
import os.path
import os

cur_path, home_path = os.getcwd(), os.getenv("HOME")
class_path = ":".join([cur_path+"/target/classes",
                      home_path+"/.m2/repository/org/apache/commons/commons-csv/1.8/commons-csv-1.8.jar",
                      home_path+"/.m2/repository/org/openjdk/jmh/jmh-core/1.26/jmh-core-1.26.jar",
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
@dataclass
class ContainedStringCase:
    index: int
    escaped_regex: str
    regex_len: int
    str_to_match: dict
    
def produce_case(index:int, regex_len:int) -> ContainedStringCase:
    regex_literal = generate_random_str(regex_len,CharacterSetType.Printable)
    regex_literal = re.escape(regex_literal)
    
    composed_regex = regex_literal + ".*"
    try:
        composed = re.compile(composed_regex,re.RegexFlag.DOTALL)
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
    
def get_cmd(regex_index:int, string_character:str, regex:str, str_exec:str) -> list:
    return ["java", "-Dfile.encoding=UTF-8", "-classpath", class_path, "benchmark.StringStartsWith", 
            str(regex_index)+"_"+string_character+".csv",
            str(regex_index)+"_"+string_character+".log",
            regex.encode('utf-8'),
            str_exec.encode('utf-8')]

def get_result(regex_count:int, case: ContainedStringCase):
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

def generate_matching_str0(regex_literal, str_len, regex_len):
    remaining = str_len - regex_len
    return regex_literal + generate_random_str(remaining, CharacterSetType.Printable)

def generate_matching_str1(regex_literal, str_len, regex_len):
    remaining = str_len//2 - regex_len
    return generate_random_str(str_len//2, CharacterSetType.Printable) + regex_literal + generate_random_str(remaining, CharacterSetType.Printable)

def generate_nonmatching_str(regex_literal, str_len, ver_regex):
    s = generate_random_str(str_len, CharacterSetType.Printable)
    while ver_regex.match(s):
        s = generate_random_str(str_len, CharacterSetType.Printable)
    return s

    
if __name__ == '__main__':
#     matching_input = dict()
#     for regex_len in [5, 50, 500, 5000]:
#         regex_literal = generate_random_str(regex_len, CharacterSetType.Printable)
#         matching_input[regex_literal] = []
#         for str_len in [10, 100, 1000, 10000, 100000, 1000000]:
#             if str_len > regex_len:
#                 s1 = generate_matching_str0(regex_literal, str_len, regex_len)
#                 s2 = generate_matching_str1(regex_literal, str_len, regex_len) # match pos: str_len//2
#                 matching_input[regex_literal].append([s1,s2])
#             
#     for regex_literal, str_list in matching_input.items():
#         rgx = re.escape(regex_literal)
#         r1 = re.compile(".*"+rgx+".*", re.RegexFlag.DOTALL)
#         r2 = re.compile(rgx+".*", re.RegexFlag.DOTALL)
#         for s1, s2 in str_list:
#             assert(r1.match(s1))
#             assert(r1.match(s2))
#             assert(r2.match(s1))
#             print(len(regex_literal), len(s1), len(s2))
#     
#     pickle.dump(matching_input, open("string_contains_match.input", "wb"))
    file_name = "string_contains_match.input"
    data = pickle.load(open(file_name, "rb"))
    nonmatching_input = dict()
    for regex_literal in data:
        regex_len = len(regex_literal)
        nonmatching_input[regex_literal] = []
        ver_regex = re.compile(".*" + re.escape(regex_literal) + ".*", re.RegexFlag.DOTALL)
        for str_len in [10, 100, 1000, 10000, 100000, 1000000]:
            if str_len > regex_len:
                for i in range(5):
                    nonmatching_input[regex_literal].append(generate_nonmatching_str(regex_literal, str_len, ver_regex))
    pickle.dump(nonmatching_input, open("string_contains_nonmatch.input", "wb"))    
        
# #     produce([5, 10, 50, 100, 500, 1000], file_name)
#     cases = pickle.load(open(file_name, "rb"))
#     for case in cases:
# #         if case.index >= 3: break #continue
#         string_count = 0
#         for s, (mis_rm, mis_edit) in case.str_to_match.items():
#             for cmd in [get_cmd(case.index, str(string_count)+"_matching", case.escaped_regex, s),
#                         get_cmd(case.index, str(string_count)+"_dismatching_rm", case.escaped_regex, mis_rm),
#                         get_cmd(case.index, str(string_count)+"_dismatching_edit", case.escaped_regex, mis_edit)
#                     ]:
#                 print("verifying matching in Java Benchmark:", cmd)
#                 result = subprocess.run(cmd, stdout=subprocess.PIPE)
#             string_count += 1
