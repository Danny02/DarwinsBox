#version 120
#pragma include includes/StdLib.vert

in vec4 in_position;    //<Position>
in vec2 in_texcoord;    //<TexCoord>
out vec2 texcoord;

uniform mat4 mat_mvp;    //<MAT_MVP>

const float texscale = 90.;

void main() {
    gl_Position = mat_mvp * in_position;
    texcoord = in_texcoord * texscale;
}
