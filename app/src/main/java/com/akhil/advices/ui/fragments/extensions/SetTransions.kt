package com.akhil.advices.ui.fragments.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.akhil.advices.ui.fragments.AdviceFragment
import com.akhil.advices.util.Constants
import com.akhil.advices.util.Utilities
import kotlinx.android.synthetic.main.fragment_advice.*

internal fun AdviceFragment.startFadeInAnimation() {
    text_view.startAnimation(fadeIn)
//    refresh.startAnimation(fadeIn)
}

internal fun AdviceFragment.startFadeOutAnimation() {
    text_view.startAnimation(fadeOut)
    if (lanuch_icon.visibility == View.VISIBLE)
        lanuch_icon.startAnimation(fadeOut)
//    refresh.startAnimation(fadeOut)
}

internal fun AdviceFragment.initTransition() {
    transit = utilities.getInitTransition()
    main_activity_bg.background = transit.drawable
    setTint()
    transit.drawable.startTransition(Constants.ANIMATION_DURATION)
}

internal fun AdviceFragment.initNextTransition() {
    transit = utilities.getNextTransition(transit.drawable)
    main_activity_bg.background = transit.drawable
}

internal fun AdviceFragment.initNextTransitionByColor(color:Int) {
    transit = utilities.getTransitionByColor(transit.drawable, color)
    main_activity_bg.background = transit.drawable
}

internal fun AdviceFragment.initAnimations() {
    fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
        duration = Constants.ANIMATION_EXIT_FADE_DURATION
        startOffset = 0
    }
    fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
        duration = Constants.ANIMATION_ENTER_FADE_DURATION
        startOffset = 0
    }

    fadeIn.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            isAnimationLoading.postValue(false)
            text_view.alpha = 1.0f
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })

    fadeOut.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            isAnimationLoading.postValue(true)
        }

        override fun onAnimationEnd(p0: Animation?) {
            setData()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })

    rotate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            if (isLoading) {
                refresh.startAnimation(rotate)
            }else{
                startFadeOutAnimation()
            }
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })
}