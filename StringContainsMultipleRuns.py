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
        
    file_name = "http_test_10runs.input3"
    strings = []
    for str_len in lens:
        gen_str2 = generate_match_str(".*" + re.escape(SUBSTR_LITERAL) + ".*", str_len)
        strings.append((gen_str2, str_len, True, len(gen_str2)))
        gen_str = generate_random_nonmatching_str(len(gen_str2), substr_regex, character_type)
        strings.append((gen_str, str_len, False, len(gen_str)))
    pickle.dump(strings, open(file_name,"wb"))
    
    JAVA_CLASS_NAME = "benchmark.StringContains"
    cmds = []

    data = pickle.load(open(file_name, "rb"))
    for gen_str, str_len, matches, true_len in data:
        print("length:", str_len, "generated str:", gen_str, "matches:", matches, "string length:", true_len)
        for idx in range(runs):
            cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join([str(str_len), str(matches), str(idx), "run"]), re.escape(SUBSTR_LITERAL), gen_str)
            cmds.append(cmd)
    
    random.shuffle(cmds)
    for cmd in cmds:
            print(f"Verifying Java Benchmark: {cmd}")
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
            time.sleep(10)
