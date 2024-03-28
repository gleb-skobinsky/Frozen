package com.example.frozen

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.example.frozen.ui.theme.FrozenTheme

class MainActivity : ComponentActivity() {
    private val frozenId = uuid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = GLSurfaceView(this).apply {
            layoutParams = ViewGroup.LayoutParams(1000, 1000)
            setEGLContextClientVersion(2)
            setZOrderOnTop(true)
            setEGLConfigChooser(
                /* redSize = */ 8,
                /* greenSize = */ 8,
                /* blueSize = */ 8,
                /* alphaSize = */ 8,
                /* depthSize = */ 16,
                /* stencilSize = */ 0
            )
            holder.setFormat(PixelFormat.RGBA_8888)
            setRenderer(NewRenderer())
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        }

        val layout = FrameLayout(this)
        val composeScene = ComposeView(this)
        composeScene.setContent {
            FrozenTheme {
                Text(
                    text = SAMPLE_TEXT,
                    modifier = Modifier
                        .frozen(frozenId)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        layout.addView(composeScene)
        layout.addView(glView)
        setContentView(layout)
    }
}
