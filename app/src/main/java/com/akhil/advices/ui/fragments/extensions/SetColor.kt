package com.akhil.advices.ui.fragments.extensions

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.akhil.advices.R
import com.akhil.advices.ui.fragments.AdviceFragment
import com.akhil.advices.util.Utilities
import kotlinx.android.synthetic.main.fragment_advice.*

internal fun AdviceFragment.setShareMenuIconTint() {
    for (i in 0 until popupMenu.menu.size()) {
        val drawable: Drawable = popupMenu.menu.getItem(i).icon
        drawable.mutate()
        drawable.setTint(transit.accent)
    }
}

internal fun AdviceFragment.setAlarmTint() {
    if (isNavExpanded())
        alarm.setColorFilter(transit.accent)
    else
        alarm.setColorFilter(Color.WHITE)
}

internal fun AdviceFragment.setAlarmTint(slide: Float) {
    val col = utilities.getFactColor(transit.accent, Color.WHITE, slide)
    alarm.setColorFilter(col)
}

internal fun AdviceFragment.setShareTint() {
    if (isMenuOpened)
        share.setColorFilter(transit.accent)
    else
        share.setColorFilter(Color.WHITE)
}

internal fun AdviceFragment.setSwitchTint(slide: Float) {
    val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

    val colPrimaryDis = ContextCompat.getColor(requireContext(), R.color.disabledColorPrimary)
    val colSecondDis = ContextCompat.getColor(requireContext(), R.color.disabledColorSecondary)
    val colPrimary = transit.accent
    val colSecond = transit.accentSecond

    val colorThumb = intArrayOf(
        utilities.getFactColor(colPrimaryDis, Color.WHITE, slide),
        utilities.getFactColor(colPrimary, Color.WHITE, slide)
    )

    val colorsTrack = intArrayOf(
        utilities.getFactColor(colSecondDis, Color.WHITE, slide),
        utilities.getFactColor(colSecond, Color.WHITE, slide)
    )

    switchButton.thumbTintList = ColorStateList(states, colorThumb)
    switchButton.trackTintList = ColorStateList(states, colorsTrack)
}

internal fun AdviceFragment.setSwitchTint() {
    val states =
        arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

    val colorThumb = intArrayOf(
        ContextCompat.getColor(requireContext(), R.color.disabledColorPrimary),
        transit.accent
    )

    val colorsTrack = intArrayOf(
        ContextCompat.getColor(requireContext(), R.color.disabledColorSecondary),
        transit.accentSecond
    )

    switchButton.thumbTintList = ColorStateList(states, colorThumb)
    switchButton.trackTintList = ColorStateList(states, colorsTrack)
}

internal fun AdviceFragment.setTimeButtonBackground(slide: Float) {

    if (utilities.isScheduled()) {
        val textCol = utilities.getFactColor(transit.color, Color.WHITE, slide)
        val bgCol = utilities.getFactColor(transit.accent, Color.WHITE, slide)
        time.setTextColor(textCol)
        time.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            orientation = GradientDrawable.Orientation.TR_BL
            colors = arrayOf(bgCol, bgCol).toIntArray()
        }
    } else {
        val bgCol = utilities.getFactColor(ContextCompat.getColor(requireContext(), R.color.disabledColorPrimary), Color.WHITE, slide)
        time.setTextColor(Color.WHITE)
        time.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            orientation = GradientDrawable.Orientation.TR_BL
            colors = arrayOf(bgCol, bgCol).toIntArray()
        }
    }
}

internal fun AdviceFragment.setTimeButtonBackground() {

    if (utilities.isScheduled()) {
        time.setTextColor(transit.color)
        time.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            orientation = GradientDrawable.Orientation.TR_BL
            colors = arrayOf(transit.accent, transit.accent).toIntArray()
        }
    } else {
        time.setTextColor(Color.WHITE)
        time.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            orientation = GradientDrawable.Orientation.TR_BL
            colors = arrayOf(
                ContextCompat.getColor(requireContext(), R.color.disabledColorPrimary),
                ContextCompat.getColor(
                    requireContext(),
                    R.color.disabledColorPrimary
                )
            ).toIntArray()
        }
    }
}

internal fun AdviceFragment.setTint() {
    text_view.setTextColor(transit.color)
    edit_text.setTextColor(transit.color)
//    refresh.setColorFilter(transit.accent)
    setTimeButtonBackground()
    setSwitchTint()
    setAlarmTint()
    setShareMenuIconTint()
    setShareTint()
}

