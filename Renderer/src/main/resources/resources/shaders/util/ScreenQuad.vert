#pragma include includes/StdLib.vert

in vec2 in_position; //<Position>
out vec2 texcoord;

void main(void)
{
    gl_Position = vec4(in_position, -1., 1. );
    texcoord = in_position * 0.5 + 0.5;
}
