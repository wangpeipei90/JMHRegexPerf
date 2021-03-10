'''
Created on Feb 1, 2021

@author: pw
'''

import random
import string
from enum import Enum
import exrex
import re

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
    
if __name__ == '__main__':
    pass