package seer 
package examples

import graphics._
import graphics.lwjgl._

import runtime._
import math._


object Bubbles1 extends SeerApp {

	val graphics = new LwjglGraphicsRuntimeModule()

	useModules(graphics :: List())

	var timer = 0.0
  var shader:ShaderProgram = _
  var mesh:Mesh = _


  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 330 core
      uniform vec2 mouse;
      uniform float time;
      in vec2 vuv;
      out vec4 fragColor;

      //Noice function [0,1]
      vec2 T = vec2(0.);

      float No(float x){
        return fract(9667.5*sin(7983.75*(x + T.x) + 297. + T.y));
      }

      vec4 Rancol(vec2 x){
        return vec4(No(x.x + x.y), No(x.x*x.x+ x.y), No(x.x*x.x + x.y*x.y),1.);
      }

      //bubbles!!
      vec4 grid(vec2 uv, float t){
        vec4 C1,C2;
        uv *= 10.;
        vec2 id = vec2(int(uv.x),int(uv.y));
        uv.y += (5.*No(id.x*id.x) + 1.)*t*.4	;
        // uv.y += (5.*No(id.x*id.x*id.y) + 1.)*t*.4	;
        uv.x += No(id.x);
        // uv.y += No(id.x + id.y);
        id = vec2(int(uv.x), int(uv.y));
        uv = fract(uv) - .5;

        // return vec4(uv, 0., 1.);

        // if (id == vec2(1,10)){C1 = vec4(1.);}

        float d = length(uv);
        t *= 10.*No(id.x + id.y);
        //uv.x += No(id.x);
        //if (uv.x > .46 || uv.y > .46){C1 = vec4(1.);}

        float r = .1*sin(t + sin(t)*.5)+.3;
        float r1 = .07 * sin(2.*t + sin(2.*t)*.5) + .1 * No(id.x + id.y);

        return vec4(d, r1, r, 1.);

        if(d < r && d > r-.1){
          C2 = .5*Rancol(id + vec2(1.)) + vec4(.5);
          C2 *= smoothstep(r-.12,r,d);
          C2 *= 1. - smoothstep(r-.05, r+.12,d);
        }

        if (d < r1){
          C2 = .5*Rancol(id + vec2(1.)) + vec4(.5);
        }

        return C2 + C1;
      }

      void main() {
        vec2 uv = vuv; // / vec2(640.,480.);
        uv.y *= 480./640.;
        float t = time;
        T = mouse.xy;

        fragColor = vec4(grid(uv, t));
        // fragColor = vec4(1.,0.,0.,1.);
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
    mesh = new Mesh()
    mesh.resize(4, hasTexcoords=true)
    mesh.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    mesh.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    mesh.resizeIndices(6)
    mesh.indices.put(Array(0,1,2,2,3,0))
  }

	graphics.onUpdate = (dt:Double) => {
		timer += dt
		// if(timer > 0.5) timer = 0.0

    mesh.update()    
	}

	graphics.onDraw = (g:Graphics) => {
		import g.gl._

    shader.bind()
    shader.uniform("time", timer)
    shader.uniform("mouse", Vec2(0.5f,0.5f))

    mesh.draw()
	}

}
