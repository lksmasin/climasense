package org.climasense.main.adapters.main.holder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import org.climasense.common.basic.models.Location
import org.climasense.common.utils.helpers.AsyncHelper
import org.climasense.main.utils.MainModuleUtils
import org.climasense.theme.resource.providers.ResourceProvider

abstract class AbstractMainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    protected lateinit var context: Context
    protected var provider: ResourceProvider? = null
    protected var itemAnimationEnabled = false
    private var mInScreen = false
    private var mItemAnimator: Animator? = null
    private var mDelayController: AsyncHelper.Controller? = null
    @CallSuper
    open fun onBindView(
        context: Context, location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean
    ) {
        this.context = context
        this.provider = provider
        this.itemAnimationEnabled = itemAnimationEnabled
        mInScreen = false
        mDelayController = null
        if (listAnimationEnabled) {
            itemView.alpha = 0f
        }
    }

    val top = itemView.top

    fun checkEnterScreen(
        host: RecyclerView,
        pendingAnimatorList: MutableList<Animator>,
        listAnimationEnabled: Boolean
    ) {
        if (!itemView.isLaidOut || top >= host.measuredHeight) {
            return
        }
        if (!mInScreen) {
            mInScreen = true
            if (listAnimationEnabled) {
                executeEnterAnimator(pendingAnimatorList)
            } else {
                onEnterScreen()
            }
        }
    }

    fun executeEnterAnimator(pendingAnimatorList: MutableList<Animator>) {
        itemView.alpha = 0f
        mItemAnimator = getEnterAnimator(pendingAnimatorList).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    pendingAnimatorList.remove(mItemAnimator)
                }
            })
        }
        mDelayController = AsyncHelper.delayRunOnUI({
            pendingAnimatorList.remove(mItemAnimator)
            onEnterScreen()
        }, mItemAnimator!!.startDelay)
        pendingAnimatorList.add(mItemAnimator!!)
        mItemAnimator!!.start()
    }

    protected open fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        return MainModuleUtils.getEnterAnimator(itemView, pendingAnimatorList.size)
    }

    open fun onEnterScreen() {
        // do nothing.
    }

    open fun onRecycleView() {
        mDelayController?.let {
            it.cancel()
            mDelayController = null
        }
        mItemAnimator?.let {
            it.cancel()
            mItemAnimator = null
        }
    }
}
