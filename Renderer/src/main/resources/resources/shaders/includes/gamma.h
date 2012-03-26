#ifndef GAMMA_VAL
    #define GAMMA_VAL 2.2
#endif

uconst float gamma = GAMMA_VAL;

#ifdef COMPILED
    const float invgamma = 1. / gamma;
#else
    #define invgamma (1./gamma)
#endif

#ifdef GAMMA
    #define toLinear(x) pow(x, gamma)
    #define toGamma(x) pow(x, invgamma)
#else
    #define toLinear(x) x
    #define toGamma(x) x
#endif
