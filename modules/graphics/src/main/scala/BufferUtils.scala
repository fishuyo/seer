package seer.graphics 

import java.nio._

object BufferUtils {
  def newFloatBuffer(numFloats:Int): FloatBuffer = {
		val buffer = ByteBuffer.allocateDirect(numFloats * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	def newDoubleBuffer(numDoubles:Int): DoubleBuffer = {
		val buffer = ByteBuffer.allocateDirect(numDoubles * 8);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asDoubleBuffer();
	}

	def newByteBuffer(numBytes:Int): ByteBuffer = {
		val buffer = ByteBuffer.allocateDirect(numBytes);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	def newShortBuffer(numShorts:Int): ShortBuffer = {
		val buffer = ByteBuffer.allocateDirect(numShorts * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asShortBuffer();
	}

	def newCharBuffer(numChars:Int): CharBuffer = {
		val buffer = ByteBuffer.allocateDirect(numChars * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asCharBuffer();
	}

	def newIntBuffer(numInts:Int): IntBuffer = {
		val buffer = ByteBuffer.allocateDirect(numInts * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	def newLongBuffer(numLongs:Int): LongBuffer = {
		val buffer = ByteBuffer.allocateDirect(numLongs * 8);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asLongBuffer();
	}
}