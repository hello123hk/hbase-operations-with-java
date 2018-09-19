

hbase shell <<EOF
truncate 'batch'
EOF


if [ $# -ne 7 ]; then
    echo "aaaaaaaaaaaa"
    exit 1
fi

sleep 10


cd /home/work/wx/hbase-operations-with-java/
MY_CLASSPATH=/home/work/wx/hbase-operations-with-java/lib/*:/home/work/wx/hbase-operations-with-java/target/original-hcg-1.0-SNAPSHOT.jar
MY_CLASSPATH=$MY_CLASSPATH:`hadoop classpath`:`hbase classpath`

date
java -Xmx10240m -Xms10240m  -cp $MY_CLASSPATH  cn.ngsoc.hbase.client.MultiThreadedClientExample   $1 $2 $3 $4 $5 $6 $7
date

