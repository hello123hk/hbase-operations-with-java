#!/usr/bin/env bash

if [ $# -ne 1 ]; then
    echo "false"
    exit 1
fi



hbase shell <<EOF
disable 'batch'
drop 'batch'
EOF


hbase org.apache.hadoop.hbase.util.RegionSplitter batch HexStringSplit -c 6 -f  $1


hbase shell <<EOF
scan 'hbase:meta',{FILTER=>"PrefixFilter('batch')"}

scan 'batch',{LIMIT=>1}
describe 'batch'

EOF
