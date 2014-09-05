package com.ivan

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HTable,Put}
import org.apache.hadoop.hbase.util.Bytes

object Schema {
  lazy val conf = HBaseConfiguration.create()
  lazy val table = new HTable(conf, "metrics")

  def put(row: String, v: Stat) = {
    val theput = new Put(Bytes.toBytes(row))
    theput.add(Bytes.toBytes("data"), Bytes.toBytes("min"), Bytes.toBytes(v.min))
    theput.add(Bytes.toBytes("data"), Bytes.toBytes("avg"), Bytes.toBytes(v.avg))
    theput.add(Bytes.toBytes("data"), Bytes.toBytes("max"), Bytes.toBytes(v.max))
    table.put(theput)
  }
}