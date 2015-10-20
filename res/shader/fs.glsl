#version 150 core

uniform sampler2D texture_diffuse;
uniform mat4 stMatrix;
uniform vec4 color;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	vec4 texCoord4 = vec4(0,0,0,1);
	texCoord4.xy = pass_TextureCoord;
	out_Color = texture(texture_diffuse, (stMatrix*texCoord4).xy)*color;
}