uniform sampler2D m_ColorMap;

varying vec2 texCoord;
varying vec2 quadSize;
varying vec2 texTileCoord;

void main() {
	vec2 correctedTexCoord = vec2(
		texTileCoord.x + mod(texCoord.x * quadSize.x, 1.0f / 6.0f),
		texTileCoord.y + mod(texCoord.y * quadSize.y, 1.0f));
	
	gl_FragColor = texture2D(m_ColorMap, correctedTexCoord);
}
