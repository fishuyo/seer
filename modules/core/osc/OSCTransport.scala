package seer
package osc

import scala.util.{Try, Failure, Success}
import java.nio.channels.DatagramChannel
import java.net.StandardProtocolFamily
import java.net.StandardSocketOptions
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.net.SocketAddress


class Udp(val bufferSize:Int = 65507) {

  val buffer:ByteBuffer = ByteBuffer.allocateDirect(bufferSize)
  var channel:DatagramChannel = _

  def listen(ip:String, port:Int, recvBufSize:Int = 4096, sendBufSize:Int = 4096) = {

    Try {
      
      channel = DatagramChannel.open(StandardProtocolFamily.INET)
      if (channel.isOpen()) {
        channel.setOption(StandardSocketOptions.SO_RCVBUF, recvBufSize)
        channel.setOption(StandardSocketOptions.SO_SNDBUF, sendBufSize)

        channel.bind(new InetSocketAddress(ip, port))
        println("[Udp] server is bound to:" + channel.getLocalAddress())
      } else {
        println("[Udp] error, channel not opened.")
      }

    } match {
      case Failure(ex) => println(ex.getMessage())
      case Success(_) => 
    }


  }


  def receive() = {
    // while (true) {
    val clientAddress: SocketAddress = channel.receive(buffer)
    // buffer.flip()
    println(s"[Udp] received ${buffer.position()} bytes from ${clientAddress.toString()}")
    // channel.send(buffer, clientAddress)
    buffer.clear()
    // }
  }

  def close() = {
    if(channel.isOpen()) channel.close()
  }
}