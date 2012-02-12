#version 120
#include StdLib.frag

in vec2 texcoord;

uniform sampler2D diffuse_sampler;

void main() {
    FragColor = tex(diffuse_sampler, texcoord, 0.);
}

