#define COMPILED
#define GAMMA
#pragma include includes/StdLib.frag
#pragma include includes/gamma.h

in vec3 normal, tangent, bitangent;
in vec2 texcoord;
in vec4 stc;

uniform vec3 halfvector;
uniform vec3 light_pos;

uniform float time_delta;

uniform sampler2D normal_sampler;
uniform sampler2D float_sampler;
uniform sampler2D shadow_sampler;

uconst float texscale = 300.;

uconst vec3 water_color = vec3(20,57,143) * NORMALIZE_8BIT;
uconst vec3 water_color2 = vec3(24,139,82) * NORMALIZE_8BIT;
uconst vec3 specular = vec3(250,250,190) * NORMALIZE_8BIT;

uconst float flow_dist = 0.1;

const float flow_speed = 7000.;
const float flow_speed_inv = 1./flow_speed;

//inverse shadowmap size
uconst float tw = 1./1024.;
uconst float SHADOW_DARKENING = 0.8;
uconst float SPEC_POWER = 60.;
uconst float WATER_HIGHLIGHT = 4.;

void main() {

    vec2 tc = texcoord * texscale;

    vec2 flowtc = texture(float_sampler, tc).st * 2. - 1.;

    float phase0 = mod(time_delta, flow_speed) * flow_speed_inv;
    float phase1 = phase0 + 0.5;
    if(phase1 > 1.) phase1 -= 1.;

    // Sample normal map.
    vec3 normalT0 = texture(normal_sampler, tc + flowtc * phase0 * flow_dist).xyz;
    vec3 normalT1 = texture(normal_sampler, tc + flowtc * phase1 * flow_dist).xyz;

    float flowLerp = abs( 0.5 - phase0 ) * 2.;
    vec3 tn = mix(normalT0, normalT1, flowLerp) * 2. - 1.;

    vec3 N = normalize(normal);
    vec3 T = normalize(tangent);
    vec3 B = normalize(bitangent);
    N = normalize(T * tn.x  + B * tn.y + N * tn.z);
    float NdotE = N.z;//dot(N, vec3(0.,0.,1.));
    N.y = abs(N.y);
    vec3 L = light_pos;
    //L.y = abs(L.y);

    float NdotL = dot(N, L);
    vec3 color = vec3(0.);

    vec4 shadow;
    shadow.x = texture(shadow_sampler, stc.st- vec2(tw)).x;
    /*shadow.y = texture(shadow_sampler, stc.st+vec2(tw)).x;
    shadow.z = texture(shadow_sampler, stc.st+vec2(-tw,tw)).x;
    shadow.w = texture(shadow_sampler, stc.st+vec2(tw,-tw)).x;
    vec4 inlight = sign(vec4(stc.z - 0.001) - shadow)*0.5+0.5;
    float per = 1. - dot(inlight, vec4(0.25));*/

        vec3 R = reflect(light_pos, N);
        float RdotN = dot(-N, R);
        color += mix(water_color2, water_color, NdotE) * NdotL;
        color += pow(NdotE, WATER_HIGHLIGHT);
        if(stc.z< shadow.x)
            color += specular * pow(max(RdotN, 0.), SPEC_POWER);
        //if(per==0.)
        else
            color *= SHADOW_DARKENING;


    // write Total Color:
    FragColor = vec4(normal, 1.);
}

