package com.example.frozen.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlThreex3TextureSamplingShader extends GlShader {
    private static final String THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER =
            "attribute vec4 aPosition;" +
                    "attribute vec4 aTextureCoord;" +

                    "uniform highp float texelWidth;" +
                    "uniform highp float texelHeight;" +

                    "varying highp vec2 textureCoordinate;" +
                    "varying highp vec2 leftTextureCoordinate;" +
                    "varying highp vec2 rightTextureCoordinate;" +

                    "varying highp vec2 topTextureCoordinate;" +
                    "varying highp vec2 topLeftTextureCoordinate;" +
                    "varying highp vec2 topRightTextureCoordinate;" +

                    "varying highp vec2 bottomTextureCoordinate;" +
                    "varying highp vec2 bottomLeftTextureCoordinate;" +
                    "varying highp vec2 bottomRightTextureCoordinate;" +

                    "void main() {" +
                    "gl_Position = aPosition;" +

                    "vec2 widthStep = vec2(texelWidth, 0.0);" +
                    "vec2 heightStep = vec2(0.0, texelHeight);" +
                    "vec2 widthHeightStep = vec2(texelWidth, texelHeight);" +
                    "vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);" +

                    "textureCoordinate = aTextureCoord.xy;" +
                    "leftTextureCoordinate = textureCoordinate - widthStep;" +
                    "rightTextureCoordinate = textureCoordinate + widthStep;" +

                    "topTextureCoordinate = textureCoordinate - heightStep;" +
                    "topLeftTextureCoordinate = textureCoordinate - widthHeightStep;" +
                    "topRightTextureCoordinate = textureCoordinate + widthNegativeHeightStep;" +

                    "bottomTextureCoordinate = textureCoordinate + heightStep;" +
                    "bottomLeftTextureCoordinate = textureCoordinate - widthNegativeHeightStep;" +
                    "bottomRightTextureCoordinate = textureCoordinate + widthHeightStep;" +
                    "}";

    private float mTexelWidth;
    private float mTexelHeight;

    public GlThreex3TextureSamplingShader(String fragmentShaderSource) {
        super(THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER, fragmentShaderSource);
    }

    public float getTexelWidth() {
        return mTexelWidth;
    }

    public void setTexelWidth(float texelWidth) {
        mTexelWidth = texelWidth;
    }

    public float getTexelHeight() {
        return mTexelHeight;
    }

    public void setTexelHeight(float texelHeight) {
        mTexelHeight = texelHeight;
    }

    //////////////////////////////////////////////////////////////////////////

    @Override
    public void setFrameSize(final int width, final int height) {
        mTexelWidth = 1f / width;
        mTexelHeight = 1f / height;
    }

    @Override
    public void onDraw() {
        glUniform1f(getHandle("texelWidth"), mTexelWidth);
        glUniform1f(getHandle("texelHeight"), mTexelHeight);
    }



}
