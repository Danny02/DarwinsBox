#include StdLib.frag

in float alpha;

uniform vec3 color = vec3(0.);

void main() {
    // Set the fragment color for example to gray, alpha 1.0
    FragColor = vec4(color, alpha);
}

