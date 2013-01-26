#ifndef misc_h
#define misc_h

#ifdef COMPILED
#define uconst const
#else
#define uconst uniform
#endif

#define NORMALIZE_8BIT 0.003921569 // 1/255

#if __VERSION__ >= 410
    #define HexColor(x) unpackUnorm4x8(x).gba
#else
    #define HexColor(x) (vec3((x>>16)&0xFF,(x>>8)&0xFF,(x)&0xFF)/255)
#endif

#define RGBColor(r, g, b) (vec3(r, g, b) * NORMALIZE_8BIT)

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
#endif