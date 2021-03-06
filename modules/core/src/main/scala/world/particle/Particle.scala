
package com.fishuyo.seer
package world
package particle

import spatial._


trait KinematicState {
	var _mass = 1f
	var invMass = 1f

	var position = Vec3()
	var lPosition = Vec3()
	var velocity = Vec3()
	var acceleration = Vec3()
	// var jerk = Vec3()

	def mass = _mass
	def mass_=(f:Float){ _mass = f; invMass = 1f / f }

	def applyForce( f: Vec3 ) = acceleration = acceleration + (f * invMass)
  def applyGravity() = acceleration = acceleration + Gravity
  def applyDamping( damp: Float ) = acceleration = acceleration - velocity * (damp * invMass)

  // def collide(p:Vec3,n:Vec3,r:Float){
    
  // }
  def collideGround(y:Float, r:Float){
  	if( position.y < y){
  		position.y = y
  		velocity.y *= -1
  		lPosition = position - velocity * r
  	}
  }

}

trait RotationalState {

	var _inertia = 1f 
	var invInertia = 1f 

	def inertia = _inertia
	def inertia_=(f:Float){ _inertia = f; invInertia = 1f / f }

  // var euler = Vec3(0)
  // var lEuler = Vec3(0)
  // var angularAcceleration = Vec3(0)

  var orientation = Quat()
  var lOrientation = Quat()

  var angularMomentum = Vec3(0)
  var angularVelocity = Vec3(0)
  var spin = Quat() // angular velocity as quaternion

  var torque = Vec3(0)
  var dSpin = Quat()

  def applyTorque( f: Vec3 ) = { torque = torque + f; dSpin = Quat().fromEuler(f*invInertia) * dSpin }
  def applyTorque( q: Quat ) = { dSpin = q * dSpin }
  def applyAngularDamping( damp: Float ) = { torque = torque - angularVelocity * (damp); dSpin = spin.inverse.slerp(Quat(),1f-damp) * dSpin }	

}



object Integrators {

	var dt = 1/30f
	var dtdt_2 = dt*dt*.5f

	def setTimeStep(timeStep:Float){
		dt = timeStep
		dtdt_2 = dt*dt*.5f
	}

	// Kinematic State integrators
	def verlet(k:KinematicState){
    k.velocity = k.position - k.lPosition
    k.lPosition = k.position
    k.position = k.position + k.velocity + k.acceleration * ( dtdt_2 )
    k.acceleration = Vec3(0)
	}
	def verletR(k:KinematicState){
    k.velocity = k.lPosition - k.position
    k.position = k.lPosition
    k.lPosition = k.position + k.velocity + k.acceleration * ( dtdt_2 )
    k.acceleration = Vec3(0)
	}

	def euler(k:KinematicState){
		k.velocity = k.velocity + k.acceleration*dt
		k.lPosition = k.position
		k.position = k.position + k.velocity*dt
		k.acceleration = Vec3(0)
	}

	// rotationalState itnegrators
	def rotationalVerlet(k:RotationalState){
		k.spin = k.orientation * k.lOrientation.inverse
		k.lOrientation = k.orientation
		k.orientation = ( (k.dSpin) * k.spin * k.orientation).normalize
		k.dSpin = Quat()

   //  k.angularVelocity =  k.euler - k.lEuler  	
  	// k.lEuler = k.euler
  	// k.euler = k.euler + k.angularVelocity + k.angularAcceleration * (.5f*dt*dt)
  	// k.angularAcceleration = Vec3(0)
	}

	def rotationalEuler(k:RotationalState){
		// k.angularVelocity = k.angularVelocity + k.angularAcceleration*dt
		// k.lEuler = k.euler
		// k.euler = k.euler + k.angularVelocity*dt
  // 	k.angularAcceleration = Vec3(0)
	}
}

object Particle {
	def apply(pos:Vec3) = new Particle{ initialPosition = pos; position = pos; lPosition = pos }
	def apply(pos:Vec3, vel:Vec3) = new Particle{ initialPosition = pos; position = pos; velocity = vel; lPosition = pos - vel }
}

class Particle extends KinematicState {
	var initialPosition = Vec3()
	var t = 0f

	def step(){
		t += Integrators.dt
		Integrators.verlet(this)
	}

  def setVelocity(v:Vec3){
    velocity = v 
    lPosition = position - v
  }
	
	def reset(){ position = initialPosition; lPosition = initialPosition }
}

object Stick {
  def apply(pose:Pose):Stick = { 
    this(pose.pos,pose.quat)
  }	
  def apply(pos:Vec3, q:Quat):Stick = { 
		val p = new Stick
		p.position = pos
		p.lPosition = pos
		p.orientation = q
		p.lOrientation = q
		p 
	}
	def apply(pos:Vec3, vel:Vec3, quat:Quat, angVel:Vec3):Stick = { 
		val p = new Stick
		p.position = pos; 
		p.velocity = vel; p.lPosition = pos - vel;
		p.orientation = quat
		p.angularVelocity = angVel
		p.lOrientation = Quat().fromEuler(angVel).inverse * p.orientation
		p
	}
}

class Stick extends KinematicState with RotationalState {
	var t = 0f
	var length = 1f
	val end = new Particle

	def step(){
		t += Integrators.dt
		Integrators.verlet(this)
		Integrators.rotationalVerlet(this)
		end.lPosition = end.position
		end.position = position + -orientation.toZ()*length
		// end.position = position + Quat().fromEuler(euler).toZ()*length
	}
}


object Gravity extends Vec3(0,-10,0)
