package com.ivan

import com.twitter.finagle.memcached.protocol._
import org.jboss.netty.buffer.ChannelBuffer
import com.twitter.util.Future
import com.twitter.finagle.Service
import scala.collection.mutable

case class Entry(min: Long, avg: Long, max: Long, count: Long) {
  // TODO
}

class Storage[A, B] extends mutable.HashMap[A, B] {
  def lock[C](key: ChannelBuffer)(f: mutable.Map[A, B] => C) = {
    this.synchronized {
      f(this)
    }
  }
}

class InterpreterService(interpreter: Interpreter) extends Service[Command, Response] {
  def apply(command: Command) = Future(interpreter(command))
}

class Interpreter(storage: Storage[ChannelBuffer, Entry]) {
  def aggregate(key: ChannelBuffer, value: ChannelBuffer): (mutable.Map[ChannelBuffer, Entry] => Response) = {
    data => NotStored() // TODO
  }

  def apply(command: Command): Response = {
    command match {
      case Set(key, flags, expiry, value) => storage.lock(key)(aggregate(key, value))
      case Add(key, flags, expiry, value) => storage.lock(key)(aggregate(key, value))
      case Replace(key, flags, expiry, value) => storage.lock(key)(aggregate(key, value))
      case Append(key, flags, expiry, value) => storage.lock(key)(aggregate(key, value))
      case Prepend(key, flags, expiry, value) => storage.lock(key)(aggregate(key, value))
      case Get(keys) => NotFound() // TODO
      case Quit() => NoOp()
      case _ => Error(new Exception("Not supported"))
    }
  }
}




