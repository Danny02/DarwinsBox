#version 130
#pragma include includes/StdLib.frag
#define GAMMA
#pragma include includes/gamma.h

//<defines DIFFUSE_MAPPING,SPECULAR_MAPPING,ALPHA_TEST,NORMAL_MAPPING,AMBIENT_GRADIENT,[CEM,BPCEM]>

in vec3 ambient, diffuse, specular;
in vec3 normal;
in vec2 texcoord;
in float height;

uniform vec3 halfvector;
uniform vec3 light_pos;

uniform float mat_spec_exp;

#ifdef DIFFUSE_MAPPING
    uniform sampler2D diffuse_sampler;
#endif

#ifdef SPECULAR_MAPPING
    uniform sampler2D specular_sampler;
#endif

#ifdef ALPHA_TEST 
    uniform sampler2D alpha_sampler;
#endif

#ifdef NORMAL_MAPPING
    in vec3 tangent, bitangent;
    uniform sampler2D normal_sampler;
#endif

//TODO
#ifdef BPCEM
    #ifndef CEM
        #define CEM
    #endif
    in vec3 pos;
    uniform vec3 EnvBoxMax = vec3(100.);
    uniform vec3 EnvBoxMin = vec3(-100.);
    uniform vec3 EnvBoxPos = vec3(0.);

    uniform mat4 mat_v;//<MAT_V>
#endif

#ifdef CEM
    in vec3 reflection;
    uniform samplerCube env_sampler;
#endif

void main() {

    #ifdef ALPHA_TEST
        if(texture(alpha_sampler, texcoord).a < 0.5)
            discard;
    #endif

    vec3 N = normalize(normal);

    vec3 color, dcolor;
    color = vec3(0.);
    dcolor = diffuse;
    #ifdef DIFFUSE_MAPPING
        dcolor *= toLinear(texture(diffuse_sampler, texcoord).rgb);
    #endif

    #ifdef NORMAL_MAPPING
        vec3 T = normalize(tangent);
        vec3 B = normalize(bitangent);
        vec3 tn = normalize(texture(normal_sampler, texcoord).rgb*0.5-0.5);
        N = normalize(T * tn.x  + B * tn.y + N * tn.z);
        //float xy = length(N.xy);
    #endif

    float NdotL = dot(N, light_pos);

    if(NdotL > 0.0){
        float HdotN = dot(N, halfvector);
        vec3 scolor = specular;
        #ifdef SPECULAR_MAPPING
            scolor *= texture(specular_sampler, texcoord).rgb;
        #endif
        color += scolor * pow(max(HdotN, 0.), mat_spec_exp);
        color += dcolor * NdotL;
    }
    color += ambient * dcolor;

    #ifdef CEM
        vec3 rdir;
        #ifdef BPCEM
            vec3 nrdir = normalize(reflection);
            mat3 mat_vn = mat3(mat_v);
            vec3 rbmax = (mat_vn*EnvBoxMax - pos)/nrdir;
            vec3 rbmin = (mat_vn*EnvBoxMin - pos)/nrdir;

            vec3 rbminmax;
            if(nrdir.x > 0. && nrdir.y >0. && nrdir.z >0.)
                rbminmax = rbmax;
            else
                rbminmax = rbmin;

            float fa = min(min(rbminmax.x, rbminmax.y), rbminmax.z);

            vec3 posonbox = pos + nrdir*fa;
            rdir = posonbox - EnvBoxPos;
        #else
            rdir = reflection;
        #endif

        color = mix(textureCube(env_sampler, rdir).rgb, color, 0.5);
    #endif
    // write Total Color:
    FragColor =  vec4(toGamma(color), clamp(height, 0., 1.));
}

