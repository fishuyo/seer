#version 330 core

in vec2 vuv;

out vec4 fragColor;

uniform float time;

void main() {
  float t = sin(time);

  vec2 xy = vuv*2.0 - vec2(1,1);
  float d = sqrt(xy.x * xy.x + xy.y * xy.y);
  float v = smoothstep(1.0, t, d);
  fragColor = vec4(v - t, v, v, 1.0);

}