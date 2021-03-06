
package com.fishuyo.seer
package actor

import akka.actor._

import com.beachape.filemanagement.MonitorActor
import com.beachape.filemanagement.RegistryTypes._
import com.beachape.filemanagement.Messages._

import java.nio.file._
import java.nio.file.StandardWatchEventKinds._
import com.sun.nio.file._

object Monitor {

  val monitorActor = System().actorOf(MonitorActor(concurrency = 2))

  def apply(path:String, rec:Boolean=false)(f:Callback){
    //This will receive callbacks for just the one file
    monitorActor ! RegisterCallback(
      event = ENTRY_MODIFY,
      modifier = Some(SensitivityWatchEventModifier.HIGH), //None,
      path = Paths.get(path),
      callback = f,
      recursive = rec
    )
  }

  def stop(path:String, rec:Boolean=false){
    monitorActor ! UnRegisterCallback(
      ENTRY_MODIFY,
      recursive = rec,
      path = Paths.get(path)
      )
  }

  def kill(){
    monitorActor ! Kill
  }
}