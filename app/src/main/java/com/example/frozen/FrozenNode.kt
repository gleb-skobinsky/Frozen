package com.example.frozen

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
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
                val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
                draw(
                    density = drawContext.density,
                    layoutDirection = layoutDirection,
                    canvas = Canvas(bitmap),
                    size = size
                ) {
                    contentScope.drawContent()
                }
                contentScope.drawContent()

                val glView = GLSurfaceView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(1000, 1000)
                    setRenderer(NewRenderer())
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