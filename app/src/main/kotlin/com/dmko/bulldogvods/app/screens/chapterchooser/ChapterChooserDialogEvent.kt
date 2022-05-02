package com.dmko.bulldogvods.app.screens.chapterchooser

import kotlin.time.Duration

data class ChapterChooserDialogEvent(
    val vodId: String,
    val selectedStartOffset: Duration
)
