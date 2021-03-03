import pickle
import re
import pandas as pd
import numpy as np
from StringContains import ContainedStringCase 
from jmh_parser import parseFile

def format_csv(df: pd.DataFrame, regex_index: int, string_index: int, regex_len: int, string_len: int, matching_type: str, matching_pos = None):
    df = df[["Iteration","Iteration Type","Measurement Unit","Method","Score"]]
    print(regex_index, string_index, matching_pos, matching_type)
    df["str_len"] = string_len
    df["regex_len"] = regex_len
    df["regex_index"] = regex_index
    df["string_index"] = string_index
    df["match_pos"] = matching_pos if matching_pos is not None else string_len + 1
    df["match_type"] = matching_type
    return df

# NONMATCHING_POS = 2**32
                       
if __name__ == '__main__':
    results_dir = input("Please type the directory of the experiment results, ending with /: ")
    file_name = "string_contains.input"
    cases = pickle.load(open(file_name, "rb"))
    data = None
    for case in cases:
        print(case.index, len(case.escaped_regex))
        regex_len = len(case.escaped_regex)
        regex_search = re.compile(case.escaped_regex)
        string_count = 0
        for s, (mis_rm, mis_edit) in case.str_to_match.items():
            filename_prefix = results_dir+str(case.index)+"_"+str(string_count)
            
            df1 = parseFile(filename_prefix+"_matching.log")
            df2 = parseFile(filename_prefix+"_dismatching_rm.log")
            df3 = parseFile(filename_prefix+"_dismatching_edit.log")
            
            matching_pos = regex_search.search(s).start()
            df1 = format_csv(df1, case.index, string_count, regex_len, len(s), "M", matching_pos)
            df2 = format_csv(df2, case.index, string_count, regex_len, len(mis_rm), "N_RM")
            df3 = format_csv(df3, case.index, string_count, regex_len, len(mis_edit), "N_ED")
            
            string_count += 1

            if data is None:
                data = pd.concat([df1, df2, df3])
            else:
                data = data.append([df1, df2, df3])
    data.to_csv(results_dir+"stringcontains_data_all.csv")