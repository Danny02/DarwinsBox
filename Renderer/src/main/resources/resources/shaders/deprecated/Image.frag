#version 120
#include StdLib.frag

in vec2 texcoord;

uniform float phase;
uniform float size;
uniform float offset;

uniform sampler2D diffuse_sampler;

void main(){
    vec4 col = tex(diffuse_sampler, texcoord);
    float p = (texcoord.y+offset) * size;
    col.r *= sin( p + phase * 1.1 ) * 0.1 + 1.0;
    col.g *= sin( p + phase * 1.2 ) * 0.1 + 1.0;
    col.b *= sin( p + phase * 1.3 ) * 0.1 + 1.0;
    col.a *= sin( p + phase ) * 0.25 + 1.0;
    FragColor = clamp( col, 0., 1.);
}
