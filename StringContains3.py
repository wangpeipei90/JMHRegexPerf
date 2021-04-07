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
import time
import os
from benchmarkutils import (
    generate_random_nonmatching_str,
    get_class_path, CharacterSetType,
    generate_mismatch_str_by_edit, generate_mismatch_str_by_remove,
    generate_match_str,
)
import json

MAX_STR_LEN = 400
match_ratios = [0.1, 0.3, 0.5, 0.7, 0.9] # the number of matching strings in total number of strings
    
def get_cmd(class_path: str, java_class_name: str, file_name_prefix: str, regex: str, str_exec: str) -> list:
    return ["java", "-Dfile.encoding=UTF-8", "-classpath", class_path, java_class_name, 
            file_name_prefix+".csv",
            file_name_prefix+".log",
            regex.encode('utf-8'),
            str_exec.encode('utf-8')]
        
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
    
    repetition = 50
    dataset_size = 1000
    file_name = "http_dataset.input"
    if not os.path.exists(file_name):
        http_data = []
        for i in range(repetition):
            data = defaultdict(list)
            for ratio in match_ratios:
                print(f"Generating {dataset_size} strings for {i}th and matching string ratio {ratio}") 
                num_matching = dataset_size * ratio
                num_nonmatching = dataset_size - num_matching
                
                random.seed(round(time.time())) # use current time as seed of random generator
                while num_nonmatching > 0:
                    string_len = random.randint(1, MAX_STR_LEN)
                    gen_str = generate_random_nonmatching_str(string_len, substr_regex, character_type)
                    data[ratio].append((gen_str, string_len, "NM"))
                    num_nonmatching -= 1
                
                while num_matching > 0:
                    prefix_len = random.randint(0, MAX_STR_LEN - len(SUBSTR_LITERAL))
                    non_matching_prefix = generate_random_nonmatching_str(prefix_len, substr_regex, character_type)
                    suffix_len = random.randint(0, MAX_STR_LEN - len(SUBSTR_LITERAL) - prefix_len)
                    non_matching_suffix = generate_random_nonmatching_str(suffix_len, substr_regex, character_type)
                    gen_str = non_matching_prefix + SUBSTR_LITERAL + non_matching_suffix
                    
                    data[ratio].append((gen_str, prefix_len + len(SUBSTR_LITERAL) + suffix_len, "M"))
                    num_matching -= 1
            
            http_data.append(data)
            print(f"Finishing generating {dataset_size} strings for {i}th")        
        pickle.dump(http_data, open(file_name,"wb"))
        print("generation over")
        
    JAVA_CLASS_NAME = "benchmark.StringContainsDataset"
#     cmds = []
    for idx, data in enumerate(pickle.load(open(file_name, "rb"))): # one repetition
        # print(list(data.keys()), len(data))
        for match_ratio, dataset in data.items():
            print(idx, match_ratio, len(dataset))
            json_strs = json.dumps(dataset)
            # print(match_ratio, len(json_strs),json_strs)
            cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join([SUBSTR_LITERAL, str(match_ratio), str(idx)]), re.escape(SUBSTR_LITERAL), json_strs)
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
            break
        break
