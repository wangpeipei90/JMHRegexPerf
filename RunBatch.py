import pickle
import subprocess
import os

if __name__ == "__main__":
    print(os. getcwd())
    assert os.path.exists("target/regexbenchmarks.jar"), "jmh jar file not found!!"

    benchmark_class="org.ncsu.regex.perf2.JavaContains"
    regex=".*error.*"
    contain_str="error"

    matches=pickle.load(open("test3.p", "rb" ))
    not_matches = pickle.load(open("test4.p", "rb"))

    cmd=[
        "java -jar target/regexbenchmarks.jar",benchmark_class,"-f 1 -gc true -wi 10 -i 50 -rf csv",
         '-p regex="'+regex+'"', '-p str="'+contain_str+'"'
         ]

    for idx,(match,*rest) in enumerate(matches):
        cmd2=list(cmd)
        cmd2.append('-p testString="'+match+'"')
        cmd2.append('-p expectation="true"')
        cmd2.append("-rff contains_error_iter50_match_"+str(idx)+".csv")
        cmd2.append("-o log/contains_error_iter50_match_"+str(idx) + ".log")

        print(' '.join(cmd2))
        # subprocess.run(cmd2)
        break

    for idx, (not_match,*rest) in enumerate(not_matches):
        cmd2 = list(cmd)
        cmd2.append('-p testString="'+not_match+'"')
        cmd2.append('-p expectation="false"')
        cmd2.append("-rff contains_error_iter50_not_match_" + str(idx) + ".csv")
        cmd2.append("-o log/contains_error_iter50_not_match_" + str(idx) + ".log")

        print(' '.join(cmd2))
        # subprocess.call(cmd2)
        break