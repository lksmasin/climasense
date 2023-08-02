package org.climasense.settings.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import james.adaptiveicon.AdaptiveIcon
import james.adaptiveicon.AdaptiveIconView
import org.climasense.R
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.theme.resource.ResourceHelper
import org.climasense.theme.resource.providers.ResourceProvider
import java.util.*

object AdaptiveIconDialog {
    fun show(
        context: Context,
        code: WeatherCode,
        daytime: Boolean,
        provider: ResourceProvider
    ) {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_adaptive_icon, null, false)
        initWidget(view, code, daytime, provider)
        MaterialAlertDialogBuilder(context)
            .setTitle(code.name + if (daytime) "_DAY" else "_NIGHT")
            .setView(view)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget(
        view: View,
        code: WeatherCode,
        daytime: Boolean,
        provider: ResourceProvider
    ) {
        val iconView = view.findViewById<AdaptiveIconView>(R.id.dialog_adaptive_icon_icon)
        iconView.icon = AdaptiveIcon(
            ResourceHelper.getShortcutsForegroundIcon(provider, code, daytime),
            ColorDrawable(Color.TRANSPARENT),
            0.5
        )
        iconView.setPath(Random().nextInt(AdaptiveIconView.PATH_TEARDROP + 1))
    }
}
