#version 120
#include StdLib.frag

in vec2  texcoord;
uniform sampler2D diffuse_sampler;
uniform float texstep;


const mat3 sobelX = mat3(3.0, 0.0, -3.0,
                         10.0, 0.0, -10.0,
                         3.0, 0.0, -3.0);

const mat3 sobelY = mat3(3.0, 10.0, 3.0,
                         0.0, 0.0, 0.0,
                         -3.0, -10.0, -3.0);

float filter(mat3 fil){
    float outp = 0.;
    float minv = 0.;
    float maxv = 0.;

    for(int x =0;x<3;++x){
        for(int y =0;y<3;++y){
            vec2 shiftedtexcoord = texcoord+vec2(texstep*float(x-1), texstep*float(y-1));
            if(shiftedtexcoord.x<0.0 || shiftedtexcoord.y<0.0 || shiftedtexcoord.x>1.0 || shiftedtexcoord.y>1.0)
                continue;
            if(fil[x][y] < 0.)
                minv += fil[x][y];
            else
                maxv += fil[x][y];

            outp += tex(diffuse_sampler, shiftedtexcoord).r * fil[x][y];
        }
    }

    return outp/(maxv-minv);
}

void main() {
    float sx = filter(sobelX);
    float sy = filter(sobelY);

    float c = abs(sx+sy);
    c = step(0.05, c);
    FragColor = vec4(c, c, c, 1.);
}

