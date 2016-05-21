uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec2 inTexCoord3;

varying vec2 texCoord;
varying vec2 quadSize;
varying vec2 texTileCoord;

void main() {
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
	texCoord = inTexCoord;
	quadSize = inTexCoord2;
	texTileCoord = inTexCoord3;
}
