#version 150
#extension GL_EXT_geometry_shader4 : enable 

layout(points) in;
layout(line_strip, max_vertices = 2) out;

in vec3 normal[1];
in vec3 tangent[1];
in vec3 bitangent[1];

out vec3 color;
const float n_scale = 0.2;

uniform mat4 mat_mvp;    //<MAT_MVP>

void drawLine(vec4 pos0, vec4 pos1)
{    
    gl_Position = pos0;
    EmitVertex();
    gl_Position = pos1;
    EmitVertex();
    gl_Position = pos0;
    EmitVertex();
}

void main()
{
    vec4 pos = gl_in[0].gl_Position;

    vec3 tan = normalize(cross(normal[0], vec3(1,0,0)));
    vec3 bin = normalize(cross(normal[0], tan));
    
    vec4 mpos = mat_mvp * pos;

    vec3 N = normalize(normal[0]);
    vec3 T = normalize(tangent[0]);
    vec3 B =normalize(bitangent[0]);

    color = vec3(1,0,0);
    drawLine(mpos, mat_mvp * vec4(pos.xyz + N * n_scale, 1.0));

    color = vec3(0,1,0);
    drawLine(mpos, mat_mvp * vec4(pos.xyz + T * n_scale, 1.0));

    color = vec3(0,0,1);
    drawLine(mpos, mat_mvp * vec4(pos.xyz + B * n_scale, 1.0));

    EndPrimitive();
}