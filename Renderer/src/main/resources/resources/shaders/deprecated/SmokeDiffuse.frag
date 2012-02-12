#version 120

uniform sampler2D tex, speclookup, difflookup, scenedepth;

uniform float ScatterAmt, ScatterSmooth, Lstrength, Lsize;
uniform vec4 ScatterColor, AmbientColor;
uniform vec3 color;

const vec3 diffuse_color = vec3(0.4, 0.4, 0.4);
const vec2 lightpos = vec2(0.5, 0.3);
const float aplha_mul = 1.5;
const float soft_scale = 20. ;

void main() {
    vec4 col;
    vec2 tc = gl_TexCoord[0].xy;
    vec3 lpos = gl_LightSource[0].position.xyz;

    float sdepth = texture2D(scenedepth, gl_FragCoord.xy/1024., 0.).r;

    vec4 particle = texture2D(tex, tc, 0.);
    vec3 normal = normalize (particle.rgb * 2. - 1.);
    vec3 lightdir = normalize (lpos);
    float lightAmount = dot ( lightdir , normal);

    vec2 lightUVlookup = vec2(lightAmount * 0.5 + 0.5, 0);
    vec3 lightFalloff = texture2D (difflookup, lightUVlookup).rgb * 2.;

    //Diffuse Lighting
    col.a = particle.a;
    col.rgb = clamp(lightFalloff *  diffuse_color, 0., 1.);

    //Specular Scattering
    vec2 dist = (tc - lightpos)*Lsize;
    vec2 scatterLookup;
    scatterLookup.x = dot (dist, dist);
    scatterLookup.y = col.a * Lstrength;
    float cm = texture2D(speclookup, scatterLookup, 0.).r;
    col += vec4(color * cm, 0.0);

    //Soft Scattering
    float val = smoothstep(ScatterAmt, ScatterSmooth + ScatterAmt, col.a);
    col.rgb += mix(ScatterColor.rgb, AmbientColor.rgb, val);
    col.a *= aplha_mul;

    float soft = clamp((sdepth - gl_FragCoord.z) * soft_scale, 0., 1.);
    col.a *= soft;

    gl_FragColor = col;
}

