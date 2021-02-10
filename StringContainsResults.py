import pickle
import re
import pandas as pd
from StringContains import ContainedStringCase 
# def get_result(regex_count:int, case: ContainedStringCase):
#     string_count = 0
# #     str(case.index)+"_"+str(string_count)+"_dismatching_rm.csv", 
# #     str(case.index)+"_"+str(string_count)+"_dismatching_edit.csv",
#     
#     csv_names = []
#     for s, (mis_rm, mis_edit) in case.str_to_match.items():
#         
#         string_count += 1
#         if os.path.exists(csv_name):
#             csv_names.append(csv_name)
#         df = pd.concat(map(pd.read_csv, glob.glob(os.path.join('', "my_files*.csv"))))
def plot1(df_names, file_figure, regex_len: int, string_type: str): #, regex_len, type):
    df_all = pd.concat(df_names)
#     df_all["str:len"] = df_all["Param: str"].map(lambda x: len(x) if not pd.isnull(x) else 0)
#     df_all["Benchmark"] = df_all["Benchmark"].map(lambda x: x.split(".")[-1])
#     if string_type == "M":
#         df = df_all[["Benchmark","Score","str:len","str:pos"]].pivot(index=["str:len","str:pos"],columns="Benchmark",values="Score")
#     else:
    df = df_all[["Benchmark","Score","str:len"]].pivot(index="str:len",columns="Benchmark",values="Score")
    ax = df.plot.bar()
    ax.figure.savefig(file_figure)
    df['ratio'] = df['regexMatches'] / df['stringContains']
    df['regex:len'] = regex_len
    df['str:type'] = string_type
    print(file_figure)
    print(df)
    return df

def read_csv(file_name: str, string_len: int, matching_pos = None):
    df = pd.read_csv(file_name)
    df["str:len"] = string_len
    df["Benchmark"] = df["Benchmark"].map(lambda x: x.split(".")[-1])
    if matching_pos is not None:
        df["str:pos"] = matching_pos
    return df
                       
if __name__ == '__main__':
    results_dir = input("Please type the directory of the experiment results, ending with /: ")
    file_name = "string_contains.input"
#     results_dir = "stringcontains_results/"
    cases = pickle.load(open(file_name, "rb"))
    data1, data2, data3 = None, None, None
    for case in cases:
        print(case.index, len(case.escaped_regex))
        regex_len = len(case.escaped_regex)
        regex_search = re.compile(case.escaped_regex)
#         if case.index > 2:
#             break
        string_count = 0
        df_names_matching, df_names_dismatching_rm, df_names_dismatching_edit = [], [], []
        for s, (mis_rm, mis_edit) in case.str_to_match.items():
            filename_prefix = results_dir+str(case.index)+"_"+str(string_count)
            
            matching_pos = regex_search.search(s).start()
            print(matching_pos, len(s))
            df1 = read_csv(filename_prefix+"_matching.csv", len(s), matching_pos)
            df_names_matching.append(df1)
            
            df2 = read_csv(filename_prefix+"_dismatching_rm.csv", len(mis_rm))
            df_names_dismatching_rm.append(df2)
            
            df3 = read_csv(filename_prefix+"_dismatching_edit.csv", len(mis_edit))
            df_names_dismatching_edit.append(df3)
            
            string_count += 1
        
        df1 = plot1(df_names_matching, str(case.index)+"_matching.pdf", regex_len, "M")
        df2 = plot1(df_names_dismatching_rm, str(case.index)+"_dismatching_rm.pdf", regex_len, "N_RM")
        df3 = plot1(df_names_dismatching_edit, str(case.index)+"_dismatching_edit.pdf", regex_len, "N_ED")
        if data1 is not None:
            data1 = data1.append(df1)
            data2 = data2.append(df2)
            data3 = data3.append(df3)
        else:
            data1, data2, data3 = df1, df2, df3
#         print(df1.shape,df2.shape, df3.shape, data.shape)
    data1.to_csv("stringcontains_data_matching.csv")
    data2.to_csv("stringcontains_data_dismatching_rm.csv")
    data3.to_csv("stringcontains_data_dismatching_edit.csv")