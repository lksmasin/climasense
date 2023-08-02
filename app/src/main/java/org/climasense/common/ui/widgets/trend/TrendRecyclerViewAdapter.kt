package org.climasense.common.ui.widgets.trend

import androidx.recyclerview.widget.RecyclerView
import org.climasense.common.basic.models.Location

abstract class TrendRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
    private var mLocation: Location
) : RecyclerView.Adapter<VH>() {
    var location: Location
        get() = mLocation
        set(location) {
            mLocation = location
            notifyDataSetChanged()
        }
}
