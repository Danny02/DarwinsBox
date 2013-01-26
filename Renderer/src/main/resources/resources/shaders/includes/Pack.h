vec3 unpackFloat(float f)
{
  vec3 rgb;

  rgb = floor(f * vec3(128.0, 128.0 * 256.0, 128.0 * 256.0 * 256.0));
  rgb -= vec3 (0.0, rgb.r * 256.0, rgb.g * 256.0);
  rgb /= 255.0;

  return rgb;
}

float packFloat(vec3 rgb)
{
  return dot(rgb, vec3(1.0, 1.0 / 256.0, 1.0 / 256.0 / 256.0) * 255.0 / 128.0);
}