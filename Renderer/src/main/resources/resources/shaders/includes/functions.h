//see http://iquilezles.org/www/articles/functions/functions.htm

/**
 * Say you don't want to change a value unless it's too small and screws some
 * of your computations up. Then, rather than doing a sharp conditional branch,
 * you can blend your value with your threshold, and do it smoothly
 * (say, with a cubic polynomial). Set m to be your threshold
 * (anything above m stays unchanged), and n the value things will take when
 * your value is zero. Then set
 */
float almostIdentity(float x, float m, float n)
{
    if(x > m)
        return x;

    float a = 2.0 * n - m;
    float b = 2.0 * m - 3.0 * n;
    float t = x / m;

    return (a * t + b) * t * t + n;
}

/**
 * Great for triggering behaviours or making envelopes for music or animation,
 * and for anything that grows fast and then slowly decays. Use k to control
 * the streching o the function. Btw, it's maximun, which is 1.0, happens at
 * exactly x = 1/k.
 */
float impulse(float x, float k)
{
    float h = k * x;
    return h * exp(1.0 - h);
}

/**
 * Of course you found yourself doing smoothstep(c-w,c,x)-smoothstep(c,c+w,x)
 *  very often, probably cause you were trying to isolate some features. Then
 *  this cubicPulse() is your friend. Also, why not, you can use it as a cheap
 *  replacement for a gaussian.
 */
float cubicPulse(float x, float c, float w)
{
    x = abs(x - c);
    if(x > w)
        return 0.0;

    x /= w;
    return 1.0 - x * x * (3.0 - 2.0 * x);
}

/**
 * A natural attenuation is an exponential of a linearly decaying quantity:
 *  exp(-x). A gaussian, is an exponential of a quadratically
 *  decaying quantity: exp(-x²). You can go on increasing
 *  powers, and get a sharper and sharper smoothstep(), until you get a step()
 *  in the limit.
 */
float expStep(float x, float k, float n)
{
    return exp(-k * pow(x, n));
}

/*
 * The function interpolates smoothly between two input values based on a third one
 * that should be between the first two.
 * The returned value is clamped between 0 and 1.
 * The slope (i.e. derivative) of the smoothstep function starts at 0 and ends at 0.
 * Smoothstep implements cubic Hermite interpolation after doing a clamp:
 *
float smoothstep(float edge0, float edge1, float x)
{
    // Scale, bias and saturate x to 0..1 range
    x = saturate((x - edge0)/(edge1 - edge0));
    // Evaluate polynomial
    return x*x*(3 - 2*x);
}*/

/**
 * Ken Perlin suggests an improved version of the smoothstep
 *  function which has zero 1st and 2nd order derivatives at t=0 and t=1:
 *  smootherstep(t) = 6t^5 − 15t^4 + 10t^3
 */
float smootherstep(float edge0, float edge1, float x)
{
    // Scale, and clamp x to 0..1 range
    x = clamp((x - edge0)/(edge1 - edge0), 0, 1);
    // Evaluate polynomial
    return x*x*x*(x*(x*6 - 15) + 10);
}