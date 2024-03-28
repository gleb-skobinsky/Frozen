package com.example.frozen.View

import com.example.frozen.camera.CameraHelper
import java.io.IOException


interface PreviewTexture {
    interface OnFrameAvailableListener {
        fun onFrameAvailable(previewTexture: PreviewTexture?)
    }

    fun setOnFrameAvailableListener(l: OnFrameAvailableListener?)
    val textureTarget: Int

    @Throws(IOException::class)
    fun setup(camera: CameraHelper?)
    fun updateTexImage()
    fun getTransformMatrix(mtx: FloatArray?)
}