#version 120
#include StdLib.frag

const int MAX_ITERATIONS = 15;
const float BAIL_OUT = 2.0;

const float T = 0.2;
const float STEP_SIZE = 0.01;
const float pi = 3.14159265;

const float zoom = 0.3;
const vec2 shift = vec2(0.5, 0.3);

const float absdepth = 1.6;
const float reldepth = absdepth / STEP_SIZE;

vec4 frak_color = vec4(0.0, 0.0, 0.0, 0.0);

uniform vec4 juliaC;
uniform float scale;
uniform float roty;

in vec3 normal;

vec3 convert(vec3 a){
    a = a * 2.0 - 1.0;
    a *= scale;
    return a;
}

vec3 rotate(vec3 vec){
    float alpha = roty/ 180. * pi ;
    mat3 rotmat = mat3(cos(alpha), 0., sin(alpha), 0., 1., 0., -sin(alpha), 0., cos(alpha));
    return vec*rotmat;
}

bool isJulia(vec3 pos);
bool isMandelbrot(vec3 pos);

void main() {
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

    vec3 origin = convert(vec3(normal.xy*zoom + shift, -0.3));
    origin = rotate(origin);
    vec3 direction = rotate(normal);

    normalize(direction);
    vec3 ray = vec3(0.0, 0.0, 0.0);

    for(int i=0;i<int(reldepth); ++i){

        if(isJulia(ray+origin)){
            float a = length(ray) / absdepth;
            a = 1.0 -a;
            color = vec4(a, a, a, 1.0);
            break;
        }

        ray += direction * STEP_SIZE;
    }

    FragColor = color;
}

vec4 mulQuaternions(vec4 a, vec4 b){
    vec4 c;

    c.x = a.x * b.x - a.y * b.y - a.z * b.z - a.w * b.w;
    c.y = a.x * b.y + a.y * b.x + a.z * b.w - a.w * b.z;
    c.z = a.x * b.z + a.z * b.x + a.w * b.y - a.y * b.w;
    c.w = a.x * b.w + a.w * b.x + a.y * b.z - a.z * b.y;

    return c;
}

bool isJM_Set(vec4 z, vec4 c){
    for(int i=0;i<MAX_ITERATIONS; ++i){
        z = mulQuaternions(z, z) + c;
        if(length(z)>=BAIL_OUT)
            return false;
    }

    frak_color = vec4(z.xyz, 1.0);

    return true;
}

bool isJulia(vec3 pos){
    vec4 z = vec4(pos, T);

    return isJM_Set(z, juliaC);
}

bool isMandelbrot(vec3 pos){
    vec4 c = vec4(pos, T);
    vec4 z = vec4(0.0, 0.0, 0.0, 0.0);

    return isJM_Set(z, c);
}