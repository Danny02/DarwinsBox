#version 120
//<include includes/StdLib.vert>

in vec2 in_position; //<Position>
in vec2 in_texcoord; //<TexCoord>
out vec2 texcoord;

uniform vec2 shift;
uniform vec2 scale;

void main() {
    gl_Position =  vec4(in_position* scale + shift, -1., 1.);

    texcoord = in_texcoord;
}
