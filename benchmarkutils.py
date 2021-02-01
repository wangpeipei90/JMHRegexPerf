'''
Created on Feb 1, 2021

@author: pw
'''

import random
import string
from enum import Enum
import exrex

class CharacterSetType(Enum):
    AlphaNumeric = "AlphaNumeric" 
    Printable = "Printable"
    ASCII = "ASCII"
    Unicode = "Unicode"

def generate_random_str(length: int, type: CharacterSetType) -> str:
    if type == CharacterSetType.AlphaNumeric:
        return ''.join([random.choice(string.ascii_letters + string.digits) for _ in range(length)])
    elif type == CharacterSetType.Printable:
        return ''.join([random.choice(string.printable) for _ in range(length)])
    else:
        raise Exception('Not supported character set type yet')
    
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
    
if __name__ == '__main__':
    pass