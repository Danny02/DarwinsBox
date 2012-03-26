#pragma include includes/StdLib.vert

in vec4 in_position;    //<Position>
in vec2 in_texcoord;    //<TexCoord>

out vec3 normal, tangent, bitangent;
out vec2 texcoord;
out vec4 stc;

uniform mat4 mat_m, mat_vp;     //<MAT_M, MAT_VP>
uniform mat4 mat_s;             //<MAT_S>
uniform mat3 mat_nv;            //<MAT_NV>

void main() {
    texcoord = in_texcoord;

    normal = mat_nv * vec3(0.,1.,0.);//TODO mat_nv[1].xyz
    tangent = mat_nv * vec3(1.,0.,0.);
    bitangent = cross(normal, tangent);

    gl_Position = mat_m * in_position;
    stc = mat_s * gl_Position;
    stc.xyz = (stc.xyz / stc.w)*0.5+0.5;
    gl_Position = mat_vp * gl_Position;
}
