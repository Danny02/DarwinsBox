#include StdLib.frag

in vec2 texcoord;

uniform sampler2D tex;

void main() {
    vec4 c = tex(tex, texcoord);
    c.r *= c.a;
    c.g *= c.a;
    c.b *= c.a;
    FragColor = c;
}