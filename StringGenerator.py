import random
import string

character_set=["AlphaNumeric","Printable"] #"ASCII","Unicode"]

def generate(length: int, type: str):
    if length==0:
        return ''

    if str == "AlphaNumeric":
        return ''.join([random.choice(string.ascii_letters + string.digits) for _ in range(length)])
    elif str == "Printable":
        return ''.join([random.choice(string.printable) for _ in range(length)])


def genStartsWith(start_str: str, size:int, len_lb=0, len_hb=1000, type="AlphaNumeric"):
    res=[]
    matchLen=len(start_str)
    step=size//(len_hb-len_lb+1) +1
    cur_len=0
    while len(res)<size:
        res.append((generate(cur_len,type),cur_len))
        if len(res)%step==0:
            cur_len+=1

    res=
    pass


if __name__ == "__main__":
    t=generate(0,"AlphaNumeric")
    print(t,t=="")