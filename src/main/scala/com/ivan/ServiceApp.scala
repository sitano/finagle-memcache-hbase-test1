package com.ivan

import java.net.{InetSocketAddress, SocketAddress}
import com.twitter.finagle.builder.{Server => BuiltServer, ServerBuilder}
import com.twitter.finagle.memcached.protocol.text.Memcached
import com.twitter.util.Await
import org.jboss.netty.buffer.ChannelBuffer

class Server(address: SocketAddress) {
  private[this] val service = {
    val interpreter = new Interpreter(new Storage[ChannelBuffer, Entry])
    new InterpreterService(interpreter)
  }

  private[this] val serverSpec =
    ServerBuilder()
      .name("finagle")
      .codec(Memcached())
      .bindTo(address)

  private[this] var server: Option[BuiltServer] = None

  def start(): BuiltServer = {
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