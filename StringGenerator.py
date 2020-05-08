import random
import string
import math
import pickle
import time

character_set=["AlphaNumeric","Printable"] #"ASCII","Unicode"]

def generate(length: int, type: str):
    if length==0:
        return ''
    if type == "AlphaNumeric":
        return ''.join([random.choice(string.ascii_letters + string.digits) for _ in range(length)])
    elif type == "Printable":
        return ''.join([random.choice(string.printable) for _ in range(length)])

def generate_batch(size: int, length: int, type: str, exclude):
    # print(size,length,type)
    res=[]
    for _ in range(size):
        t=generate(length,type)
        while exclude(t):
            t = generate(length, type)
        res.append(t)
    return res

def genStartsWith(start_str: str, size:int, len_lb=0, len_hb=1024, type="AlphaNumeric"):
    res=[]
    matchLen=len(start_str)
    len_lb=max(len_lb,matchLen)

    step=math.ceil(size/(len_hb-len_lb+1))
    cur_len=0
    while len(res)<size:
        # print(step,cur_len,len(res))
        res.append((start_str+generate(cur_len,type),matchLen+cur_len))
        if len(res)%step==0:
            cur_len+=1

    # print(res)
    return res


def genNotStartsWithCommonPrefix(start_str: str, prefix: str, size:int, len_lb=0, len_hb=1000, type="AlphaNumeric"):
    # print(start_str, prefix, size, len_lb, len_hb)

    res = []
    matchLen = len(prefix)
    len_lb = max(len_lb, matchLen)
    step = math.ceil(size / (len_hb - len_lb + 1))
    cur_len = 0
    while len(res) < size:
        t=generate(cur_len, type)
        while t!="" and t[0]==start_str[matchLen]:
            t = generate(cur_len, type)
        res.append((prefix + t, matchLen + cur_len))
        if len(res) % step == 0:
            cur_len += 1


    # print(res)
    return res

def genNotStartsWith(start_str: str, size:int, len_lb=0, len_hb=1024, type="AlphaNumeric"):
    res=[]
    matchLen=len(start_str)
    unit=math.ceil(size/matchLen)
    # print(start_str, size, len_lb, len_hb, unit)

    cur_matchLen=0
    while len(res)<size:
        res.extend(genNotStartsWithCommonPrefix(start_str,start_str[:cur_matchLen],unit,cur_matchLen,len_hb,type))
        cur_matchLen+=1

    # print(res)
    return res


def genContains(contain_str: str, size:int, len_lb=0, len_hb=1024, type="AlphaNumeric"):
    res=[]
    matchLen=len(contain_str)

    max_len=len_hb-matchLen
    unit=math.ceil(2*size/((max_len+1)*(max_len+2)))

    prefix_len=0
    while len(res)<size:
        for suffix_len in range(max_len+1-prefix_len):
            prefixes=generate_batch(unit,prefix_len, type, lambda x: contain_str in x)
            suffixes=generate_batch(unit,suffix_len, type, lambda x: contain_str in x)

            for prefix,suffix in zip(prefixes,suffixes):
                res.append((''.join([prefix,contain_str,suffix]),prefix_len+matchLen+suffix_len,prefix_len))

        prefix_len+=1
    # print(res)
    return res


def genNotContains(contain_str: str, size:int, len_lb=0, len_hb=1024, type="AlphaNumeric"):
    ''' can we add the longest common prefix?'''
    unit=math.ceil(size/(len_hb+1))
    cur_len=0
    res=[]
    while len(res)<size:
        # print("generate len:", cur_len, "count:", unit, len(res))
        res.extend((gen_str, cur_len) for gen_str in generate_batch(unit,cur_len,type,exclude=lambda x: contain_str in x))
        cur_len+=1
    return res


def save_to_file(obj: list, filename: str) -> None: #for starts with
    pickle.dump(obj, open(filename[:-4]+".p", "wb" ))
    with open(filename, mode='wt', encoding='utf-8') as myfile:
        myfile.write("string, length\n")
        for gen_str,length in obj:
            myfile.write(gen_str+", "+str(length)+"\n")
        myfile.write('\n')

def save_to_file2(obj: list, filename: str) -> None: #for starts with
    pickle.dump(obj, open(filename[:-4]+".p", "wb"))
    with open(filename, mode='wt', encoding='utf-8') as myfile:
        myfile.write("string, length, firstMatchPos\n")
        for gen_str,length, firstMatch_pos in obj:
            myfile.write(gen_str +", " + str(length) +", " + str(firstMatch_pos) + '\n')
        myfile.write('\n')

def asserted(obj: list, verify):
    for gen_str, *rest in obj:
        assert verify(gen_str), "wrong string generated: "+gen_str

if __name__ == "__main__":
    # t=generate(0,"AlphaNumeric")
    # t = generate(10, "AlphaNumeric")
    # print(t,t=="")

    res=genStartsWith("abc",100,0,20)
    asserted(res,lambda x: x[:3]=="abc")
    save_to_file(res,"test.csv")

    res2=genNotStartsWith("abc",100,0,20)
    asserted(res2, lambda x: x[:3] != "abc")
    save_to_file(res2, "test2.csv")

    res3 = genContains("error", 100, 0, 20)
    asserted(res3, lambda x: "error" in x)
    save_to_file2(res3, "test3.csv")

    res4 = genNotContains("error", 100, 0, 20)
    asserted(res4, lambda x: "error" not in x)
    save_to_file(res4, "test4.csv")