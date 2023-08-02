package org.climasense.common.utils

import android.content.Context
import android.os.Build
import org.climasense.BuildConfig
import org.climasense.common.utils.helpers.SnackbarHelper
import org.climasense.common.extensions.createFileInCacheDir
import org.climasense.common.extensions.getUriCompat
import org.climasense.common.extensions.toShareIntent
import org.climasense.common.extensions.withNonCancellableContext
import org.climasense.common.extensions.withUIContext

/**
 * Taken from Tachiyomi
 * Apache License, Version 2.0
 *
 * https://github.com/tachiyomiorg/tachiyomi/blob/75460e01c80a75d604ae4323c14ffe73252efa9e/app/src/main/java/eu/kanade/tachiyomi/util/CrashLogUtil.kt
 */

class CrashLogUtils(private val context: Context) {

    suspend fun dumpLogs() = withNonCancellableContext {
        try {
            val file = context.createFileInCacheDir("climasense_crash_logs.txt")
            Runtime.getRuntime().exec("logcat *:E -d -f ${file.absolutePath}").waitFor()
            file.appendText(getDebugInfo())

            val uri = file.getUriCompat(context)
            context.startActivity(uri.toShareIntent(context, "text/plain"))
        } catch (e: Throwable) {
            e.printStackTrace()
            withUIContext { SnackbarHelper.showSnackbar("Failed to get logs") }
        }
    }

    fun getDebugInfo(): String {
        return """
            App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.FLAVOR}, ${BuildConfig.VERSION_CODE}
            Android version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
            Android build ID: ${Build.DISPLAY}
            Device brand: ${Build.BRAND}
            Device manufacturer: ${Build.MANUFACTURER}
            Device name: ${Build.DEVICE}
            Device model: ${Build.MODEL}
            Device product name: ${Build.PRODUCT}
        """.trimIndent()
    }
}