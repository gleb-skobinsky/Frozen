package com.example.frozen.renderer

import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.frozen.R
import com.example.frozen.Utils.OpenGlUtils
import com.example.frozen.ogles.GlView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MainRenderer(view: GlView) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener {
    private val vss = """attribute vec2 vPosition;
    attribute vec2 vTexCoord;
    varying vec2 texCoord;
    void main() {
      texCoord = vTexCoord;
      gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
    }"""
    private val fss = "#extension GL_OES_EGL_image_external : require\n" +
            "varying mediump vec2 texCoord;\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform sampler2D iChannel1;\n" +
            "\n" +
            "int off0 = 0;\n" +
            "int off1 = 1;\n" +
            "int off2 = 16; \n" +
            "int off3 = 17;\n" +
            "int off4 = 256;\n" +
            "int off5 = 257;\n" +
            "int off6 = 272;\n" +
            "int off7 = 273;\n" +
            "\n" +
            "\n" +
            "float interpr(int p, float dr,float dg, float db) {    \n" +
            "    float fr00 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off0),float(256.0)))), int(abs(float(p+off0)/256.0)))).r*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off1),float(256.0)))), int(abs(float(p+off1)/256.0)))).r*1.0*dr;\n" +
            "    float fr01 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off2),float(256.0)))), int(abs(float(p+off2)/256.0)))).r*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off3),float(256.0)))), int(abs(float(p+off3)/256.0)))).r*1.0*dr;\n" +
            "    float fr10 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off4),float(256.0)))), int(abs(float(p+off4)/256.0)))).r*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off5),float(256.0)))), int(abs(float(p+off5)/256.0)))).r*1.0*dr;\n" +
            "    float fr11 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off6),float(256.0)))), int(abs(float(p+off6)/256.0)))).r*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off7),float(256.0)))), int(abs(float(p+off7)/256.0)))).r*1.0*dr;\n" +
            "    \n" +
            "    float frb0 = fr00 * (1.0-db)+fr01*db;\n" +
            "    float frb1 = fr10 * (1.0-db)+fr11*db;\n" +
            "    float frbg = frb0 * (1.0-dg)+frb1*dg;\n" +
            "\n" +
            "    return frbg;\n" +
            "}\n" +
            "\n" +
            "float interpg(int p, float dr,float dg, float db) {    \n" +
            "    float fr00 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off0),float(256.0)))), int(abs(float(p+off0)/256.0)))).g*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off1),float(256.0)))), int(abs(float(p+off1)/256.0)))).g*1.0*dr;\n" +
            "    float fr01 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off2),float(256.0)))), int(abs(float(p+off2)/256.0)))).g*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off3),float(256.0)))), int(abs(float(p+off3)/256.0)))).g*1.0*dr;\n" +
            "    float fr10 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off4),float(256.0)))), int(abs(float(p+off4)/256.0)))).g*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off5),float(256.0)))), int(abs(float(p+off5)/256.0)))).g*1.0*dr;\n" +
            "    float fr11 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off6),float(256.0)))), int(abs(float(p+off6)/256.0)))).g*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off7),float(256.0)))), int(abs(float(p+off7)/256.0)))).g*1.0*dr;\n" +
            "\n" +
            "\n" +
            "    \n" +
            "    float frb0 = fr00 * (1.0-db)+fr01*db;\n" +
            "    float frb1 = fr10 * (1.0-db)+fr11*db;\n" +
            "    float frbg = frb0 * (1.0-dg)+frb1*dg;\n" +
            "\n" +
            "    return frbg;\n" +
            "}\n" +
            "\n" +
            "float interpb(int p, float dr,float dg, float db) {    \n" +
            "   float fr00 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off0),float(256.0)))), int(abs(float(p+off0)/256.0)))).b*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off1),float(256.0)))), int(abs(float(p+off1)/256.0)))).b*1.0*dr;\n" +
            "    float fr01 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off2),float(256.0)))), int(abs(float(p+off2)/256.0)))).b*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off3),float(256.0)))), int(abs(float(p+off3)/256.0)))).b*1.0*dr;\n" +
            "    float fr10 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off4),float(256.0)))), int(abs(float(p+off4)/256.0)))).b*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off5),float(256.0)))), int(abs(float(p+off5)/256.0)))).b*1.0*dr;\n" +
            "    float fr11 = \n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off6),float(256.0)))), int(abs(float(p+off6)/256.0)))).b*1.0*(1.0 - dr) +\n" +
            "        texture2D(iChannel1, vec2(int(abs(mod(float(p+off7),float(256.0)))), int(abs(float(p+off7)/256.0)))).b*1.0*dr;\n" +
            "    \n" +
            "    float frb0 = fr00 * (1.0-db)+fr01*db;\n" +
            "    float frb1 = fr10 * (1.0-db)+fr11*db;\n" +
            "    float frbg = frb0 * (1.0-dg)+frb1*dg;\n" +
            "\n" +
            "    return frbg;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "mediump vec4 Sepia( mediump vec4 color ) {\n" +
            "\treturn vec4(\n" +
            "        clamp(color.r * 0.393 + color.g * 0.769 + color.b * 0.189, 0.0, 1.0),\n" +
            "        clamp(color.r * 0.349 + color.g * 0.686 + color.b * 0.168, 0.0, 1.0),\n" +
            "        clamp(color.r * 0.272 + color.g * 0.534 + color.b * 0.131, 0.0, 1.0), \n" +
            "        color.a );\n" +
            "} \n" +
            "\n" +
            "float scale_R = (16.0-1.0)/256.0;\n" +
            "float scale_G = (16.0-1.0)/256.0;\n" +
            "float scale_B = (16.0-1.0)/256.0;\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "\t\n" +
            "    vec4 color = texture2D(sTexture, texCoord );\n" +
            "    float r = color.r;\n" +
            "    float b = color.b;\n" +
            "    float g = color.g;\n" +
            "    \n" +
            "    float fb = b*scale_B;\n" +
            "\tfloat fg = g*scale_G;\n" +
            "\tfloat fr = r*scale_R;\n" +
            "\tfloat lut_b = fb;\n" +
            "\tfloat lut_g = fg;\n" +
            "\tfloat lut_r = fr;\n" +
            "\tint p = int(lut_r+lut_b*16.0+lut_g*16.0*16.0);\n" +
            "    \n" +
            "\tfloat dr = fr-lut_r;\n" +
            "\tfloat dg = fg-lut_g;\n" +
            "\tfloat db = fb-lut_b;\n" +
            "    \n" +
            "    color.r = dr; //interpr(p, dr,dg,db);\n" +
            "    color.g = dg; //interpg(p, dr,dg,db);\n" +
            "    color.b = db; //interpb(p, dr,dg,db);\n" +
            "\t\n" +
            "    gl_FragColor = color;\n" +
            "}"
    private val fss_sepia = """#extension GL_OES_EGL_image_external : require
varying mediump vec2 texCoord;
precision mediump float;
uniform samplerExternalOES sTexture;
void main() {
   vec4 FragColor = texture2D(sTexture, texCoord);
   gl_FragColor.r = dot(FragColor.rgb, vec3(.393, .769, .189));
   gl_FragColor.g = dot(FragColor.rgb, vec3(.349, .686, .168));
   gl_FragColor.b = dot(FragColor.rgb, vec3(.272, .534, .131));
}"""
    private var hTex: IntArray = intArrayOf()
    private val pVertex: FloatBuffer
    private val pTexCoord: FloatBuffer
    private var hProgram = 0
    private var sepiaProgram = 0
    private val cyanoProgram = 0
    private var mCamera: Camera? = null
    private var mSTexture: SurfaceTexture? = null
    private var mUpdateST = false
    private val mView: GlView

    init {
        mView = view
        val vtmp = floatArrayOf(1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f)
        val ttmp = floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f)
        pVertex = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        pVertex.put(vtmp)
        pVertex.position(0)
        pTexCoord = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        pTexCoord.put(ttmp)
        pTexCoord.position(0)
    }

    fun close() {
        mUpdateST = false
        mSTexture!!.release()
        deleteTex()
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        //String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        //Log.i("mr", "Gl extensions: " + extensions);
        //Assert.assertTrue(extensions.contains("OES_EGL_image_external"));
        initTex()
        mSTexture = SurfaceTexture(hTex[0])
        mSTexture!!.setOnFrameAvailableListener(this)
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f)
        hProgram = loadShader(vss, fss)
        sepiaProgram = loadShader(vss, fss_sepia)
        hProgram = sepiaProgram
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        synchronized(this) {
            if (mUpdateST) {
                mSTexture!!.updateTexImage()
                mUpdateST = false
            }
        }
        GLES20.glUseProgram(hProgram)
        val ph = GLES20.glGetAttribLocation(hProgram, "vPosition")
        val tch = GLES20.glGetAttribLocation(hProgram, "vTexCoord")
        val th = GLES20.glGetUniformLocation(hProgram, "sTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, hTex[0])
        GLES20.glUniform1i(th, 0)
        val offsetDepthMapTextureUniform = GLES20.glGetUniformLocation(hProgram, "iChannel1")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTex[1])
        GLES20.glUniform1i(offsetDepthMapTextureUniform, 3)
        GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4 * 2, pVertex)
        GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4 * 2, pTexCoord)
        GLES20.glEnableVertexAttribArray(ph)
        GLES20.glEnableVertexAttribArray(tch)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFlush()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val param = mCamera!!.parameters
        val psize = param.supportedPreviewSizes
        if (psize.size > 0) {
            var i: Int
            i = 0
            while (i < psize.size) {
                if (psize[i].width < width || psize[i].height < height) break
                i++
            }
            if (i > 0) i--
            param.setPreviewSize(psize[i].width, psize[i].height)
            //Log.i("mr","ssize: "+psize.get(i).width+", "+psize.get(i).height);
        }
        param["orientation"] = "landscape"
        //param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera!!.parameters = param
        mCamera!!.startPreview()
    }

    private fun initTex() {
        val o = BitmapFactory.Options()
        o.inScaled = false
        val mFxBitmapId: Int = R.drawable.ic_launcher_background
        val mFxBitmap = BitmapFactory.decodeResource(mView.getResources(), mFxBitmapId, o)
        hTex = IntArray(2)
        GLES20.glGenTextures(2, hTex, 0)
        hTex[1] = -1
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, hTex[0])
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_NEAREST
        )
        hTex[1] = OpenGlUtils.loadTexture(mFxBitmap, hTex[1], true)
    }

    private fun deleteTex() {
        GLES20.glDeleteTextures(1, hTex, 0)
    }

    @Synchronized
    override fun onFrameAvailable(st: SurfaceTexture) {
        mUpdateST = true
        mView.requestRender()
    }

    companion object {
        private fun loadShader(vss: String, fss: String): Int {
            var vshader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            GLES20.glShaderSource(vshader, vss)
            GLES20.glCompileShader(vshader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(vshader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                Log.e("Shader", "Could not compile vshader")
                Log.v("Shader", "Could not compile vshader:" + GLES20.glGetShaderInfoLog(vshader))
                GLES20.glDeleteShader(vshader)
                vshader = 0
            }
            var fshader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fshader, fss)
            GLES20.glCompileShader(fshader)
            GLES20.glGetShaderiv(fshader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                Log.e("Shader", "Could not compile fshader")
                Log.v("Shader", "Could not compile fshader:" + GLES20.glGetShaderInfoLog(fshader))
                GLES20.glDeleteShader(fshader)
                fshader = 0
            }
            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vshader)
            GLES20.glAttachShader(program, fshader)
            GLES20.glLinkProgram(program)
            return program
        }
    }
}
