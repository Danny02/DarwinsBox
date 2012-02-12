#version 120
//<include includes/StdLib.vert>

in vec4 in_position;    //<Position>
in vec2 in_texcoord;    //<TexCoord>
out vec2 texcoord;

uniform mat4 mat_mvp;    //<MAT_MVP>

const float texscale = 90.;

void main() {
    // Vertex transformation
    gl_Position = mat_mvp * in_position;
    texcoord = in_texcoord*texscale;
    //gl_Position = vec4(in_position.xy, -1., 1. );
    //texcoord = (gl_Position.xy + 1.) * 0.5;
}
