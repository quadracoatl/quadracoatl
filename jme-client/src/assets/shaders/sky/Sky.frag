
#import "/assets/shaders/common/ColorFunctions.glsllib"

uniform vec4 m_Color;
uniform int m_ColorBlendMode;
uniform samplerCube m_Texture;

varying vec3 direction;

void main() {
	vec3 normalizedDirection = normalize(direction);
	
	gl_FragColor = textureCube(m_Texture, normalizedDirection);
	gl_FragColor = blendColors(gl_FragColor, m_Color, m_ColorBlendMode);
}
