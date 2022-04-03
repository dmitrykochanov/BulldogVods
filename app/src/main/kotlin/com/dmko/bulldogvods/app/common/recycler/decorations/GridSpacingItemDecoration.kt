package com.dmko.bulldogvods.app.common.recycler.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    @Px private val spacing: Int,
    private val includeLeftEdge: Boolean = true,
    private val includeRightEdge: Boolean = true,
    private val includeTopEdge: Boolean = true,
    private val includeBottomEdge: Boolean = true,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            val column = position % spanCount
            if (includeLeftEdge) {
                outRect.left = spacing - column * spacing / spanCount
            }
            if (includeRightEdge) {
                outRect.right = (column + 1) * spacing / spanCount
            }
            if (includeTopEdge && position < spanCount) {
                outRect.top = spacing
            }
            if (includeBottomEdge) {
                outRect.bottom = spacing
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }
}
