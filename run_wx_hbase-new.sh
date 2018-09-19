#!/usr/bin/env bash



echo -e "20w    \t      100KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 102400  200 1000  5 1 0 > hbase_times_20w_100_5_0.txt

sleep 10
echo -e "50w    \t      100KB  \t  3fc  : "
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_run_wx.sh 16 102400  500 1000 3 1 0 > hbase_times_50w_100_3_0.txt


sleep 10
echo -e "50w    \t      100KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 102400  500 1000 5 1 1 > hbase_times_50w_100_5_1.txt



sleep 10
echo -e "50w    \t      100KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 102400  500 1000 5 1 0 > hbase_times_50w_100_5_0.txt



sleep 10
echo -e "100w    \t      1KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 1024  1000 1000 5 1 1 > hbase_times_100w_1_5_1.txt



sleep 10
echo -e "100w    \t      1KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 1024  1000 1000 5 1 0 > hbase_times_100w_1_5_0.txt



sleep 10
echo -e "100w    \t      10KB  \t  1fc  : "
sh hbase_delete.sh foo0
sh hbase_run_wx.sh 16 10240  100 10000 1 1 1 > hbase_times_100w_10_1_1.txt


sleep 10
echo -e "100w    \t      10KB  \t  1fc  : "
sh hbase_delete.sh foo0
sh hbase_run_wx.sh 16 10240  100 10000 1 1 0 > hbase_times_100w_10_1_0.txt


sleep 10
echo -e "100w    \t      10KB  \t  3fc  : "
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_run_wx.sh 16 10240  1000 1000 3 1 1 > hbase_times_100w_10_3_1.txt



sleep 10
echo -e "100w    \t      10KB  \t  3fc  : "
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_run_wx.sh 16 10240  1000 1000 3 1 0 > hbase_times_100w_10_3_0.txt


sleep 10
echo -e "100w    \t      10KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 10240  1000 1000 5 1 1 > hbase_times_100w_10_5_1.txt



sleep 10
echo -e "100w    \t      10KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 10240  1000 1000 5 1 0 > hbase_times_100w_10_5_0.txt


sleep 10
echo -e "100w    \t      100KB  \t  1fc  : "
sh hbase_delete.sh foo0
sh hbase_run_wx.sh 16 102400  1000 1000 1 1 1 > hbase_times_100w_100_1_1.txt


sleep 10
echo -e "100w    \t      100KB  \t  1fc  : "
sh hbase_delete.sh foo0
sh hbase_run_wx.sh 16 102400  1000 1000 1 1 0 > hbase_times_100w_100_1_0.txt


sleep 10
echo -e "100w    \t      100KB  \t  3fc  : "
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_run_wx.sh 16 102400  1000 1000 3 1 1 > hbase_times_100w_100_3_1.txt


sleep 10
echo -e "100w    \t      100KB  \t  3fc  : "
sh hbase_delete.sh foo0:foo1:foo2
sh hbase_run_wx.sh 16 102400  1000 1000 3 1 0 > hbase_times_100w_100_3_0.txt



sleep 10
echo -e "100w    \t      100KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 102400  1000 1000 5 1 1 > hbase_times_100w_100_5_1.txt


sleep 10
echo -e "100w    \t      100KB  \t  5fc  : "
sh hbase_delete.sh foo0:foo1:foo2:foo3:foo4:foo5
sh hbase_run_wx.sh 16 102400  1000 1000 5 1 0 > hbase_times_100w_100_5_0.txt



















