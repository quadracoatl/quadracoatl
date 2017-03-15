
#import "/assets/shaders/common/ColorFunctions.glsllib"

uniform vec4 m_Color;
uniform int m_ColorBlendMode;
uniform sampler2D m_ColorMap;

varying vec2 texCoord;

void main() {
	gl_FragColor = texture2D(m_ColorMap, texCoord);
	gl_FragColor = blendColors(gl_FragColor, m_Color, m_ColorBlendMode);
}
