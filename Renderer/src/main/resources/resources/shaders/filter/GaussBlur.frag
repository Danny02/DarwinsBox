#version 150
#pragma include includes/StdLib.frag
//<defines [X,Y],GEWICHTUNG>

uniform sampler2D texture;
uniform float offset;

#ifdef GEWICHTUNG
    uniform sampler2D filter_weight;
#endif

in vec2 texcoord;

const int FILTER_RADIUS 2;

const float offsets[FILTER_RADIUS] = float[](1.3846153846, 3.2307692308 );//<generate GausConstants.Offsets>
const float center_weight = 0.231933593;//<generate GausConstants.Center>
const float weight[FILTER_RADIUS] = float[](0.314208984375, 0.06982421875 );//<generate GausConstants.Weights>

/** So in etwa könnte man werte generatoren implementieren
const float offsets[FILTER_RADIUS]; //<generate GausConstants.Offsets>
const float center_weight;          //<generate GausConstants.Center(FILTER_RADIUS))>
const float weight[FILTER_RADIUS];  //<generate GausConstants.Weights>
*/

#ifdef X
    #define DIRECTION(a) a.x
#elif defined Y
    #define DIRECTION(a) a.x
#endif

vec4 addSamples(int i) {
    vec4 color;
    vec2 coords = texcoord;
    float off = offset * offsets[i];
    DIRECTION(coords) += off;
    color = texture(texture, coords) * weight[i];

    coords = texcoord;
    DIRECTION(coords) -= off;
    color += texture(texture, coords) * weight[i];
}

void main(){
    vec4 color = texture(texture, texcoord) * center_weight;

    #UNROLL FILTER_RADIUS
    for(int i = 0; i < FILTER_RADIUS; ++i)
        color += addSamples(i);

    #ifdef GEWICHTUNG
        float w = texture(filter_weight, texcoord).r;
        color = mix(main, color, clamp(w, 0.0, 1.0));
    #endif
    FragColor = color;
}
