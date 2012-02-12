//<include includes/StdLib.vert>

in vec4 in_position; //<Position>
in vec2 in_texcoord; //<TexCoord>
in vec3 in_normal;   //<Normal>

out vec2 texcoord;
out vec3 normal;
out float depth;

uniform mat4 mat_mvp;    //<MAT_MVP>
uniform mat3 mat_nv;            //<MAT_NV>

uniform float near = -1000;
uniform float far = 1000;

void main() {
    texcoord = in_texcoord;
    normal = mat_nv * in_normal;
    gl_Position = mat_mvp * in_position;
    depth = (-gl_Position.z-near)/(far-near);
}
