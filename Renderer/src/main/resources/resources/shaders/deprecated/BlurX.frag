#version 120
#include StdLib.frag

uniform sampler2D diffuse_sampler;
uniform float texstep;

in vec2 texcoord;

const float offset[3] = float[]( 0.0, 1.3846153846, 3.2307692308 );
const float weight[3] = float[]( 0.2255859375, 0.314208984375, 0.06982421875 );

void main(){
    
    FragColor = tex(diffuse_sampler, texcoord) * weight[0];

    for (int i=1; i<3; i++) {
        FragColor += tex(diffuse_sampler, texcoord+vec2(texstep * offset[i], 0.)) * weight[i];
        FragColor += tex(diffuse_sampler, texcoord-vec2(texstep * offset[i], 0.)) * weight[i];
    }
}


