package org.climasense.common.utils.helpers

import android.view.View
import org.climasense.climasense
import org.climasense.common.basic.GeoActivity
import org.climasense.common.snackbar.Snackbar

object SnackbarHelper {

    fun showSnackbar(
        content: String,
        action: String? = null,
        activity: GeoActivity? = null,
        listener: View.OnClickListener? = null
    ) {
        if (action != null && listener == null) {
            throw RuntimeException("Must send a non null listener as parameter.")
        }
        val container = (activity ?: climasense.instance.topActivity ?: return).provideSnackbarContainer()
        Snackbar.make(container.container, content, Snackbar.LENGTH_LONG, container.cardStyle)
            .setAction(action, listener)
            .setCallback(Snackbar.Callback())
            .show()
    }
}
