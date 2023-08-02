package org.climasense.common.ui.widgets.slidingItem

import android.graphics.Canvas
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SlidingItemTouchCallback : ItemTouchHelper.Callback() {
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        )
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val slidingContainer = findSlidingContainer(viewHolder)
        if (isCurrentlyActive) {
            var originalElevation =
                slidingContainer.getTag(androidx.viewpager2.R.id.item_touch_helper_previous_elevation)
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(slidingContainer)
                val newElevation = 1.0f + findMaxElevation(recyclerView, slidingContainer)
                ViewCompat.setElevation(slidingContainer, newElevation)
                slidingContainer.setTag(
                    androidx.viewpager2.R.id.item_touch_helper_previous_elevation,
                    originalElevation
                )
            }
        }
        viewHolder.itemView.translationY = dY
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            slidingContainer.swipe(dX)
        }
    }

    companion object {
        private fun findMaxElevation(recyclerView: RecyclerView, itemView: View): Float {
            val childCount = recyclerView.childCount
            var max = 0.0f
            for (i in 0 until childCount) {
                val child = recyclerView.getChildAt(i)
                if (child !== itemView) {
                    val elevation = ViewCompat.getElevation(child)
                    if (elevation > max) {
                        max = elevation
                    }
                }
            }
            return max
        }

        private fun findSlidingContainer(
            viewHolder: RecyclerView.ViewHolder
        ): SlidingItemContainerLayout {
            if (viewHolder.itemView is SlidingItemContainerLayout) {
                return viewHolder.itemView as SlidingItemContainerLayout
            }
            if (viewHolder.itemView is CardView) {
                return (viewHolder.itemView as CardView).getChildAt(0) as SlidingItemContainerLayout
            }
            throw IllegalStateException("Cannot find a valid sliding container.")
        }
    }
}