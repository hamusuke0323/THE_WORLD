#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

void main() {
    vec4 texColor = texture2D(DiffuseSampler, texCoord);
    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    gl_FragColor = vec4(vec3(gray), 1.0);
}