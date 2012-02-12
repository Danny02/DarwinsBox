#version 120

in vec2 texCoord0;
in vec4 pos;

uniform mat4 mat_mvp;

out vec2 texcoord;

void main() { 
    // Vertex transformation 
    gl_Position = mat_mvp * pos;
    texcoord = texCoord0;
}
