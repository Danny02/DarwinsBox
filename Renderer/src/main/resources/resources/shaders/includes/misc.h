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

// Pack three positive normalized numbers between 0.0 and 1.0 into a 32-bit fp
// channel of a render target
#define Pack3PNForFP32(channel) uintBitsToFloat(uint(channel.x * 65535.0 + 0.5) |(uint(channel.y * 255.0 + 0.5) << 16) |(uint(channel.z * 253.0 + 1.5) << 24))
/*float Pack3PNForFP32(vec3 channel)
{
 // layout of a 32-bit fp register
 // SEEEEEEEEMMMMMMMMMMMMMMMMMMMMMMM
 // 1 sign bit; 8 bits for the exponent and 23 bits for the mantissa
 uint uValue;

 // pack x
 uValue = uint(channel.x * 65535.0 + 0.5); // goes from bit 0 to 15
 
 // pack y in EMMMMMMM
 uValue |= uint(channel.y * 255.0 + 0.5) << 16;

 // pack z in SEEEEEEE
 // the last E will never be 1 because the upper value is 254
 // max value is 11111110 == 254
 // this prevents the bits of the exponents to become all 1
 // range is 1.. 254
 // to prevent an exponent that is 0 we add 1.0
 uValue |= uint(channel.z * 253.0 + 1.5) << 24;

 return uintBitsToFloat(uValue);
}*/

// unpack three positive normalized values from a 32-bit float

#define Unpack3PNFromUINT(uInputFloat) vec3((uInputFloat & uint(65535)) / 65535.0, ((uInputFloat >> uint(16)) & uint(255)) / 255.0, (((uInputFloat >> uint(24)) & uint(255)) - 1.0) / 253.0)
#define Unpack3PNFromFP32(fFloatFromFP32) Unpack3PNFromUINT(floatBitsToUint(fFloatFromFP32))

/*vec3 Unpack3PNFromFP32(float fFloatFromFP32)
{
 float a, b, c, d;
 uint uValue;
 
 uint uInputFloat =  floatBitsToUint(fFloatFromFP32);
 
 // unpack a
 // mask out all the stuff above 16-bit with 0xFFFF
 a = (uInputFloat & 0xFFFF) / 65535.0;
  
 b = ((uInputFloat >> 16) & 0xFF) / 255.0;
 
 // extract the 1..254 value range and subtract 1
 // ending up with 0..253
 c = (((uInputFloat >> 24) & 0xFF) - 1.0) / 253.0;

 return vec3(a, b, c);
}*/
#endif