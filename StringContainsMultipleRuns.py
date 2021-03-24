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
import time
import random
import os
from benchmarkutils import (
    generate_random_nonmatching_str,
    get_class_path, CharacterSetType,
    generate_mismatch_str_by_edit, generate_mismatch_str_by_remove,
    generate_match_str,
) 
from StringContains import get_cmd        
        
if __name__ == '__main__':
    cur_path, home_path = os.getcwd(), os.getenv("HOME")
    class_path = get_class_path(cur_path, home_path)
    character_type = CharacterSetType.Printable
    print(cur_path, home_path)
    
    SUBSTR_LITERAL = "http" # '?' * 50 # '8' * 50 #"some" # http
    substr_regex = re.compile(".*" + re.escape(SUBSTR_LITERAL) + ".*", re.RegexFlag.DOTALL)
    
    runs = 10
    lens = [9, 17, 63, 125, 318, 948]
        
#     file_name = "http_test_10runs.input4"
#     file_name2 = "http_test_10runs.input5"
#     strings, strings2 = [], []
#     for str_len in lens:
#         gen_str = generate_match_str(".*" + re.escape(SUBSTR_LITERAL) + ".*", str_len)
#         
#         match_pos = gen_str.index(SUBSTR_LITERAL)
#         
#         gen_str2 = generate_random_nonmatching_str(match_pos, substr_regex, character_type) + \
#         SUBSTR_LITERAL + generate_random_nonmatching_str(len(gen_str) - match_pos - len(SUBSTR_LITERAL), substr_regex, character_type) 
#         
#         strings.append((gen_str, str_len, True, len(gen_str)))
#         strings2.append((gen_str2, str_len, True, len(gen_str2)))
#         
#         gen_str_nonmatching = generate_random_nonmatching_str(len(gen_str), substr_regex, character_type)
#         gen_str_nonmatching2 = generate_random_nonmatching_str(len(gen_str), substr_regex, character_type)
#         
#         strings.append((gen_str_nonmatching, str_len, False, len(gen_str_nonmatching)))
#         strings2.append((gen_str_nonmatching2, str_len, False, len(gen_str_nonmatching2)))
#         
#     pickle.dump(strings, open(file_name,"wb"))
#     pickle.dump(strings2, open(file_name2,"wb"))
    
    JAVA_CLASS_NAME = "benchmark.StringContains"
    cmds = []
    
    for idx_dataset, file_name in enumerate(["http_test_10runs.input4", "http_test_10runs.input5"]):
        data = pickle.load(open(file_name, "rb"))
        for gen_str, str_len, matches, true_len in data:
            if (matches, true_len) in [(False, 90)]: #, (True, 137), (False, 137), (False, 318), (True, 1537), (False, 1537)]:
                print("length:", str_len, "generated str:", gen_str, "matches:", matches, "string length:", true_len)
                for idx_run in range(runs):
                    cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join(["dataset" + str(idx_dataset + 4), \
                        str(str_len), str(matches), str(idx_run), "run"]), re.escape(SUBSTR_LITERAL), gen_str)
                    cmds.append(cmd)
    
    random.shuffle(cmds)
    for cmd in cmds:
            print(f"Verifying Java Benchmark: {cmd}")
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
            time.sleep(10)
