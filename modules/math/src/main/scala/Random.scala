
package seer.math

import seer.Generator


object Random {
	val r = new java.util.Random
	val rseed = new java.util.Random
	
	def seed() = r.setSeed(rseed.nextLong)
	def seed(s:Long) = r.setSeed(s)

	val int = new Generator[Int]{
		def apply() = r.nextInt
	}
	val long = new Generator[Long]{
		def apply() = r.nextLong
	}
	def int(hi:Int): Generator[Int] = for(x <- int) yield math.abs(x) % hi
	def int(lo:Int,hi:Int): Generator[Int] = for(x <- int) yield lo + math.abs(x) % (hi - lo)

	val float = new Generator[Float]{
		def apply() = r.nextFloat
	}
	def float(lo:Float,hi:Float): Generator[Float] = for(x <- float) yield x * (hi-lo) + lo

	val double = new Generator[Double]{
		def apply() = r.nextFloat
	}

	val bool = new Generator[Boolean]{
		def apply() = r.nextBoolean
	}

	val vec3 = new Generator[Vec3]{
		def apply() = Vec3(float(-1f,1f)(),float(-1f,1f)(),float(-1f,1f)())
	}
	def vec3(lo:Vec3, hi:Vec3): Generator[Vec3] = {
		for(x <- float(lo.x,hi.x);
				y <- float(lo.y,hi.y);
				z <- float(lo.z,hi.z)) yield Vec3(x,y,z)
	}

	val vec2 = new Generator[Vec2]{
		def apply() = Vec2(float(-1f,1f)(),float(-1f,1f)())
	}
	def vec2(lo:Vec2, hi:Vec2): Generator[Vec2] = {
		for(x <- float(lo.x,hi.x);
				y <- float(lo.y,hi.y)) yield Vec2(x,y)
	}

	def quat = new Generator[Quat]{
		def apply() = Quat(float(-1f,1f)(),float(-1f,1f)(),float(-1f,1f)(),float(-1,1)()).normalize
	}

	// def rgb = new Generator[RGB]{
	// 	def apply() = RGB(float(),float(),float())
	// }

	def oneOf[T](xs: T*) = for(i <- int(0,xs.length)) yield xs(i)

	def decide[T](xs:Seq[T], weights:Seq[Float]) = new Generator[T]{
		def apply():T = {
			val r = float()
			val sum = weights.sum
			var accum = 0f
			for( i <- ( 0 until weights.length)){
				accum += weights(i) / sum
				if( r < accum) return xs(i)
			}
			xs(0)
		}
	}

}

