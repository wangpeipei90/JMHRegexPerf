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
  
  
def get_cmd(class_path: str, java_class_name: str, file_name_prefix: str, regex: str, str_exec: str) -> list:
    return ["java", "-Dfile.encoding=UTF-8", "-classpath", class_path, java_class_name, 
            file_name_prefix+".csv",
            file_name_prefix+".log",
            regex.encode('utf-8'),
            str_exec.encode('utf-8')]                
        
if __name__ == '__main__':
    cur_path, home_path = os.getcwd(), os.getenv("HOME")
    class_path = get_class_path(cur_path, home_path)
    character_type = CharacterSetType.Printable
    print(cur_path, home_path)
    
    split_regex = "\\s*,\\s*"
    split_regex_obj = re.compile(split_regex, re.RegexFlag.DOTALL)
    print(split_regex_obj.pattern)
    
    file_name = "split_regex_trimmed.input"
    JAVA_CLASS_NAME = "benchmark.PrecompiledRegexSplit"
    
    data = defaultdict(list)
    for i in range(2):
        gen_str_match = generate_match_str(split_regex, 200)
        gen_str_nonmatch = generate_random_nonmatching_str(len(gen_str_match), split_regex_obj, character_type)
        
        data[len(gen_str_match)].append(gen_str_match); data[len(gen_str_match)].append(gen_str_nonmatch)
        
        for idx, str_val in enumerate([gen_str_match, gen_str_nonmatch]):
            cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join(["split_precompiled_regex", str(i), str(len(gen_str_match)), "Match" if idx == 0 else "NonMatch"]), re.escape(split_regex), str_val)
            print(len(gen_str_match), cmd)   
            result = subprocess.run(cmd, stdout=subprocess.PIPE, check=True)
            time.sleep(10) 
    
    pickle.dump(data, open(file_name,"wb"))