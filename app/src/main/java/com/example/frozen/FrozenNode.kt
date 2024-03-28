package com.example.frozen

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

internal abstract class FrozenNode : DelegatingNode() {
    open fun onUpdate() {}
}

internal class AndroidFrozenNode : FrozenNode(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode,
    LayoutAwareModifierNode,
    ObserverModifierNode {

    private var localView: ViewGroup? = null
    private lateinit var context: Context

    private var openGlAttached: Boolean = false

    override fun ContentDrawScope.draw() {
        val contentScope = this
        if (localView == null) {
            localView = currentValueOf(LocalView).castToGroup()
            context = currentValueOf(LocalContext)
        }
        if (!openGlAttached) {
            localView?.let {
                openGlAttached = true
                /*
                val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
                draw(
                    density = drawContext.density,
                    layoutDirection = layoutDirection,
                    canvas = Canvas(bitmap),
                    size = size
                ) {
                    contentScope.drawContent()
                }
                 */
                contentScope.drawContent()

                val glView = GLSurfaceView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(1000, 1000)
                    setEGLContextClientVersion(2)
                    setRenderer(NewRenderer())
                    setBackgroundColor(Color.Red.toArgb())
                }
                it.addView(glView)
            }
        }
    }


    override fun onObservedReadsChanged() {
        observeReads {
            localView = currentValueOf(LocalView).castToGroup()
            context = currentValueOf(LocalContext)
        }
    }

    private fun View.castToGroup(): ViewGroup? = this as? ViewGroup
}

internal data class FrozenElement(val id: String) : ModifierNodeElement<FrozenNode>() {
    override fun create(): FrozenNode = AndroidFrozenNode()

    override fun update(node: FrozenNode) {
        node.onUpdate()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "frozen"
        properties["id"] = id
    }
}

fun Modifier.frozen(id: String): Modifier = this then FrozenElement(id)