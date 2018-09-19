/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * Example on how to use HBase's {@link Connection} and {@link Table} in a
 * multi-threaded environment. Each table is a light weight object
 * that is created and thrown away. Connections are heavy weight objects
 * that hold on to zookeeper connections, async processes, and other state.
 *
 * <pre>
 * Usage:
 * bin/hbase org.apache.hadoop.hbase.client.example.MultiThreadedClientExample testTableName 500000
 * </pre>
 *
 * <p>
 * The table should already be created before running the command.
 * This example expects one column family named d.
 * </p>
 * <p>
 * This is meant to show different operations that are likely to be
 * done in a real world application. These operations are:
 * </p>
 *
 * <ul>
 *   <li>
 *     30% of all operations performed are batch writes.
 *     30 puts are created and sent out at a time.
 *     The response for all puts is waited on.
 *   </li>
 *   <li>
 *     20% of all operations are single writes.
 *     A single put is sent out and the response is waited for.
 *   </li>
 *   <li>
 *     50% of all operations are scans.
 *     These scans start at a random place and scan up to 100 rows.
 *   </li>
 * </ul>
 *
 */
public class MultiThreadedScanClientExample extends Configured implements Tool {
    private static final Log LOG = LogFactory.getLog(MultiThreadedClientExample.class);
    private static final int DEFAULT_NUM_OPERATIONS = 50;

    /**
     * The name of the column family.
     *
     * d for default.
     */
    private static final byte[] FAMILY = Bytes.toBytes("d");

    /**
     * For the example we're just using one qualifier.
     */
    private static final byte[] QUAL = Bytes.toBytes("test");

    private final ExecutorService internalPool;

    private final int threads;
    private final List<byte[]> rowkeys;

    public MultiThreadedScanClientExample(int threads,List<byte[]> rowkeys) throws IOException {
        // Base number of threads.
        // This represents the number of threads you application has
        // that can be interacting with an hbase client.
//        this.threads = Runtime.getRuntime().availableProcessors() * 4;
        this.threads = threads;
        this.rowkeys = rowkeys;

        // Daemon threads are great for things that get shut down.
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true).setNameFormat("internal-pol-%d").build();


        System.out.println("#####################threads:"+threads);

