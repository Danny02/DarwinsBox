#version 120
#include StdLib.vert

in vec4 in_position;
out vec3 texcoord;

uniform mat3 mat_view;
uniform float ratio;

void main(void)
{
    gl_Position = vec4( in_position.xy, 0.0, 1.0 );
    gl_Position = sign( gl_Position );
    vec3 dir = vec3(-ratio * sign(gl_Position.x-0.5), -sign(gl_Position.y-0.5), 1.);
    //normalize(dir);
    texcoord = -(mat_view * dir);
}