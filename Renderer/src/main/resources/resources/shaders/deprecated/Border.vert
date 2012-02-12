#include StdLib.vert

in vec2 in_position;
in float in_alpha;
out float alpha;

void main() {
    alpha = in_alpha;
    gl_Position = vec4(in_position, -1, 1.);
}
