#version 120
#include StdLib.frag

in vec2 texcoord;

uniform sampler2D diffuse_sampler;
uniform float texstep;

out vec4 FragColor;

int a[4] = int[](2,4,6,7);

vec2 offset[] = vec2[](
    vec2( -texstep, -texstep ), vec2( 0, -texstep ), vec2( texstep, -texstep ),
    vec2( -texstep, 0 ), vec2( 0, 0 ), vec2( texstep, 0 ),
    vec2( -texstep, texstep ), vec2( 0, texstep ), vec2( texstep, texstep )
);

float kernel[9] = float[](
    -1.0, -1.0, -1.0,
    -1.0, 8.0, -1.0,
    -1.0, -1.0, -1.0
);

void main(){
    vec4 col;
    for ( int i = 0; i < 9; i++ ){
        col += te( diffuse_sampler, texcoord + offset[i] ) * kernel[i];
    }
    col *= 2.0;
    col.r += tex( diffuse_sampler, texcoord + offset[3] * 5.0 ).r;
    col.g += tex( diffuse_sampler, texcoord ).g;
    col.b += tex( diffuse_sampler, texcoord + offset[5] * 5.0 ).b;

    col.a = 1.;

    FragColor = clamp(col, 0., 1.);
}

