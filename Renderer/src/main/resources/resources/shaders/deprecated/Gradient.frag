#version 120
#include StdLib.frag

uniform sampler2D gradient;

in vec4 ambient, diffuse, specular;
in vec3 normal, lightdir, eyevector;//, tangent, bitangent;
in vec2 texcoord;
in float lightdist, specexp;

void main() {
    vec4 color;
    vec3 N, L, E;
    float NdotL;

    N = normalize(normal);
    L = normalize(lightdir);
    E = normalize(eyevector);
    NdotL = dot(N, L);
    NdotL = 0.5 * NdotL + 0.50;

        vec3 R = reflect(-L, N);
        float RdotE = max(dot(R, E), 0.0);
        color = diffuse * (tex(gradient,vec2(NdotL, 0.))*2. + ambient);
        float spec = pow(RdotE, specexp) * 0.3;
        float rim =  pow(RdotE * pow((1. - dot(N, E)),1.), 2.) * 2.;
        //vec4 drim = dot(N, up) * pow((1. - dot(N, E)),4.) * ambient;
        color += specular * max(spec, rim);
        //color += drim;
    
    FragColor = color;
}