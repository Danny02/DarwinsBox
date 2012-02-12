#version 120
#include StdLib.frag

in vec3 texcoord;

uniform samplerCube diffuse_sampler;

void main() {
    Fragcolor = textureCube(diffuse_sampler, texcoord);
}

