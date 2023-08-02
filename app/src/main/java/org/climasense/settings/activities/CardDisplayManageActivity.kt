package org.climasense.settings.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.options.appearance.CardDisplay
import org.climasense.common.ui.adapters.TagAdapter
import org.climasense.common.ui.decorations.GridMarginsDecoration
import org.climasense.common.ui.decorations.ListDecoration
import org.climasense.common.ui.widgets.slidingItem.SlidingItemTouchCallback
import org.climasense.common.utils.ColorUtils
import org.climasense.databinding.ActivityCardDisplayManageBinding
import org.climasense.settings.SettingsManager
import org.climasense.settings.adapters.CardDisplayAdapter
import org.climasense.theme.ThemeManager
import java.util.*

class CardDisplayManageActivity : GeoActivity() {
    private lateinit var mBinding: ActivityCardDisplayManageBinding
    private lateinit var mCardDisplayAdapter: CardDisplayAdapter
    private var mCardDisplayItemTouchHelper: ItemTouchHelper? = null
    private var mTagAdapter: TagAdapter? = null
    private var mBottomAnimator: AnimatorSet? = null
    private var mBottomBarVisibility: Boolean? = null

    @Px
    private var mElevation = 0

    private inner class CardTag(
        var tag: CardDisplay
    ) : TagAdapter.Tag {
        override val name = tag.getName(this@CardDisplayManageActivity)
    }

    private inner class CardDisplaySwipeCallback : SlidingItemTouchCallback() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition
            mCardDisplayAdapter.moveItem(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mCardDisplayAdapter.removeItem(viewHolder.bindingAdapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            ViewCompat.setElevation(
                viewHolder.itemView,
                (if (dY != 0f || isCurrentlyActive) mElevation else 0).toFloat()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCardDisplayManageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mElevation = resources.getDimensionPixelSize(R.dimen.touch_rise_z)
        mBinding.appBar.injectDefaultSurfaceTintColor()
        mBinding.toolbar.setBackgroundColor(
            ColorUtils.getWidgetSurfaceColor(
                6f,
                ThemeManager.getInstance(this).getThemeColor(this, androidx.appcompat.R.attr.colorPrimary),
                ThemeManager.getInstance(this).getThemeColor(this, com.google.android.material.R.attr.colorSurface)
            )
        )
        mBinding.toolbar.setNavigationOnClickListener { finish() }
        val displayTags = SettingsManager.getInstance(this).cardDisplayList
        mCardDisplayAdapter = CardDisplayAdapter(
            displayTags.toMutableList(),
            { cardDisplay: CardDisplay ->
                setResult(RESULT_OK)
                mTagAdapter!!.insertItem(CardTag(cardDisplay))
                resetBottomBarVisibility()
            },
            { holder: CardDisplayAdapter.ViewHolder ->
                mCardDisplayItemTouchHelper!!.startDrag(holder)
            }
        )
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.addItemDecoration(
            ListDecoration(
                this,
                ThemeManager.getInstance(this).getThemeColor(this, com.google.android.material.R.attr.colorOutline)
            )
        )
        mBinding.recyclerView.adapter = mCardDisplayAdapter
        mCardDisplayItemTouchHelper = ItemTouchHelper(CardDisplaySwipeCallback()).apply {
            attachToRecyclerView(mBinding.recyclerView)
        }
        val otherTags = CardDisplay.entries.toMutableList()
        for (i in otherTags.indices.reversed()) {
            for (displayCard in displayTags) {
                if (otherTags[i] === displayCard) {
                    otherTags.removeAt(i)
                    break
                }
            }
        }
        val tagList: MutableList<TagAdapter.Tag> = ArrayList()
        for (tag in otherTags) {
            tagList.add(CardTag(tag))
        }
        val colors = ThemeManager.getInstance(this).getThemeColors(
            this, intArrayOf(
                com.google.android.material.R.attr.colorOnPrimaryContainer,
                com.google.android.material.R.attr.colorOnSecondaryContainer,
                com.google.android.material.R.attr.colorPrimaryContainer,
                com.google.android.material.R.attr.colorSecondaryContainer
            )
        )
        mTagAdapter = TagAdapter(
            tagList,
            colors[0],
            colors[1],
            colors[2],
            colors[3],
            { _: Boolean, _: Int, newPosition: Int ->
                setResult(RESULT_OK)
                val tag = mTagAdapter!!.removeItem(newPosition) as CardTag
                mCardDisplayAdapter.insertItem(tag.tag)
                resetBottomBarVisibility()
                true
            }
        )
        mBinding.bottomRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        mBinding.bottomRecyclerView.addItemDecoration(
            GridMarginsDecoration(
                resources.getDimension(R.dimen.normal_margin), mBinding.bottomRecyclerView
            )
        )
        mBinding.bottomRecyclerView.adapter = mTagAdapter
        mBottomAnimator = null
        mBottomBarVisibility = false
        mBinding.bottomRecyclerView.post { resetBottomBarVisibility() }
    }

    override fun onStop() {
        super.onStop()
        val oldList = SettingsManager.getInstance(this).cardDisplayList
        val newList = mCardDisplayAdapter.cardDisplayList
        if (oldList != newList) {
            SettingsManager.getInstance(this).cardDisplayList = newList
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        // do nothing.
    }

    private fun resetBottomBarVisibility() {
        val visible = mTagAdapter!!.itemCount != 0
        if (mBottomBarVisibility == null || mBottomBarVisibility != visible) {
            mBottomBarVisibility = visible
            mBottomAnimator?.cancel()
            mBottomAnimator = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(
                        mBinding.bottomBar, "alpha",
                        mBinding.bottomBar.alpha, (if (visible) 1 else 0).toFloat()
                    ),
                    ObjectAnimator.ofFloat(
                        mBinding.bottomBar, "translationY",
                        mBinding.bottomBar.translationY, (if (visible) 0 else mBinding.bottomBar.measuredHeight).toFloat()
                    )
                )
                duration = (if (visible) 350 else 150).toLong()
                interpolator = if (visible) DecelerateInterpolator(2f) else AccelerateInterpolator(2f)
            }.also { it.start() }
        }
    }
}