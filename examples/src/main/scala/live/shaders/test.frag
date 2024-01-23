#version 330 core

in vec2 vuv;

out vec4 fragColor;

void main() {

  vec2 xy = vuv*2.0 - vec2(1,1);
  float d = sqrt(xy.x * xy.x + xy.y * xy.y);
  float v = smoothstep(0.4, 0.5, d);
  fragColor = vec4(v, v, v, 1.0);
}