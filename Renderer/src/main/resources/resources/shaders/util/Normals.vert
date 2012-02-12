#version 150

in vec4 in_position; //<Position>
in vec3 in_normal;   //<Normal>
in vec3 in_tangent;  //<Tangent>

out vec3 normal;
out vec3 tangent;
out vec3 bitangent;

void main() {
    normal = normalize(in_normal);
    tangent = normalize(in_tangent);
    bitangent = cross(normal, tangent);
    gl_Position = in_position;
}
