#version 120
#include StdLib.frag

in vec2 texcoord;

uniform sampler2D diffuse_sampler;
uniform sampler2D noise_sampler;

uniform float brighten;
uniform float noiseOffsetX;
uniform float noiseOffsetY;
uniform float distort;
uniform float distortAmount;

const vec4 lumCoef = vec4( 0.2125, 0.7154, 0.0721, 1.0 );
const vec4 white = vec4( 1.0, 1.0, 1.0, 1.0 );

vec4 overlay( in vec4 baseCol, in vec4 blendCol ){
    float luminance = dot( baseCol, lumCoef );

    vec4 col;
    if ( luminance < 0.45 ){
        col = 2.0 * baseCol * blendCol;
    }
    else if ( luminance > 0.55 ){
        col = white - 2.0 * ( white - blendCol ) * ( white - baseCol );
    }
    else{
        vec4 r1 = 2.0 * blendCol * baseCol;
        vec4 r2 = white - 2.0 * ( white - blendCol ) * ( white - baseCol );
        col = mix( r1, r2, ( luminance - 0.45 ) * 10.0 );
    }

    return col;
}

void main(){
    vec2 texCoord = texcoord;

    if ( distortAmount > 0.0 ){
        texCoord += distortAmount * ( vec2( 0.5 ) - texCoord ) * ( 0.3 + sin( distort + cos( 1.7 * distort + 3.0 * texCoord.x + 2.0 * texCoord.y ) ) * 0.3 );
    }

    vec4 baseCol = tex( diffuse_sampler, texCoord );
    vec4 noiseCol = tex( noise_sampler, texcoord + vec2( noiseOffsetX, noiseOffsetY ) * ( 1.0 + noiseOffsetX + noiseOffsetY ) ) * 0.5 + vec4( 0.25 );

    baseCol.a = 1.0;

    float luminance = dot( baseCol, lumCoef );

    vec4 col = baseCol;
    col = overlay( col, noiseCol );
    col *= brighten;

    FragColor = clamp(col, 0., 1.);
}