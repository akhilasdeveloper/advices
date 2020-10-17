package com.akhil.advices.dao

import android.graphics.drawable.TransitionDrawable

data class TransitionAndText(
    var drawable: TransitionDrawable,
    var accent: Int,
    var accentSecond: Int,
    var color: Int
)