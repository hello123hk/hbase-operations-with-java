/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ngsoc.hbase.client;

import cn.ngsoc.hbase.HBase;
import cn.ngsoc.hbase.HBaseServiceImpl;
import cn.ngsoc.hbase.util.HBaseUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Simple example for writing random data in sequential order to Accumulo.
 */
public class SequentialBatchWriter {

  private static final Logger log = LoggerFactory.getLogger(SequentialBatchWriter.class);

  public static byte[] createValue(long rowId,int size) {
    Random r = new Random(rowId);
    byte value[] = new byte[size];

    r.nextBytes(value);

    // transform to printable chars
    for (int j = 0; j < value.length; j++) {
      value[j] = (byte) (((0xff & value[j]) % 92) + ' ');
    }

    return value;
  }

  /**
   * Writes 1000 entries to Accumulo using a {@link BatchWriter}. The rows of the entries will be sequential starting from 0.
   * The column families will be "foo" and column qualifiers will be "1". The values will be random 50 byte arrays.
   */
  public static void main(String[] args) {
    HBaseUtil.init("web1:2181,web2:2181,web3:2181");
    int default_row = 10;
    int size = 50;
    int buff = 10000;
    if (args.length == 3) {
      default_row = Integer.valueOf(args[0]);
      size = Integer.valueOf(args[1]);
      buff = Integer.valueOf(args[2]);
    }
    long start = System.currentTimeMillis();
    System.out.println(" start insert db "+default_row+" rows: " + start);
    List<Put> puts = new ArrayList<>();
    for (int i = 0; i < default_row; i++) {
      Put put = new Put(Bytes.toBytes(String.format("row_%010d_%d", i,System.currentTimeMillis())));
      put.addColumn(Bytes.toBytes("foo"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("etldr"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("etldrclean"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("emoi"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("empi"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("schema"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("vpid"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("etldrnorm"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("pp"), Bytes.toBytes("severity"), createValue(i,size));
      put.addColumn(Bytes.toBytes("soar"), Bytes.toBytes("severity"), createValue(i,size));
      puts.add(put);

      if (i % buff == 0) {
        log.info("wrote {} entries", i);
      }
      if (puts.size() > buff) {
//        log.info("wrote {} entries", puts.size());
        HBase.put("batch", puts, true);
        puts.clear();
        puts = new ArrayList<>();
      }
    }
    HBase.put("batch", puts, true);
    puts.clear();

    long end = System.currentTimeMillis();
    System.out.println("end  insert db : " + end);
    System.out.println(" insert db use  : " + (end - start));
    System.exit(1);
  }
}
