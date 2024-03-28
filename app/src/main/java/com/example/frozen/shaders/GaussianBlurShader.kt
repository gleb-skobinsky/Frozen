package com.example.frozen.shaders

import android.graphics.Shader
import android.opengl.GLES20


class GaussianBlurShader :
    GlShader(VERTEX_SHADER, FRAGMENT_SHADER) {
    private var texelWidthOffset = 0.01f
    private var texelHeightOffset = 0.01f
    private var blurSize = 3f
    override fun getName(): String {
        return mShaderName
    }

    public override fun onDraw() {
        println("Called onDraw for gaussian blur")
        GLES20.glUniform1f(getHandle("texelWidthOffset"), texelWidthOffset)
        GLES20.glUniform1f(getHandle("texelHeightOffset"), texelHeightOffset)
        GLES20.glUniform1f(getHandle("blurSize"), blurSize)
    }

    companion object {
        private const val VERTEX_SHADER = "attribute vec4 aPosition;" +
                "attribute vec4 aTextureCoord;" +
                "const lowp int GAUSSIAN_SAMPLES = 9;" +
                "uniform highp float texelWidthOffset;" +
                "uniform highp float texelHeightOffset;" +
                "uniform highp float blurSize;" +
                "varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +
                "void main() {" +
                "gl_Position = aPosition;" +
                "highp vec2 vTextureCoord = aTextureCoord.xy;" +  // Calculate the positions for the blur
                "int multiplier = 0;" +
                "highp vec2 blurStep;" +
                "highp vec2 singleStepOffset = vec2(texelHeightOffset, texelWidthOffset) * blurSize;" +
                "for (lowp int i = 0; i < GAUSSIAN_SAMPLES; i++) {" +
                "multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));" +  // Blur in x (horizontal)
                "blurStep = float(multiplier) * singleStepOffset;" +
                "blurCoordinates[i] = vTextureCoord.xy + blurStep;" +
                "}" +
                "}"
        private const val FRAGMENT_SHADER = "precision mediump float;" +  // 演算精度を指定します。
                "const lowp int GAUSSIAN_SAMPLES = 9;" +
                "varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +
                "uniform lowp sampler2D sTexture;" +
                "void main() {" +
                "lowp vec4 sum = vec4(0.0);" +
                "sum += texture2D(sTexture, blurCoordinates[0]) * 0.05;" +
                "sum += texture2D(sTexture, blurCoordinates[1]) * 0.09;" +
                "sum += texture2D(sTexture, blurCoordinates[2]) * 0.12;" +
                "sum += texture2D(sTexture, blurCoordinates[3]) * 0.15;" +
                "sum += texture2D(sTexture, blurCoordinates[4]) * 0.18;" +
                "sum += texture2D(sTexture, blurCoordinates[5]) * 0.15;" +
                "sum += texture2D(sTexture, blurCoordinates[6]) * 0.12;" +
                "sum += texture2D(sTexture, blurCoordinates[7]) * 0.09;" +
                "sum += texture2D(sTexture, blurCoordinates[8]) * 0.05;" +
                "gl_FragColor = sum;" +
                "}"
    }
}