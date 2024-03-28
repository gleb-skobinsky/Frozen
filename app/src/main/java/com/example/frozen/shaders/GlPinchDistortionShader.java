package com.example.frozen.shaders;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform2f;

public class GlPinchDistortionShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp vec2 center;" +
			"uniform highp float radius;" +
			"uniform highp float scale;" +

			"void main() {" +
				"highp vec2 textureCoordinateToUse = vTextureCoord;" +
				"highp float dist = distance(center, vTextureCoord);" +
				"textureCoordinateToUse -= center;" +
				"if (dist < radius) {" +
					"highp float percent = 1.0 + ((0.5 - dist) / 0.5) * scale;" +
					"textureCoordinateToUse = textureCoordinateToUse * percent;" +
				"}" +
				"textureCoordinateToUse += center;" +

				"gl_FragColor = texture2D(sTexture, textureCoordinateToUse);" +
			"}";

	private float mCenterX = 0.5f;
	private float mCenterY = 0.5f;
	private float mRadius = 1.0f;
	private float mScale = 0.5f;

	public GlPinchDistortionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	protected String mShaderName = "Pinch";

	public String getName() {
		return mShaderName;
	}

	public float getCenterX() {
		return mCenterX;
	}

	public void setCenterX(final float centerX) {
		mCenterX = centerX;
	}

	public float getCenterY() {
		return mCenterY;
	}

	public void setCenterY(final float centerY) {
		mCenterY = centerY;
	}

	public float getRadius() {
		return mRadius;
	}

	public void setRadius(final float radius) {
		mRadius = radius;
	}

	public float getScale() {
		return mScale;
	}

	public void setScale(final float scale) {
		mScale = scale;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform2f(getHandle("center"), mCenterX, mCenterY);
		glUniform1f(getHandle("radius"), (float) Math.sin(System.currentTimeMillis()/100.f));
		glUniform1f(getHandle("scale"), mScale);
	}

}