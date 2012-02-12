#version 130
#define MRT
#include StdLib.frag

in vec4 ambient, diffuse;
in vec3 normal;//, tangent, bitangent;texcoor
in vec2 texcoord;
in float specexp, specular;

uniform vec4 smapler_settings;
uniform sampler2D diffuse_sampler, specular_sampler, normal_sampler, alpha_sampler;

void main() {
    if(smapler_settings.w != 0.0){
        if(tex(alpha_sampler, texcoord).r < 0.5)
            discard;
    }

    vec4 color, dcolor;
    color = vec4(0,0,0,0);
    dcolor = diffuse;
    if(smapler_settings.x > 0.5){
        dcolor *= tex(diffuse_sampler, texcoord, 0.);
    }

    vec3 N, L;
    float NdotL;

    N = normalize(normal);

    float scolor;
    scolor = specular;
    if(smapler_settings.y != 0.0){
        scolor *= tex(specular_sampler, texcoord, 0.).r;
    }

    //vec3 tn = normalize(texture2D(normal_sampler, texcoor, 0.).xyz);
    //if(settings.z != 0.0)
    // N = normalize(tangent*tn.x+bitangent*tn.z+normal*tn.y);

    // write Total Color:
    dcolor.a = scolor;
    N = (N+1)*0.5;
    vec4 norspecex = vec4(N, specexp);
    FragData[0] = dcolor;
    FragData[1] = norspecex;
    FragData[2] = ambient*dcolor;
}

