#if __VERSION__<=120
    #define texture(s,t) texture2D(s, t)
    #define in attribute
    #define out varying
#endif