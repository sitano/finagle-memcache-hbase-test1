package com.ivan

import java.net.{InetSocketAddress, SocketAddress}
import java.util.Date

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import org.jboss.netty.buffer.ChannelBuffer

import com.twitter.finagle.builder.{Server => BuiltServer, ServerBuilder}
import com.twitter.finagle.memcached.protocol.text.Memcached
import com.twitter.util.{SynchronizedLruMap, Await}
import com.twitter.io.Charsets

class Server(address: SocketAddress) {
  private[this] val storage = new Storage[ChannelBuffer, Entry]

  // TODO: this must be queue
  // TODO: lru here or another stategy on overflow
  private[this] val aggregator = new SynchronizedLruMap[String, Stat](10000)

  private[this] val service = {
    val interpreter = new Interpreter(storage)
    new InterpreterService(interpreter)
  }

  private[this] val serverSpec =
    ServerBuilder()
      .name("finagle")
      .codec(Memcached())
      .bindTo(address)

  private[this] var server: Option[BuiltServer] = None

  def start(): BuiltServer = {
    // TODO: extract it from here, write good start / stop, rewrite nice next time selection
    future {
      while (true) {
        // Simple and stupid walk all aggregated metrics every minute and push them on the queue
        Thread.sleep(60000)

        val ts = Math.round(System.currentTimeMillis / 60000.0) * 60000

        storage.lock { data =>
          data.foreach { p =>
            aggregator.put(
            // This is hbase key: metric_timestamp
              p._1.toString(Charsets.UsAscii) + "_" + ts,
            // Aggregated value
              Stat(p._2))
          }
        }

        System.out.println("Snapshot done at " + new Date(ts))

        // Try to put data into db
        future {
          System.out.println("TODO: push to HBase at " + new Date(ts))
        }
      }
    }

    server = Some(serverSpec.build(service))
    server.get
  }

  def stop(blocking: Boolean = false) {
    server.foreach { server =>
      if (blocking) Await.result(server.close())
      else server.close()
      this.server = None
    }
  }
}

object ServiceApp extends App {
  new Server(new InetSocketAddress("127.0.0.1", 11211)).start()
}