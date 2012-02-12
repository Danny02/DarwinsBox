#include StdLib.frag

in float alpha;
in vec3 lightdir;

uniform sampler2D diffuse_sampler;

uniform float ScatterAmt, ScatterSmooth;
uniform vec4 ScatterColor, AmbientColor;

const vec3 diffuse_color = vec3(0.4, 0.4, 0.4);
const float aplha_mul = 1.5;
const float soft_scale = 20. ;

void main() {
    vec4 color = tex(diffuse_sampler, gl_PointCoord);
    color.a *= alpha;

    vec3 normal = normalize (color.rgb * 2. - 1.);
    vec3 ldir = normalize (lightdir);
    float lightAmount = dot (ldir , normal);

    //vec2 lightUVlookup = vec2(lightAmount * 0.5 + 0.5, 0);
    //vec3 lightFalloff = texture2D (difflookup, lightUVlookup).rgb * 2.;

    //Diffuse Lighting
    color.rgb = clamp(lightAmount *  diffuse_color, 0., 1.);

    //Soft Scattering
    float val = smoothstep(ScatterAmt, ScatterSmooth + ScatterAmt, color.a);
    color.rgb += mix(ScatterColor.rgb, AmbientColor.rgb, val);
    color.a *= aplha_mul;

    FragColor = color;
}

