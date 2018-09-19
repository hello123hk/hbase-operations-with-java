
Based on hbase 1.2.4 , multi-methods to operate hbase.  
## Undo list.
*  1.asynchbase
*  2.sparkonhbase





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




disable 'batch'
drop 'batch'


hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo:etldr
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo:etldr:etldrclean
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo:etldr:etldrclean:emoi
hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f foo:etldr:etldrclean:emoi:empi

scan 'batch',{LIMIT=>1}






cd /home/work/wx/hbase-operations-with-java/
MY_CLASSPATH=/home/work/wx/hbase-operations-with-java/lib/*:/home/work/wx/hbase-operations-with-java/target/original-hcg-1.0-SNAPSHOT.jar
MY_CLASSPATH=$MY_CLASSPATH:`hadoop classpath`:`hbase classpath`


#java -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.SequentialBatchWriter  10000000   50   500000
#java -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.SequentialBatchWriter  2300000   50   575000


# 参数说明: 线程池大小, 表名, value 大小, 线程数, 每个线程处理数据条数.

## 20w
java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 1024  20 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 10240  20 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample 16 batch 102400  250 800


---

## 50w
java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 1024  50 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 10240  50 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample 16 batch 102400  625 800



---

## 100w
java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 1024  100 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 10240  100 10000

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample  16 batch 10240  1250 800

java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample 16 batch 102400  1250 800


java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample 16 batch 1048576  12500 80





########################################################################################################################

export PATH=$PATH:/home/work/wx/accumulo/bin
accumulo shell -u root 


deletetable batch
createtable batch

config -t batch
config -t batch -s table.walog.enabled=false

addsplits -t batch g n t





cd /home/work/wx/accumulo-examples/
MY_CLASSPATH=/home/work/wx/accumulo-examples/lib/*:/home/work/accumulo-examples/lib/accumulo-examples-2.0.0-SNAPSHOT.jar
export PATH=$PATH:/home/work/wx/accumulo/bin
#java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  org.apache.accumulo.examples.client.MultiThreadedAccClientExample  $1 $2 $3 $4 $5 $6



accumulo shell -u root -p yidu -e "scan -t batch -np" |wc -l

accumulo shell -u root -p yidu -e "scan -t batch"

accumulo shell -u root -p yidu -e "config -t batch" 



echo yes| accumulo shell -u root -p yidu -e "deletetable batch"
accumulo shell -u root -p yidu -e "createtable batch" 
accumulo shell -u root -p yidu -e "addsplits -t batch 1 3 5 7 9 0 a e j m o t u z" 

accumulo shell -u root -p yidu -e "config -t batch -s table.walog.enabled=false"




nohup  sh run_wx_acc.sh  16  1024  1 2 5 1  > times.txt &

# 参数说明: 线程池大小, value 大小, 线程数, 每个线程处理数据条数,列簇数量,列数量.

cat times.txt |grep -E "GENERATEDATA_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat times.txt |grep -E "INSERT_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat times.txt |grep -E "ALL_TIME" | awk -F '\t' '{sum += $2} END {print sum}'
cat times.txt |grep -E "INSERT_ROWS" | awk -F '\t' '{sum += $2} END {print sum}'


## 20w


sh run_wx_acc.sh  16  1024  20 10000 5 1 


nohup  sh run_wx_acc.sh  16  10240  25 8000 5 1 > times.txt &


nohup  sh run_wx_acc.sh  16  102400  250 800 5 1  > times.txt &



## 50w

nohup  sh run_wx_acc.sh  16  1024  50 10000 5 1  > times.txt &


nohup  sh run_wx_acc.sh  16  10240  50 10000 3 1  > times.txt &

nohup  sh run_wx_acc.sh  16  10240  500 1000 5 1  > times.txt &



nohup  sh run_wx_acc.sh  16  102400  500 1000 1 1  > times.txt &






## 100w

sh run_wx_acc.sh 16  1024  100 10000

sh run_wx_acc.sh 16  10240  100 10000

sh run_wx_acc.sh 16  102400  1250 800







