package seer
package osc

object OSCTest extends App {


  val udp = new Udp()
  udp.listen("localhost", 8008)

  while(true){

    udp.receive()
    Thread.sleep(10)
  }



}