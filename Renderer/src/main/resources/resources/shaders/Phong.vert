#version 130
#pragma include includes/StdLib.vert
#pragma include includes/misc.h
//<defines OBJECT_PLACMENT,NORMAL_MAPPING,AMBIENT_GRADIENT,[CEM,BPCEM]>

in vec4 in_position; //<Position>
in vec2 in_texcoord; //<TexCoord>
in vec3 in_normal;   //<Normal>

out vec3 ambient, diffuse, specular;
out vec2 texcoord;
out vec3 normal;
//TODO water transluency
out float height;

#ifdef NORMAL_MAPPING
    in vec3 in_tangent; //<Tangent>
    out vec3 tangent, bitangent;
#endif

#ifdef BPCEM
    #define CEM
    out vec3 pos;
#endif
#ifdef CEM
    out vec3 reflection;
#endif

uniform mat4 mat_m, mat_v, mat_p;    //<MAT_M, MAT_V, MAT_P>
//TEST
uniform mat4 mat_vp;    //<MAT_VP>
uniform mat3 mat_nv;            //<MAT_NV>

uniform vec3 light_diffuse =  vec3(255,241,162) * NORMALIZE_8BIT;
uniform vec3 light_ambient = vec3(1., 1., 1.);
uniform vec3 light_specular = vec3(1., 1., 1.);

uniform vec3 mat_diffuse;
uniform vec3 mat_ambient;
uniform vec3 mat_specular;

#ifdef OBJECT_PLACMENT
    uniform vec2 height_tc;
    uniform sampler2D height_map;
    uniform float displacement = 0.7;
#endif

void main() {
    texcoord = in_texcoord;

    ambient = mat_ambient * light_ambient + mat_ambient * 0.1;
    diffuse = mat_diffuse * light_diffuse;
    specular = mat_specular * light_specular;

    vec4 position = in_position;
    #ifdef OBJECT_PLACMENT
        float height = texture(height_map, height_tc).x;
        position.z += displacement * height;
    #endif

    normal = mat_nv * in_normal;

    gl_Position = mat_m * position;
    height = gl_Position.y * 10;

//TEST
    vec4 pos = gl_Position;
    gl_Position = mat_v * gl_Position;
    #ifdef OBJECT_PLACMENT
        gl_Position.y += 12.;
    #endif

    #ifdef CEM
        vec3 dir = gl_Position.xyz;
        reflection = - reflect(dir, normal);
    #endif
    #ifdef BPCEM
        pos = gl_Position.xyz;
    #endif

    #ifdef NORMAL_MAPPING
        tangent = mat_nv * in_tangent;
        bitangent = cross(normal, tangent);
    #endif
//TEST
    gl_Position = mat_vp * pos;
    //gl_Position = mat_p * gl_Position;
}
