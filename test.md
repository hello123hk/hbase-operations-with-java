mvn dependency:copy-dependencies -DoutputDirectory=lib   -DincludeScope=compile


create 'batch', {NAME => 'foo', VERSIONS => 1}, 
{NAME => 'etldr', VERSIONS => 1}, 
{NAME => 'etldrclean', VERSIONS => 1},
{NAME => 'emoi', VERSIONS => 1},
{NAME => 'empi', VERSIONS => 1},
{NAME => 'schema', VERSIONS => 1},
{NAME => 'vpid', VERSIONS => 1},
{NAME => 'etldrnorm', VERSIONS => 1},
{NAME => 'pp', VERSIONS => 1},
{NAME => 'soar', VERSIONS => 1}



disable 'batch'
drop 'batch'

create 'batch', {NAME => 'foo', VERSIONS => 1}

truncate 'batch'

count 'batch', INTERVAL => 100000

scan 'batch',{LIMIT=>1}






scan 'hbase:meta',{FILTER=>"PrefixFilter('batch')"}





hbase shell <<EOF 
disable 'batch'
drop 'batch'
EOF



hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 12 -f foo0
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo0:foo1:foo2
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo0:foo1:foo2:foo3:foo4:foo5


hbase shell <<EOF 
scan 'hbase:meta',{FILTER=>"PrefixFilter('batch')"}

scan 'batch',{LIMIT=>1}
describe 'batch'

truncate 'batch'

EOF


hbase shell <<EOF 
scan 'batch',{LIMIT=>1}
EOF


hbase shell <<EOF 
count 'batch', INTERVAL => 100000

EOF






