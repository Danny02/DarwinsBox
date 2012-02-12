#version 120
#include StdLib.vert

in vec4 in_position;
in vec2 in_texcoord;
in vec3 in_normal;
out vec4 ambient, diffuse;
out vec3 normal;//, tangent, bitangent;
out vec2 texcoord;
out float specexp, specular;

uniform mat4 mat_m, mat_v, mat_p;
uniform mat3 mat_n;

uniform vec4 mat_diffuse;
uniform vec4 mat_ambient;
uniform vec4 mat_specular;

uniform float mat_spec_exp;


void main() {
    texcoord = in_texcoord;
    normal = mat3(mat_v) * mat_n * in_normal;

    ambient = mat_ambient;
    diffuse = mat_diffuse;
    specular = mat_specular.r;
    specexp = mat_spec_exp;

    gl_Position = mat_p * mat_v * mat_m * in_position;

}
