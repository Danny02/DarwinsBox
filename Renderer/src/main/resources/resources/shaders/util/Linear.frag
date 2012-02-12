//<include includes/StdLib.frag>

in vec2 texcoord;
in vec3 normal;
in float depth;

void main() {
//TODO wegen Mesh organisation, braucht selbe attribute
    vec3 tc = vec3(texcoord, 1.);
    vec3 N = normalize(normal) + tc;
    gl_FragColor = vec4(N-tc, depth);
}

