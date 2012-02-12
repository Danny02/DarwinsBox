#ifndef StdLib
    #define StdLib
    #ifdef GL_ES
        precision highp float;
    #endif
    #if __VERSION__ <= 120
        #define texture(s,t) texture2D(s, t)
        //#define textureProj(s,t) texture2DProj(s, t)
        #define in varying
        #define FragColor gl_FragColor
        #define FragData gl_FragData
    #else
        #ifndef NO_OUT
            #ifdef MRT
                out vec4 FragData[MRT];
            #else
                out vec4 FragColor;
            #endif
        #endif
    #endif
#endif