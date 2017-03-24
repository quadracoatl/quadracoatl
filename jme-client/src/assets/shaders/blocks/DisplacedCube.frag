
#import "/assets/shaders/common/ColorFunctions.glsllib"
#import "/assets/shaders/common/LightingFunctions.glsllib"

uniform vec4 m_Color;
uniform int m_ColorBlendMode;
uniform sampler2D m_ColorMap;

uniform vec4[LIGHTS_MAX_COUNT] m_LightsColors;
uniform int m_LightsCount;
uniform vec3[LIGHTS_MAX_COUNT] m_LightsDirections;
uniform int[LIGHTS_MAX_COUNT] m_LightsTypes;

varying vec3 faceNormal;
varying vec2 texCoord;
varying vec4 texTileCoord;

void main() {
	vec2 correctedTexCoord = vec2(
		texTileCoord.x + mod(texCoord.x, texTileCoord.z - texTileCoord.x),
		texTileCoord.y + mod(texCoord.y, texTileCoord.w - texTileCoord.y));
		
	gl_FragColor = texture2D(m_ColorMap, correctedTexCoord);
	gl_FragColor = blendColors(gl_FragColor, m_Color, m_ColorBlendMode);
	
	vec4 lightingColor = getLightingColor(
			faceNormal,
			m_LightsCount,
			m_LightsColors,
			m_LightsDirections,
			m_LightsTypes);
	
	gl_FragColor = blendColors(gl_FragColor, lightingColor, BLEND_MODE_MULTIPLY);
}
