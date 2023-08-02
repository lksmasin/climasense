package org.climasense.common.basic

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import org.climasense.climasense
import org.climasense.common.basic.insets.FitHorizontalSystemBarRootLayout
import org.climasense.common.extensions.isDarkMode
import org.climasense.common.snackbar.SnackbarContainer
import org.climasense.common.extensions.setLanguage
import org.climasense.common.extensions.setSystemBarStyle
import org.climasense.settings.SettingsManager

abstract class GeoActivity : AppCompatActivity() {

    lateinit var fitHorizontalSystemBarRootLayout: FitHorizontalSystemBarRootLayout

    private class KeyboardResizeBugWorkaround private constructor(activity: GeoActivity) {
        private val root = activity.fitHorizontalSystemBarRootLayout
        private val rootParams: ViewGroup.LayoutParams
        private var usableHeightPrevious = 0

        private fun possiblyResizeChildOfContent() {
            val usableHeightNow = computeUsableHeight()

            if (usableHeightNow != usableHeightPrevious) {
                val screenHeight = root.rootView.height
                val keyboardExpanded: Boolean

                if (screenHeight - usableHeightNow > screenHeight / 5) {
                    // keyboard probably just became visible.
                    keyboardExpanded = true
                    rootParams.height = usableHeightNow
                } else {
                    // keyboard probably just became hidden.
                    keyboardExpanded = false
                    rootParams.height = screenHeight
                }

                usableHeightPrevious = usableHeightNow
                root.setFitKeyboardExpanded(keyboardExpanded)
            }
        }

        private fun computeUsableHeight(): Int {
            val r = Rect()
            root.getWindowVisibleDisplayFrame(r)
            return r.bottom // - r.top --> Do not reduce the height of status bar.
        }

        companion object {
            // For more information, see https://issuetracker.google.com/issues/36911528
            // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
            fun assistActivity(activity: GeoActivity) {
                KeyboardResizeBugWorkaround(activity)
            }
        }

        init {
            root.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
            rootParams = root.layoutParams
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fitHorizontalSystemBarRootLayout =
            FitHorizontalSystemBarRootLayout(this)

        climasense.instance.addActivity(this)

        this.setLanguage(SettingsManager.getInstance(this).language.locale)

        window.setSystemBarStyle(
            false,
            !this.isDarkMode,
            true,
            !this.isDarkMode
        )
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // decor -> fit horizontal system bar -> decor child.
        val decorView = window.decorView as ViewGroup
        val decorChild = decorView.getChildAt(0) as ViewGroup

        decorView.removeView(decorChild)
        decorView.addView(fitHorizontalSystemBarRootLayout)

        fitHorizontalSystemBarRootLayout.removeAllViews()
        fitHorizontalSystemBarRootLayout.addView(decorChild)

        KeyboardResizeBugWorkaround.assistActivity(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        climasense.instance.setTopActivity(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        climasense.instance.setTopActivity(this)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        climasense.instance.setTopActivity(this)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        climasense.instance.checkToCleanTopActivity(this)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        climasense.instance.removeActivity(this)
    }

    open val snackbarContainer: SnackbarContainer
        get() = SnackbarContainer(
            this,
            findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup,
            true
        )

    fun provideSnackbarContainer(): SnackbarContainer = snackbarContainer

    val isActivityCreated: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
    val isActivityStarted: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    val isActivityResumed: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
}