
package seer.graphics


// import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._

object Screen {

    def size(): (Int,Int) = {
        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return (vidmode.width(), vidmode.height())

    }

}