cd /home/work/wx/hbase-operations-with-java/
MY_CLASSPATH=/home/work/wx/hbase-operations-with-java/lib/*:/home/work/wx/hbase-operations-with-java/target/original-hcg-1.0-SNAPSHOT.jar
MY_CLASSPATH=$MY_CLASSPATH:`hadoop classpath`:`hbase classpath`
java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16  1024  20 10000 1 1 0



# 参数说明: 线程池大小, value 大小, 线程数, 每个线程处理数据条数,列簇数量,列数量,是否开启wal(0,不开启)

cat hbase_times.txt |grep -E "GENERATEDATA_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat hbase_times.txt |grep -E "INSERT_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat hbase_times.txt |grep -E "ALL_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat hbase_times.txt |grep -E "INSERT_ROWS" | awk -F '\t' '{sum += $2} END {print sum}'


hbase shell <<EOF 
alter 'batch', {MEMSTORE_FLUSHSIZE => '536870912'}
EOF


hbase shell <<EOF 
disable 'batch'
drop 'batch'
create 'batch', {NAME => 'foo0', VERSIONS=> 1},{SPLITS => ['1','3','5','7','9','0','a','d','h','l','p','t','x','z']}
scan 'hbase:meta',{FILTER=>"PrefixFilter('batch')"}
EOF





sh hbase_delete.sh foo0
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5

# 参数说明: 线程池大小, value 大小, 线程数, 每个线程处理数据条数,列簇数量,列数量,随机因子,是否开启wal(0,不开启)
sh hbase_run_wx.sh  16  5  10 100 1 1 3 1


## 100w
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9
nohup   sh hbase_run_wx.sh 10  102400  1250 800  10 1  3  1 > hbase_times_100w_100_10_1.txt &


nohup   sh hbase_run_wx.sh 10  1048576  4000 50  10 1  6  1 > hbase_times_20w_1024_10_1.txt &



sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9:foo10:foo11:foo12:foo13:foo14:foo15:foo16:foo17:foo18:foo19
nohup   sh hbase_run_wx.sh 10  512000  200 50  20 1  3  1 > hbase_times_2w_500_20_1.txt &



sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9
nohup   sh hbase_run_wx.sh 10  512000  100 100  10 1  3  1 > hbase_times_1w_500_10_1.txt &



sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9
nohup   sh hbase_run_wx.sh 10  1048576  100 50  10 1  3  1 > hbase_times_5k_500_10_1.txt &



50-20
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9:foo10:foo11:foo12:foo13:foo14:foo15:foo16:foo17:foo18:foo19
nohup   sh hbase_run_wx.sh 4  1048576  100 50  20 1  3  1 > hbase_times_1w_500_10_1.txt &


5000000-50-1
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5:foo6:foo7:foo8:foo9:foo10:foo11:foo12:foo13:foo14:foo15:foo16:foo17:foo18:foo19:foo20:foo21:foo22:foo23:foo24:foo25:foo26:foo27:foo28:foo29:foo30:foo31:foo32:foo33:foo34:foo35:foo36:foo37:foo38:foo39:foo40:foo41:foo42:foo43:foo44:foo45:foo46:foo47:foo48:foo49:foo50
nohup   sh hbase_run_wx.sh 20  1024  1000 5000  50 1  3  1 > hbase_times_500w_1024_50_1.txt &


客户端内存: 20*1 * 50 * 5000 / 1024./1024 约等于5G





# 参数说明: 线程池大小, value 大小, 线程数, 每个线程处理数据条数,列簇数量,列数量,随机因子,是否开启wal(0,不开启)

## 20w

sh hbase_run_wx.sh  16  1024  20 10000 5 1 3 1


nohup  sh hbase_run_wx.sh 16 10240  20 10000 3 1 3 1  > hbase_times.txt &
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5  ;     sh hbase_run_wx.sh 16 10240  200 1000 5 1 3 1 



sh hbase_delete.sh foo0
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5

nohup  sh hbase_run_wx.sh 16 102400  200 1000  5 1 3 1 > hbase_times.txt &


nohup  sh hbase_run_wx.sh 16 102400  250 800 1 1 1 > hbase_times.txt &




---

## 50w

nohup  sh hbase_run_wx.sh 16 1024  50 10000 5 1 1 > hbase_times.txt &

nohup  sh hbase_run_wx.sh 16 10240  500 1000 5 1 1 > hbase_times.txt &


sh hbase_delete.sh foo0
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5


nohup  sh hbase_run_wx.sh 16 102400  500 1000 3 1 1 > hbase_times.txt &




---

## 100w

sh hbase_delete.sh foo0
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5

nohup  sh hbase_run_wx.sh 16 1024  100 10000 3 1 0 > hbase_times.txt &



nohup  sh hbase_run_wx.sh 16 10240  100 10000 1 1 1 > hbase_times.txt &

nohup  sh hbase_run_wx.sh 16 10240  1250 800 1 1 1 > hbase_times.txt &

nohup  sh hbase_run_wx.sh 16 102400  1250 800 1 1 1 > hbase_times.txt &


nohup  sh hbase_run_wx.sh 16 1048576  12500 80 1 1 1 > hbase_times.txt &



##############################################################  1FC

## 生成1T 数据
#客户端内存:  8 *102400 * 10000  / 1024./1024/1024
#数据量大小为: 1 * 102400 * 1000 * 10000 / 1024./1024/1024
sh hbase_delete.sh foo0
nohup  sh hbase_run_wx.sh 8 102400  1000 10000 1 1 9 1 > hbase_times.txt &


##############################################################################
#############hbase 1000 1FC  100MB   
#客户端内存:  6 *104857600 * 10  / 1024./1024/1024
sh hbase_run_wx.sh 6 104857600  100 10 1 1 5 1  > new_hbase_times_1k_100MB_1_1.txt 


#############hbase 1000 1FC  150MB   
#客户端内存:  2 *157286400 * 4  / 1024./1024/1024
echo "new_hbase_times_1k_150MB_1_1  `hadoop fs -du -h /hbase/data/default/|grep batch`"    
sh hbase_run_wx.sh 1 157286400  1000 1 1 1 5 1  > new_hbase_times_1k_150MB_1_1.txt 

#############hbase 1000 1FC  200MB   
#客户端内存:  2 *209715200 * 4  / 1024./1024/1024
echo "new_hbase_times_1k_200MB_1_1  `hadoop fs -du -h /hbase/data/default/|grep batch`"    
sh hbase_run_wx.sh 1 209715200  1000 1 1 1 5 1  > new_hbase_times_1k_200MB_1_1.txt 

#############hbase 1000 1FC  250MB   
#客户端内存:  2 *262144000 * 4  / 1024./1024/1024
echo "new_hbase_times_1k_250MB_1_1  `hadoop fs -du -h /hbase/data/default/|grep batch`"    
sh hbase_run_wx.sh 1 209715200  1000 1 1 1 5 1  > new_hbase_times_1k_250MB_1_1.txt 




##############################################################################
#############hbase 1000 5FC  100MB   
#客户端内存:  2 *104857600 * 5 * 5  / 1024./1024/1024
sh hbase_run_wx.sh 2 104857600  200 5 5 1 5 1 > new_hbase_times_1k_100MB_5_1.txt 

#############hbase 1000 5FC  150MB   
#客户端内存:  2 *157286400 * 4  * 5 / 1024./1024/1024
sh hbase_run_wx.sh 2 157286400  250 4 5 1 5 1 > new_hbase_times_1k_150MB_5_1.txt 

#############hbase 1000 5FC  200MB   
#客户端内存:  3 *209715200 * 2  * 5 / 1024./1024/1024
sh hbase_run_wx.sh 3 209715200  500 2 5 1 5 1 > new_hbase_times_1k_200MB_5_1.txt 

#############hbase 1000 5FC  250MB   
#客户端内存:  2 *262144000 * 2  * 5 / 1024./1024/1024
sh hbase_run_wx.sh 2 209715200  500 2  5 1 5 1 > new_hbase_times_1k_250MB_5_1.txt 










## 生成1T 数据
nohup   sh acc_run_wx.sh 8 102400  1000 10000 1 1 9  > acc_times.txt &

##############################################################################
#############acc 1000 1FC  100MB   
#客户端内存:  6 *104857600 * 10  / 1024./1024/1024
sh acc_run_wx.sh 6 104857600  100 10 1 1 5  > new_acc_times_1k_100MB_1_1.txt 

#############acc 1000 1FC  150MB   
#客户端内存:  4 *157286400 * 10  / 1024./1024/1024
sh acc_run_wx.sh 4 157286400  100 10 1 1 5  > new_acc_times_1k_150MB_1_1.txt 

#############acc 1000 1FC  200MB   
#客户端内存:  3 *209715200 * 10  / 1024./1024/1024
sh acc_run_wx.sh 3 209715200  100 10 1 1 5  > new_acc_times_1k_200MB_1_1.txt 

#############acc 1000 1FC  250MB   
#客户端内存:  2 *262144000 * 10  / 1024./1024/1024
sh acc_run_wx.sh 2 209715200  100 10 1 1 5  > new_acc_times_1k_250MB_1_1.txt 


##############################################################################
#############acc 100 5FC  100MB   
#客户端内存:  2 *104857600 * 5 * 5  / 1024./1024/1024
sh acc_run_wx.sh 1 104857600  100 1 5 1 5  > new_acc_times_100_100MB_5_1.txt 

#############acc 100 5FC  150MB   
#客户端内存:  2 *157286400 * 4  * 5 / 1024./1024/1024
sh acc_run_wx.sh 1 157286400   100 1 5 1 5  > new_acc_times_100_150MB_5_1.txt 

#############acc 100 5FC  200MB   
#客户端内存:  3 *209715200 * 2  * 5 / 1024./1024/1024
sh acc_run_wx.sh 1 209715200   100 1 5 1 5  > new_acc_times_100_200MB_5_1.txt 

#############acc 100 5FC  250MB   
#客户端内存:  2 *262144000 * 2  * 5 / 1024./1024/1024
sh acc_run_wx.sh 1 209715200   100 1  5 1 5  > new_acc_times_100_250MB_5_1.txt 



hbase shell <<EOF 
disable 'traceinfo_test_wx'
drop 'traceinfo_test_wx'
EOF

hbase org.apache.hadoop.hbase.util.RegionSplitter traceinfo_test_wx HexStringSplit -c 24 -f  f1



cd /home/work/wx/hbase-operations-with-java/
MY_CLASSPATH=/home/work/wx/hbase-operations-with-java/lib/*:/home/work/wx/hbase-operations-with-java/target/original-hcg-1.0-SNAPSHOT.jar
MY_CLASSPATH=$MY_CLASSPATH:`hadoop classpath`:`hbase classpath`
java  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedScanClientExample  1 1 1 1 1 1 1 1













cat new_acc_times_100_250MB_5_1.txt |grep -E "Random Batch write ALL_TIME|Random Batch Scan finished"
