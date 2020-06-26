library(ggplot2)
setwd("D://workspace_java//JMHRegexPerf")

result_file_notCompiled_matched="regexNotcompiledMethod_warm10_iter100_batch10_res.csv"
df_notCompiled_matched=read.csv(result_file_notCompiled_matched)
df_notCompiled_matched$type="regexNotcompiled"

result_file_stringContains_matched="stringContainsMethod_warm10_iter10000_batch20.csv"
df_stringContains_matched=read.csv(result_file_stringContains_matched)
df_stringContains_matched$type="stringContains"

result_file_preCompiled_matched="regexPrecompiledMethod_warm10_iter100_batch10_res.csv"
df_preCompiled_matched=read.csv(result_file_preCompiled_matched)
df_preCompiled_matched$type="regexPrecompiled"


data=rbind(df_notCompiled_matched,df_stringContains_matched,df_preCompiled_matched)

png("11.png")
ggplot(df_stringContains_matched, aes(x = length, y = Score/20)) + 
  geom_line(aes(color = type, linetype = type)) + 
  scale_color_manual(values = c("darkred", "steelblue","green"))+
  ggtitle("Execution time Comparision BatchSize 20")
dev.off()

summary(df_notCompiled_matched$Score)
summary(df_stringContains_matched$Score)
summary(df_preCompiled_matched$Score)
# > summary(df_preCompiled_matched$Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
# 2575    3365    4005    4067    4694    6462


# > summary(res_regex$value.Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max.
# 520.4   614.7   673.9   662.9   705.8   788.1

###cur
# > summary(df_notCompiled_matched$Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
# 5290    6292    6905    6947    7576    8969

result_file_notCompiled_matched="regexNotcompiledMethod_warm10_iter100_batch10_res.csv"
df_notCompiled_matched=read.csv(result_file_notCompiled_matched)


# > summary(res_string$value.Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max.
# 17.57   18.02   18.58   20.39   23.23   24.85

### cur
# > summary(df_stringContains_matched$Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
# 157.4   162.6   172.3   189.3   216.6   261.2 



# > summary(res_baseline$value.Score)
# Min. 1st Qu.  Median    Mean 3rd Qu.    Max.
# 5.895   5.952   5.984   5.986   6.013   6.186 

## cur 
# 61.479 ? 1.045/
