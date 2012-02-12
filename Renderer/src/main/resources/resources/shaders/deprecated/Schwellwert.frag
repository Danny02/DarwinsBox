#version 120
#include StdLib.frag

uniform sampler2D screen;
uniform float texstep;

void main() {
    float c = tex(screen, gl_TexCoord[0].st).r;
    if(c>0.1)
        c=1.;
    else
        c=0.;
    FragColor = vec4(c, c, c, 1.);
}

