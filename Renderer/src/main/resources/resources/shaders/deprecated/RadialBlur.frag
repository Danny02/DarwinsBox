#version 120
#include StdLib.frag

// texture coordinates passed from the vertex shader
in vec2 texcoord;
in vec3 normal;

// some const, tweak for best look
const float sampleStrength = 30.;
const float sampels = 30.;
const float sampleDist = 0.3 / sampels;
const float sammul = 1./(sampels+1.);

// This texture should hold the image to blur.
// This can perhaps be a previous rendered scene.
uniform sampler2D diffuse_sampler;

uniform float texstep;
uniform float sampleDistMul;

void main(void)
{
    // some sample positions
    //float samples[10] =
    //float[](-0.08,-0.05,-0.03,-0.02,-0.01,0.01,0.02,0.03,0.05,0.08);

    // 0.5,0.5 is the center of the screen
    // so substracting uv from it will result in
    // a vector pointing to the middle of the screen
    vec2 dir = 0.5 - texcoord;
    //dir = vec2(-dir.y, dir.x);
    // calculate the distance to the center of the screen
    float dist = length(dir);
    dir *= dist*2.;

    // normalize the direction (reuse the distance)
    //dir = dir/dist;

    // this is the original colour of this fragment
    // using only this would result in a nonblurred version
    vec4 color = tex(diffuse_sampler, texcoord);

    vec4 sum = color;

    // take 10 additional blur samples in the direction towards
    // the center of the screen
    vec2 tmp = sampleDistMul * dir * sampleDist;

    for (int i = 0; i < sampels; i++){
        sum += tex(diffuse_sampler, texcoord + i * tmp);
    }

    // we have taken eleven samples
    sum *= sammul;

    // weighten the blur effect with the distance to the
    // center of the screen ( further out is blurred more)
    float t = dist * sampleStrength;
    t = clamp(t, 0.0, 1.0); //0 &lt;= t &lt;= 1

    //Blend the original color with the averaged pixels
    FragColor = mix(color, sum, t);

}