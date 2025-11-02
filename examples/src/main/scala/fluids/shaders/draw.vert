#version 330 core
layout(location = 0) in vec3 position;
// layout(location = 2) in vec2 uv;

uniform sampler2D particlesPosDir;

// out vec2 vuv;
void main(){

  // vuv = uv;
  vec2 uv = position.xy;
  vec4 p = texture(particlesPosDir, uv);

  gl_Position = vec4(p.xy, 0.0, 1.0); 
}