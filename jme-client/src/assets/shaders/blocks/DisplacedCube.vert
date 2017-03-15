uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inNormal;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec4 inTexCoord2;

varying vec3 faceNormal;
varying vec2 quadSize;
varying vec2 texCoord;
varying vec4 texTileCoord;

void main() {
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
	faceNormal = inNormal;
	texCoord = inTexCoord;
	texTileCoord = inTexCoord2;
}
