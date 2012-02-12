#version 120
//<include includes/StdLib.frag>

in vec2 texcoord;

uniform sampler2D diffuse_sampler;

void main() {
    FragColor = texture(diffuse_sampler, texcoord);
}

