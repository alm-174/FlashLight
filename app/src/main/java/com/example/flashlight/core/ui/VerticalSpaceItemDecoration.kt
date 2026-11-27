package com.example.flashlight.core.ui

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    @param:Px private val spacePx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION && position != (parent.adapter?.itemCount?.minus(1) ?: -1)) {
            outRect.bottom = spacePx
        }
    }
}
