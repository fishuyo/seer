
// package seer
// package graphics 

// import math._


// object MatrixStack {

// 	var stack = List[Mat4]()
	
//   var model = new Mat4()
// 	var projModelView = new Mat4()
//   var modelView = new Mat4()
//   var view = new Mat4()
//   var normal = new Mat4()

//   var worldPose = Pose()
//   var worldScale = Vec3(1)

// 	def push(){ stack = new Matrix4(model) :: stack }
// 	def pop(){ model = stack.head; stack = stack.tail }

//   def translate(x:Float,y:Float,z:Float){ translate(Vec3(x,y,z)) }
// 	def translate(p:Vec3){
// 		val m = new Matrix4().translate(p.x,p.y,p.z)
// 		Matrix4.mul(model.`val`, m.`val`)
// 	}

//   def rotate(x:Float, y:Float, z:Float){ rotate(Quat(x,y,z)) }
// 	def rotate(q:Quat){
//   	val quat = new Quaternion(q.x,q.y,q.z,q.w)
// 		val m = new Matrix4().rotate(quat)
// 		Matrix4.mul(model.`val`, m.`val`)	
// 	}

//   def scale(s:Float){ scale(Vec3(s)) }
//   def scale(x:Float,y:Float,z:Float){ scale(Vec3(x,y,z)) }
// 	def scale( s:Vec3 ){
// 		val m = new Matrix4().scale(s.x,s.y,s.z)
// 		Matrix4.mul(model.`val`, m.`val`)	
// 	}

// 	def transform(pose:Pose, scale:Vec3=Vec3(1)){
//   	val quat = new Quaternion(pose.quat.x,pose.quat.y,pose.quat.z,pose.quat.w)
// 		val m = new Matrix4().translate(pose.pos.x,pose.pos.y,pose.pos.z).rotate(quat).scale(scale.x,scale.y,scale.z)
// 		Matrix4.mul(model.`val`, m.`val`)
// 	}

// 	def clear() = { stack = List[Matrix4](); model.idt; transform(worldPose,worldScale) }
// 	def setIdentity() = model.idt

// 	def apply(camera:NavCamera=Camera) = {
// 		projModelView.set(camera.combined)
//   	modelView.set(camera.view)
//   	view.set(camera.view)
//   	Matrix4.mul( projModelView.`val`, model.`val`)
//   	Matrix4.mul( modelView.`val`, model.`val`)
//   	normal.set(modelView).toNormalMatrix()
// 	}

// 	def projectionModelViewMatrix() = projModelView
// 	def modelViewMatrix() = modelView
// 	def viewMatrix() = view
// 	def modelMatrix() = model
// 	def normalMatrix() = normal

// }

