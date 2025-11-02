#version 330 core

uniform sampler2D particlesPosDir;

in vec2 vuv;
out vec4 fragColor;

void main() {
  float dt = 0.001;

  vec4 p = texture(particlesPosDir, vuv);
  vec2 pos = p.rg;
  vec2 dir = p.ba;

  pos += dir * dt;

  fragColor = vec4(pos, dir);
}