        this.internalPool = Executors.newFixedThreadPool(threads, threadFactory);
    }

    @Override
    public int run(String[] args) throws Exception {

        int numOperations = DEFAULT_NUM_OPERATIONS;
        int value_size = 50;
        int rows = 50;
        int cf = 1;
        int c = 1;
        boolean wal = true;
        int rak = 3;


        if (args.length < 1 || args.length > 8) {
            System.out.println("Usage: " + this.getClass().getName() + " tableName [num_operations]");
            return -1;
        }else{
            value_size = Integer.parseInt(args[1]);
            numOperations = Integer.parseInt(args[2]);
            rows = Integer.parseInt(args[3]);
            cf = Integer.parseInt(args[4]);
            c = Integer.parseInt(args[5]);
            rak = Integer.parseInt(args[6]);
            if (Integer.parseInt(args[7]) == 0) {
                wal = false;
            }
        }
        numOperations = 1;

        final TableName tableName = TableName.valueOf("batch");

        System.out.println("#####################numOperations:"+numOperations);
        System.out.println("#####################value_size:"+value_size);
        System.out.println("#####################rows:"+(numOperations*rows));
        System.out.println("#####################cf:"+ cf);
        System.out.println("#####################c:"+ c);
        System.out.println("#####################wal:"+ wal);

        // Threads for the client only.
        //
        // We don't want to mix hbase and business logic.
        //
        ExecutorService service = new ForkJoinPool(threads * 2);

        // Create two different connections showing how it's possible to
        // separate different types of requests onto different connections
        Configuration conf = getConf();
        conf.set("hbase.zookeeper.quorum","web1:2181,web2:2181,web3:2181");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
//        conf.set("hbase.regionserver.handler.count", "120");

        final Connection writeConnection = ConnectionFactory.createConnection(conf, service);
        final Connection readConnection = ConnectionFactory.createConnection(conf, service);

        // At this point the entire cache for the region locations is full.
        // Only do this if the number of regions in a table is easy to fit into memory.
        //
        // If you are interacting with more than 25k regions on a client then it's probably not good
        // to do this at all.
        warmUpConnectionCache(readConnection, tableName);
        warmUpConnectionCache(writeConnection, tableName);

        List<Future<Boolean>> futures = new ArrayList<>(numOperations);
        for (int i = 0; i < numOperations; i++) {
//            double r = ThreadLocalRandom.current().nextDouble();
            Future<Boolean> f;

            // For the sake of generating some synthetic load this queues
            // some different callables.
            // These callables are meant to represent real work done by your application.
            f = internalPool.submit(new ReadExampleCallable(writeConnection, tableName,rowkeys));

//            if (r < .30) {
//                f = internalPool.submit(new WriteExampleCallable(writeConnection, tableName,size));
//            } else if (r < .50) {
//                f = internalPool.submit(new SingleWriteExampleCallable(writeConnection, tableName));
//            } else {
//                f = internalPool.submit(new ReadExampleCallable(writeConnection, tableName));
//            }
            futures.add(f);
        }

        // Wait a long time for all the reads/writes to complete
        for (Future<Boolean> f : futures) {
            f.get(10, TimeUnit.HOURS);
        }

        // Clean up after our selves for cleanliness
        internalPool.shutdownNow();
        service.shutdownNow();
        return 0;
    }

    private void warmUpConnectionCache(Connection connection, TableName tn) throws IOException {
        try (RegionLocator locator = connection.getRegionLocator(tn)) {
            LOG.info(
                    "Warmed up region location cache for " + tn
                            + " got " + locator.getAllRegionLocations().size());
        }
    }

    /**
     * Class that will show how to send batches of puts at the same time.
     */
    public static class WriteExampleCallable implements Callable<Boolean> {
        private final Connection connection;
        private final TableName tableName;
        private final int value_size;
        private final int rows;
        private final int cf;
        private final int c;
        private final boolean wal;
        private final int rak;
        private final List<byte[]> rowkeys;


        public WriteExampleCallable(Connection connection, TableName tableName,int value_size,int rows,int cf,int c,boolean wal,
                                    List<byte[]> rowkeys,int rak) {
            this.connection = connection;
            this.tableName = tableName;
            this.value_size = value_size;
            this.rows = rows;
            this.cf = cf;
            this.c = c;
            this.wal = wal;
            this.rak = rak;
            this.rowkeys = rowkeys;
        }

        public static byte[] createValue(long rowId,int value_size) {
            if (rowId % 9 == 0 & rowId % 7 == 0 & rowId % 13 == 0 ) {
                // 100MB 104857600
                value_size = 104857600;
                System.out.println("#######################RANDOM_100MB_VALUE: \t" + (value_size/1024/1024) + "MB");
            }

            Random r = new Random(rowId);
            byte value[] = new byte[value_size];

            r.nextBytes(value);

            // transform to printable chars
            for (int j = 0; j < value.length; j++) {
                value[j] = (byte) (((0xff & value[j]) % 92) + ' ');
            }

            return value;
        }

        private synchronized List<byte[]> createRandomList(List<byte[]> list, int n) {
            Collections.shuffle(list);
            Map map = new HashMap();
            List<byte[]> rowkeys = new ArrayList<>();
            if (list.size() <= n) {
                return list;
            } else {
                while (map.size() < n) {
                    int random = (int) (Math.random() * list.size());
                    if (!map.containsKey(random)) {
                        map.put(random, "");
                        rowkeys.add(list.get(random));
                    }
                }
                return rowkeys;
            }
        }



        private synchronized ArrayList<Put> generateData(int value_size, int cf, int c,boolean wal) {
            ArrayList<Put> puts = new ArrayList<>(rows);
            List<byte[]> tmpKeys = new ArrayList<>();

            for (int i = 0; i < rows; i++) {
                byte[] rk = UUID.randomUUID().toString().getBytes();
                tmpKeys.add(rk);
//                byte[] rk = String.format("%010d_row", i).getBytes();
                Put put = new Put(rk);
                for (int j = 0; j <cf ; j++) {
                    for (int k = 0; k < c; k++) {
                        put.addImmutable(Bytes.toBytes("foo"+j), Bytes.toBytes("c"+k), createValue(i,value_size));
                        put.setWriteToWAL(wal);
//                        System.out.println(("foo"+j)+("c"+k));
                    }
                }
                puts.add(put);
            }
            rowkeys.addAll(createRandomList(tmpKeys, (rows / rak)));
            tmpKeys.clear();
            return puts;
        }

        @Override
        public Boolean call() throws Exception {


            // Table implements Closable so we use the try with resource structure here.
            // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
            try (Table t = connection.getTable(tableName)) {
                long start = System.currentTimeMillis();
                ArrayList<Put> puts = generateData(value_size,cf,c,wal);
                System.out.println("#######################GENERATEDATA_TIME: \t" + (System.currentTimeMillis() - start));


                start = System.currentTimeMillis();
                t.put(puts);
                System.out.println("#######################INSERT_TIME: \t" + (System.currentTimeMillis() - start));
                puts.clear();

            }
            System.out.println("#######################INSERT_ROWS: \t" + rows);

            return true;
        }
    }

    /**
     * Class to show how to send a single put.
     */
    public static class SingleWriteExampleCallable implements Callable<Boolean> {
        private final Connection connection;
        private final TableName tableName;

        public SingleWriteExampleCallable(Connection connection, TableName tableName) {
            this.connection = connection;
            this.tableName = tableName;
        }

        @Override
        public Boolean call() throws Exception {
            try (Table t = connection.getTable(tableName)) {

                byte[] value = Bytes.toBytes(Double.toString(ThreadLocalRandom.current().nextDouble()));
                byte[] rk = Bytes.toBytes(ThreadLocalRandom.current().nextLong());
                Put p = new Put(rk);
                p.addImmutable(FAMILY, QUAL, value);
                t.put(p);
            }
            return true;
        }
    }


    /**
     * Class to show how to scan some rows starting at a random location.
     */
    public static class ReadExampleCallable implements Callable<Boolean> {
        private final Connection connection;
        private final TableName tableName;
        private final List<byte[]> rowkeys;

        public ReadExampleCallable(Connection connection, TableName tableName,List<byte[]> rowkeys) {
            this.connection = connection;
            this.tableName = tableName;
            this.rowkeys = rowkeys;
        }

        @Override
        public Boolean call() throws Exception {

            // total length in bytes of all read rows.
//            int result = 0;
            // Number of rows the scan will read before being considered done.
//            int toRead = 100;
            try (Table t = connection.getTable(tableName)) {

                long t1 = System.currentTimeMillis();
                long lookups = 0;

                for (byte[] rk : rowkeys) {
                    if(rk == null)
                        break;
//                    byte[] rk = Bytes.toBytes(ThreadLocalRandom.current().nextLong());
                    Scan s = new Scan(rk);

                    // This filter will keep the values from being sent accross the wire.
                    // This is good for counting or other scans that are checking for
                    // existence and don't rely on the value.
                    s.setFilter(new KeyOnlyFilter());

                    // Don't go back to the server for every single row.
                    // We know these rows are small. So ask for 20 at a time.
                    // This would be application specific.
                    //
                    // The goal is to reduce round trips but asking for too
                    // many rows can lead to GC problems on client and server sides.
                    s.setCaching(0);

                    // Don't use the cache. While this is a silly test program it's still good to be
                    // explicit that scans normally don't use the block cache.
                    s.setCacheBlocks(false);

                    // Open up the scanner and close it automatically when done.
                    try (ResultScanner rs = t.getScanner(s)) {
                        // Now go through rows.
//                        for (Result r : rs) {
//                             //Keep track of things size to simulate doing some real work.
//                            int rowlen = r.getRow().length;
//                            System.out.println("rowkey : " + new String(r.getRow()) + "  ");
//                        }
                    }

                    lookups++;
                    if (lookups % 100 == 0) {
                        System.out.println(lookups+" lookups");
                    }
                }

                long t2 = System.currentTimeMillis();
                System.out.println(String.format("#######################Hbase Random Batch Scan finished! %6.2f rows/sec, %.2f secs, %d scanned",
                        lookups / ((t2 - t1) / 1000.0), ((t2 - t1) / 1000.0), lookups));
            }

            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        long currentTime = System.currentTimeMillis();
        List<byte[]> rowkeys = new ArrayList<>();
        LOG.info("#######################Hbase Random Batch write start:");
        ToolRunner.run(new MultiThreadedClientExample(Integer.parseInt(args[0]),rowkeys), args);
        for (byte[] a: rowkeys){
            System.out.println(a);
        }
        System.out.println("#######################Hbase Random Batch write ALL_TIME: \t"+(System.currentTimeMillis() - currentTime));
        LOG.info("#######################Hbase Random Batch write end:");


        System.out.println("####################### 随机 key 采集策略, rowkey 汇总之后并打散 , 总随机 key 为线程数 * row/随机因子 : \t" + rowkeys.size());

        LOG.info("#######################Hbase Random Batch Scanner start:");
        currentTime = System.currentTimeMillis();

        ToolRunner.run(new MultiThreadedScanClientExample(Integer.parseInt(args[0]),rowkeys), args);

        System.out.println("#######################Hbase Random Batch Scanner ALL_TIME: \t"+(System.currentTimeMillis() - currentTime));
        LOG.info("#######################Hbase Random Batch Scanner end:");


    }
}