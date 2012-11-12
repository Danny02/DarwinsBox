#ifndef misc_h
#define misc_h

#define IFTRUE(x) if(x>0.){
#define IFFALSE(x) if(x<=0.){
#define ELSE }else{
#define ENDIF }

#ifdef COMPILED
#define uconst const
#else
#define uconst uniform
#endif

#define NORMALIZE_8BIT 0.003921569 // 1/255

#define fma(a, b, c) (a*b+c)
#define fnms(a, b, c) (-(a * b - c))
#define fast_lerp(t, a, b) fma(t, b, fnms(t, a, a))

vec4 texture2D_bilinear(sampler2D tex, vec2 uv, float textureSize, float texelSize) {
    vec2 f = fract(uv.xy * textureSize);
    vec4 t00 = texture(tex, uv);
    vec4 t10 = texture(tex, uv + vec2(texelSize, 0.0));
    vec4 tA = mix(t00, t10, f.x);
    vec4 t01 = texture(tex, uv + vec2(0.0, texelSize));
    vec4 t11 = texture(tex, uv + vec2(texelSize, texelSize));
    vec4 tB = mix(t01, t11, f.x);
    return mix(tA, tB, f.y);
}

float rand(vec2 n) {
    return 0.5 + 0.5 *
    fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

// Packing a [0-1] float value into a 4D vector where each component will be a 8-bits integer
vec4 packFloatToVec4i(float value)
{
   vec4 bitSh = vec4(256.0 * 256.0 * 256.0, 256.0 * 256.0, 256.0, 1.0);
   vec4 bitMsk = vec4(0.0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0);
   vec4 res = fract(value * bitSh);
   res -= res.xxyz * bitMsk;
   return res;
}
 
// Unpacking a [0-1] float value from a 4D vector where each component was a 8-bits integer
float unpackFloatFromVec4i(vec4 value)
{
   const vec4 bitSh = vec4(1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0);
   return dot(value, bitSh);
}

// unpack three positive normalized values from a 32-bit float
#ifdef __VERSION__ > 300
#define Pack3PNForFP32(channel) intBitsToFloat(int(channel.x * 65535.0 + 0.5) |(int(channel.y * 255.0 + 0.5) << 16) |(int(channel.z * 253.0 + 1.5) << 24))
#define Unpack3PNFromUINT(uInputFloat) vec3((uInputFloat & 65535) / 65535.0, ((uInputFloat >> 16) & 255) / 255.0, (((uInputFloat >> 24) & 255) - 1.0) / 253.0)
#define Unpack3PNFromFP32(fFloatFromFP32) Unpack3PNFromUINT(floatBitsToInt(fFloatFromFP32))
#else
// Packing a [0-1] float value into a 4D vector where each component will be a 8-bits integer
vec3 packFloatToVec3i(float value)
{
   vec3 bitSh = vec3(256.0 * 256.0, 256.0,1.0);
   vec3 bitMsk = vec3(0.0, 1.0 / 256.0, 1.0 / 256.0);
   vec3 res = fract(value * bitSh);
   res -= res.xxyz * bitMsk;
   return res;
}
 
// Unpacking a [0-1] float value from a 4D vector where each component was a 8-bits integer
float unpackFloatFromVec3i(vec3 value)
{
   const vec4 bitSh = vec3(1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0);
   return dot(value, bitSh);
}
#endif
#endif