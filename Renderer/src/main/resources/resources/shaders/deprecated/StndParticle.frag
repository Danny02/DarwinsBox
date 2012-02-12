#include StdLib.frag

in float alpha;
in vec3 lightdir;

uniform sampler2D diffuse_sampler;


void main() {
    vec4 color = tex(diffuse_sampler, gl_PointCoord);
    color.a *= alpha;

    FragColor = color;
}

