package com.ivan

import com.twitter.finagle.memcached.protocol._
import org.jboss.netty.buffer.ChannelBuffer
import com.twitter.util.Future
import com.twitter.finagle.Service
import scala.collection.mutable
import com.twitter.io.Charsets
import com.twitter.finagle.memcached.util.ParserUtils._
import org.jboss.netty.buffer.ChannelBuffers._
import com.twitter.finagle.memcached.protocol.Prepend
import com.twitter.finagle.memcached.protocol.Gets
import com.twitter.finagle.memcached.protocol.InfoLines
import com.twitter.finagle.memcached.protocol.Stored
import com.twitter.finagle.memcached.protocol.Replace
import com.twitter.finagle.memcached.protocol.NoOp
import com.twitter.finagle.memcached.protocol.Stats
import com.twitter.finagle.memcached.protocol.NotStored
import com.twitter.finagle.memcached.protocol.Quit
import com.twitter.finagle.memcached.protocol.Append
import com.twitter.finagle.memcached.protocol.Get
import scala.Some
import com.twitter.finagle.memcached.protocol.Info
import com.twitter.finagle.memcached.protocol.Add
import com.twitter.finagle.memcached.protocol.Value
import com.twitter.finagle.memcached.protocol.Values
import com.twitter.finagle.memcached.protocol.Set

class Interpreter(storage: Storage[ChannelBuffer, Entry]) {
  import ChanelBufferConverters._

  def apply(command: Command): Response = {
    command match {
      case Set(key, flags, expiry, value) => storage.lock(aggregate(key, value))
      case Add(key, flags, expiry, value) => storage.lock(aggregate(key, value))
      case Replace(key, flags, expiry, value) => storage.lock(aggregate(key, value))
      case Append(key, flags, expiry, value) => storage.lock(aggregate(key, value))
      case Prepend(key, flags, expiry, value) => storage.lock(aggregate(key, value))
      case Get(keys) => values(keys)
      case Gets(keys) => values(keys)
      case Stats(args) => storage.lock { data =>
        InfoLines(List(Info("size", List(data.size))))
      }
      case Quit() => NoOp()
    }
  }

  def aggregate(key: ChannelBuffer, value: ChannelBuffer): (mutable.Map[ChannelBuffer, Entry] => Response) = {
    data => {
      val str = value.toString(Charsets.UsAscii)
      if (!str.isEmpty && !DigitsPattern.matcher(str).matches()) {
        NotStored()
      } else {
        val vv = if (str.isEmpty) 0L else str.toLong

        data.get(key) match {
          case Some(entry) => entry(vv)
          case _ => data(key) = Entry(vv)
        }

        Stored()
      }
    }
  }

  def values(keys: Seq[ChannelBuffer]): Response =
    Values(
      keys flatMap { key =>
        storage.lock { data =>
          data.get(key) map { entry => Value(key, Stat(entry)) }
        }
      }
    )
}

class Storage[A, B] extends mutable.HashMap[A, B] {
  def lock[C](f: mutable.Map[A, B] => C) = {
    this.synchronized {
      f(this)
    }
  }
}

object ChanelBufferConverters {
  implicit def any2ChannelBuffer[A](v: A) : ChannelBuffer = wrappedBuffer(v.toString.getBytes(Charsets.UsAscii))
}

object Entry {
  def apply(v: Long) : Entry = Entry(v, v, v, 1)
}

case class Entry(var min: Long, var max: Long, var sum: Long, var count: Long) {
  def apply(v: Long) = {
    this.min = Math.min(min, v)
    this.max = Math.max(max, v)
    // TODO: validate overflow before add
    this.sum += v
    this.count += 1
  }
}

object Stat {
  def apply(v: Entry) : Stat = Stat(v.min, v.sum / v.count, v.max)
}

case class Stat(min: Long, avg: Long, max: Long)

class InterpreterService(interpreter: Interpreter) extends Service[Command, Response] {
  def apply(command: Command) = Future(interpreter(command))
}




