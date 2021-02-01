'''
Created on Dec 8, 2020
Pattern.compile(".*" + errorString + ".*", Pattern.DOTALL)
pattern.matcher(res.stdout).matches())
vs
res.stdout.contains(errorString)
@author: pw
'''
from benchmarkutils import generate_mismatch_str_by_edit, generate_mismatch_str_by_remove, generate_match_str, generate_random_str, CharacterSetType
import re
from collections import defaultdict
import subprocess
import exrex
from dataclasses import dataclass
import pickle

@dataclass
class ContainedStringCase:
    index: int
    escaped_regex: str
    regex_len: int
    str_to_match: dict
    
def produce_case(index:int, regex_len:int) -> ContainedStringCase:
    regex_literal = generate_random_str(regex_len,CharacterSetType.Printable)
    regex_literal = re.escape(regex_literal)
    
    composed_regex = ".*" + regex_literal + ".*"
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


def produce(regex_lens:list, file_name:str):
    cases = [produce_case(idx, regex_len) for idx, regex_len in enumerate(regex_lens)]
    pickle.dump(cases, open(file_name, "wb"))
    
def get_cmd(regex_index:int, string_character:str, regex:str, str_exec:str) -> list:
    return ['java', '-jar', 'target/regexbenchmarks.jar', 
            "benchmark.StringContains", "-rf", "csv", 
            "-rff", str(regex_index)+"_"+string_character+".csv",
            "-o", str(regex_index)+"_"+string_character+".log",
            "-p", "regex={}".format(regex).encode('utf-8'),
            "-p", "str={}".format(str_exec).encode('utf-8'),
            "-f", "1",
            "-gc","true"
            ]
    
if __name__ == '__main__':
    file_name = "string_contains.input"
#     produce([5, 10, 50, 100, 500, 1000], file_name)
    cases = pickle.load(open(file_name, "rb"))
    for case in cases:
        string_count = 0
        for s, (mis_rm, mis_edit) in case.str_to_match.items():
            for cmd in [get_cmd(case.index, str(string_count)+"_matching", case.escaped_regex, s),
                        get_cmd(case.index, str(string_count)+"_dismatching_rm", case.escaped_regex, mis_rm),
                        get_cmd(case.index, str(string_count)+"_dismatching_edit", case.escaped_regex, mis_edit)
                    ]:
                print("verifying matching in Java Benchmark:", cmd)
                result = subprocess.run(cmd, stdout=subprocess.PIPE)
                string_count += 1