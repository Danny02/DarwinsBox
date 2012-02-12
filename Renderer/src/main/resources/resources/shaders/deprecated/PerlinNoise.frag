#version 130
#include StdLib.frag
#include NoiseLib.h

const int MAX_ITERATIONS = 15;
const float BAIL_OUT = 2.0;
const float T = 0.2;

in vec2 texcoord;
in vec3 normal;

uniform sampler2D diffuse_sampler;
uniform float texstep;

//vec4 isJulia(vec3 pos);
vec4 isMandelbrot(vec3 pos);

void main() {
    FragColor = isMandelbrot(vec3((texcoord-0.5)*2.5, 0.)).aaaa;
}

vec4 mulQuaternions(vec4 a, vec4 b){
    vec4 c;

    c.x = a.x * b.x - a.y * b.y - a.z * b.z - a.w * b.w;
    c.y = a.x * b.y + a.y * b.x + a.z * b.w - a.w * b.z;
    c.z = a.x * b.z + a.z * b.x + a.w * b.y - a.y * b.w;
    c.w = a.x * b.w + a.w * b.x + a.y * b.z - a.z * b.y;

    return c;
}

vec4 isJM_Set(vec4 z, vec4 c){
    for(int i=0;i<MAX_ITERATIONS; ++i){
        z = mulQuaternions(z, z) + c;
        if(length(z)>=BAIL_OUT)
            return vec4(0.);
    }

    return vec4(z.xyz, 1.0);
}

/*vec4 isJulia(vec3 pos){
    vec4 z = vec4(pos, T);

    return isJM_Set(z, juliaC);
}*/

vec4 isMandelbrot(vec3 pos){
    vec4 c = vec4(pos, T);
    vec4 z = vec4(0.0, 0.0, 0.0, 0.0);

    return isJM_Set(z, c);
}

