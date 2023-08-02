package org.climasense.settings.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.climasense.R
import org.climasense.common.basic.models.options.appearance.DailyTrendDisplay
import org.climasense.common.ui.widgets.slidingItem.SlidingItemContainerLayout

class DailyTrendDisplayAdapter(
    private val mDailyTrendDisplayList: MutableList<DailyTrendDisplay>,
    private val mRemoveListener: (DailyTrendDisplay) -> Unit,
    private val mDragListener: (ViewHolder) -> Unit
) : RecyclerView.Adapter<DailyTrendDisplayAdapter.ViewHolder>() {

    inner class ViewHolder @SuppressLint("ClickableViewAccessibility") constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val container: SlidingItemContainerLayout = itemView.findViewById(R.id.item_card_display_container)
        val item: RelativeLayout = itemView.findViewById(R.id.item_card_display)
        val title: TextView = itemView.findViewById(R.id.item_card_display_title)
        val sortButton: ImageButton = itemView.findViewById(R.id.item_card_display_sortButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.item_card_display_deleteBtn)

        init {
            sortButton.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mDragListener(this)
                }
                false
            }
            deleteButton.setOnClickListener { removeItem(bindingAdapterPosition) }
        }

        fun onBindView(dailyTrendDisplay: DailyTrendDisplay) {
            title.text = dailyTrendDisplay.getName(title.context)
            container.swipe(0f)
            container.setOnClickListener { }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_card_display, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(mDailyTrendDisplayList[position])
    }

    override fun getItemCount() = mDailyTrendDisplayList.size

    val dailyTrendDisplayList: List<DailyTrendDisplay> = mDailyTrendDisplayList

    fun insertItem(dailyTrendDisplay: DailyTrendDisplay) {
        mDailyTrendDisplayList.add(dailyTrendDisplay)
        notifyItemInserted(mDailyTrendDisplayList.size - 1)
    }

    fun removeItem(adapterPosition: Int) {
        val dailyTrendDisplay = mDailyTrendDisplayList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        mRemoveListener(dailyTrendDisplay)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        mDailyTrendDisplayList.add(toPosition, mDailyTrendDisplayList.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }
}
