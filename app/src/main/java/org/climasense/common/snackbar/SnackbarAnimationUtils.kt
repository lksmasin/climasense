package org.climasense.common.snackbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Rect
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import org.climasense.common.basic.insets.FitBothSideBarHelper
import org.climasense.common.basic.insets.FitBothSideBarView
import org.climasense.common.extensions.FLOATING_DECELERATE_INTERPOLATOR
import org.climasense.common.extensions.getFloatingOvershotEnterAnimators

object SnackbarAnimationUtils : AnimationUtils() {
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

    fun getEnterAnimator(view: View, cardStyle: Boolean): Animator {
        view.translationY = view.height.toFloat()
        view.scaleX = if (cardStyle) 1.1f else 1f
        view.scaleY = if (cardStyle) 1.1f else 1f
        val animators = view.getFloatingOvershotEnterAnimators()
        if (!cardStyle) {
            animators[0].interpolator = FLOATING_DECELERATE_INTERPOLATOR
        }
        return AnimatorSet().apply {
            playTogether(animators[0], animators[1], animators[2])
        }
    }

    fun consumeInsets(view: View, insets: Rect) {
        val fitInsetsHelper = FitBothSideBarHelper(
            view, FitBothSideBarView.SIDE_BOTTOM
        )
        fitInsetsHelper.fitSystemWindows(insets) {
            insets.set(fitInsetsHelper.windowInsets)
            view.requestLayout()
        }
    }
}