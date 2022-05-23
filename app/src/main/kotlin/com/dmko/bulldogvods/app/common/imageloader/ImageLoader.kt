package com.dmko.bulldogvods.app.common.imageloader

import android.view.View
import android.widget.ImageView
import android.widget.TextView

interface ImageLoader {

    fun load(url: String, target: ImageView)

    fun loadIntoSpan(url: String, startIndexInclusive: Int, endIndexExclusive: Int, target: TextView) {
        loadIntoSpans(url, listOf(SpanPosition(startIndexInclusive, endIndexExclusive)), target)
    }

    fun loadIntoSpans(url: String, spanPositions: List<SpanPosition>, target: TextView)

    fun dispose(target: View)

    data class SpanPosition(
        val startIndexInclusive: Int,
        val endIndexExclusive: Int
    )
}
