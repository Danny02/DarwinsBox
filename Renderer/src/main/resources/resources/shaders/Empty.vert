#version 120
#pragma include includes/StdLib.vert

in vec4 in_position; //<Position>

void main() {    
    gl_Position = in_position;
}
