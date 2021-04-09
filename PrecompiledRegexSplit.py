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
from IPython.utils.capture import capture_output
  
  
def get_cmd(class_path: str, java_class_name: str, file_name_prefix: str, regex: str, str_exec: str) -> list:
    return ["java", "-Dfile.encoding=UTF-8", "-classpath", class_path, java_class_name, 
            file_name_prefix+".csv",
            file_name_prefix+".log",
            regex,
            str_exec] #.encode('utf-8')]                
        
if __name__ == '__main__':
    cur_path, home_path = os.getcwd(), os.getenv("HOME")
    class_path = get_class_path(cur_path, home_path)
    character_type = CharacterSetType.Printable
    print(cur_path, home_path)
    
    split_regex = r"\s*,\s*" # "\\s*,\\s*"
    split_regex_obj = re.compile(split_regex, re.RegexFlag.DOTALL)
    print(split_regex_obj.pattern)
    print(repr(split_regex_obj.pattern))
    
    file_name = "split_regex_trimmed.input"
    JAVA_CLASS_NAME = "benchmark.PrecompiledRegexSplit"
    
    data = defaultdict(list)
    cmds = []
    for i in range(250):
        gen_str_match = generate_match_str(split_regex, 200)
        gen_str_nonmatch = generate_random_nonmatching_str(len(gen_str_match), split_regex_obj, character_type)
        
        data[len(gen_str_match)].append(gen_str_match); data[len(gen_str_match)].append(gen_str_nonmatch)
        
        for idx, str_val in enumerate([gen_str_match, gen_str_nonmatch]):
            cmd = get_cmd(class_path, JAVA_CLASS_NAME, '_'.join(["split_precompiled_regex", str(i), str(len(gen_str_match)), "Match" if idx == 0 else "NonMatch"]), split_regex, str_val)
            cmds.append(cmd)
    
    pickle.dump(data, open(file_name,"wb"))
    print("Generation over")
    
    random.shuffle(cmds)
    for cmd in cmds:
        result = subprocess.run(cmd, check=True, capture_output = True) # stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if result.returncode < 0:
            print(result.stderr)
            sys.exit(0)
        time.sleep(10) 
    
    