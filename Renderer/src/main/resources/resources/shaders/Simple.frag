#version 120
#pragma include includes/StdLib.frag

in vec2 texcoord;

uniform sampler2D diffuse_sampler;

void main() {
    FragColor = texture(diffuse_sampler, texcoord);
}

