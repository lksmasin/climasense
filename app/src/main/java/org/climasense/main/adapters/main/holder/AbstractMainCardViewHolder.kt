package org.climasense.main.adapters.main.holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.cardview.widget.CardView
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.main.adapters.main.FirstCardHeaderController
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.theme.ThemeManager
import org.climasense.theme.resource.providers.ResourceProvider

@SuppressLint("ObjectAnimatorBinding")
abstract class AbstractMainCardViewHolder(view: View) :
    AbstractMainViewHolder(view) {
    private var mFirstCardHeaderController: FirstCardHeaderController? = null
    protected var mLocation: Location? = null
    @CallSuper
    open fun onBindView(
        activity: GeoActivity, location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean, firstCard: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled)
        mLocation = location
        val delegate = ThemeManager.getInstance(activity).weatherThemeDelegate
        val card = (itemView as CardView).apply {
            radius = delegate.getHomeCardRadius(activity)
            elevation = delegate.getHomeCardElevation(activity)
            setCardBackgroundColor(MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground))
        }
        val params = card.layoutParams as MarginLayoutParams
        params.setMargins(
            delegate.getHomeCardMargins(context),
            0,
            delegate.getHomeCardMargins(context),
            delegate.getHomeCardMargins(context)
        )
        card.layoutParams = params
        if (firstCard) {
            mFirstCardHeaderController = FirstCardHeaderController(activity, location).apply {
                bind(card.getChildAt(0) as LinearLayout)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBindView(
        context: Context, location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean
    ) {
        throw RuntimeException("Deprecated method.")
    }

    override fun onRecycleView() {
        super.onRecycleView()
        mFirstCardHeaderController?.let {
            it.unbind()
            mFirstCardHeaderController = null
        }
    }
}
