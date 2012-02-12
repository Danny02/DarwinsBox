#version 120
#include StdLib.vert

in vec4 in_position;
in vec2 in_texcoord;

uniform mat4 mat_mv, mat_p;

out vec2 texcoord;

void main() { 
    // Vertex transformation 
    gl_Position = mat_p * mat_mv * in_position;
    texcoord = in_texcoord;
}
