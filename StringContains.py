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
from collections import defaultdict
from dataclasses import dataclass
import pickle
import os.path
import time
import random
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
    
def get_cmd(class_path: str, java_class_name: str, file_name_prefix: str, regex: str, str_exec: str) -> list:
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
        
def generate(substr_literal, string_len, match_pos_ratio, substr_regex, character_type: CharacterSetType = CharacterSetType.Printable):
    substr_len = len(substr_literal)
    if match_pos_ratio < 1:
        non_matching_prefix_len = int(string_len * match_pos_ratio)
        if non_matching_prefix_len + substr_len > string_len:
            return -1
        non_matching_prefix = generate_random_nonmatching_str(non_matching_prefix_len, substr_regex, character_type)
        non_matching_suffix = generate_random_nonmatching_str(string_len - non_matching_prefix_len - substr_len, substr_regex, character_type)
        gen_str = non_matching_prefix + substr_literal + non_matching_suffix
    else:
        gen_str = generate_random_nonmatching_str(string_len, substr_regex, character_type)
    return gen_str
                  
        
if __name__ == '__main__':
    cur_path, home_path = os.getcwd(), os.getenv("HOME")
    class_path = get_class_path(cur_path, home_path)
    character_type = CharacterSetType.Printable
    print(cur_path, home_path)
    
    SUBSTR_LITERAL = "http" 
    substr_regex = re.compile(".*" + re.escape(SUBSTR_LITERAL) + ".*", re.RegexFlag.DOTALL)
    substr_len = len(SUBSTR_LITERAL)
    
    file_name = "http.input"
    if not os.path.exists(file_name):
        http_data = dict()
        for i in range(2, power_two):
            string_len = 2 ** i
            if string_len not in http_data:
                http_data[string_len] = defaultdict(list)
                
            for match_pos_ratio in match_pos_ratios:
                start_index = len(http_data[string_len][match_pos_ratio])
                retries = 0
                while len(http_data[string_len][match_pos_ratio]) < start_index + 15 or retries < 20:
                    gen_str = generate(SUBSTR_LITERAL, string_len, match_pos_ratio, substr_regex, character_type)
                    if gen_str == -1:
                        print(f"Could not generate with string of length {string_len} and matching position ratio of {match_pos_ratio}")
                        break
                    elif gen_str in http_data[string_len][match_pos_ratio]:
                        retries -= 1
                    else:
                        http_data[string_len][match_pos_ratio].append(gen_str)
                print(f"Generate {len(http_data[string_len][match_pos_ratio]) - start_index} unique string of length {string_len} and matching position ratio of {match_pos_ratio}")      
        pickle.dump(http_data, open(file_name,"wb"))
        print("generation over")
        
    JAVA_CLASS_NAME = "benchmark.StringContains"
    cmds = []
    data = pickle.load(open(file_name, "rb"))
    for str_len, dict_pos_ratio in data.items():
        for match_pos_ratio, string_set in dict_pos_ratio.items():
            for idx, gen_str in enumerate(string_set):
                cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join([SUBSTR_LITERAL, str(str_len), str(match_pos_ratio), str(idx)]), re.escape(SUBSTR_LITERAL), gen_str)
                cmds.append(cmd)
    
    random.shuffle(cmds)
    for cmd in cmds:
            print(f"Verifying Java Benchmark: string length {str_len}, matching position ratio {match_pos_ratio}", cmd)
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
            time.sleep(10)
                
