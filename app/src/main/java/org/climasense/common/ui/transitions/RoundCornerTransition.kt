package org.climasense.common.ui.transitions

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import org.climasense.R

class RoundCornerTransition(context: Context, attrs: AttributeSet?) : Transition(context, attrs) {
    private val mRadiusFrom: Float
    private val mRadiusTo: Float

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerTransition)
        mRadiusFrom = a.getDimension(R.styleable.RoundCornerTransition_radius_from, 0f)
        mRadiusTo = a.getDimension(R.styleable.RoundCornerTransition_radius_to, 0f)
        a.recycle()
    }

    override fun getTransitionProperties(): Array<String> {
        return Companion.transitionProperties
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        transitionValues.values[PROPNAME_RADIUS] = mRadiusFrom
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        transitionValues.values[PROPNAME_RADIUS] = mRadiusTo
    }

    override fun createAnimator(
        sceneRoot: ViewGroup, startValues: TransitionValues,
        endValues: TransitionValues?
    ): Animator? {
        if (endValues == null) return null
        val animator = ValueAnimator.ofFloat(
            (startValues.values[PROPNAME_RADIUS] as Float?)!!,
            (endValues.values[PROPNAME_RADIUS] as Float?)!!
        )
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            endValues.view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        (valueAnimator.animatedValue as Float)
                    )
                }
            }
            endValues.view.clipToOutline = true
        }
        return animator
    }

    companion object {
        private const val PROPNAME_RADIUS = "climasense:roundCorner:radius"
        private val transitionProperties = arrayOf(
            PROPNAME_RADIUS
        )
    }
}