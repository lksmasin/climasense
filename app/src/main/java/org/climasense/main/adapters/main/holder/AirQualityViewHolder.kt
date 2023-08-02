package org.climasense.main.adapters.main.holder

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.index.PollutantIndex
import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.common.extensions.getFormattedTime
import org.climasense.common.extensions.is12Hour
import org.climasense.common.ui.widgets.ArcProgress
import org.climasense.main.adapters.AqiAdapter
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.theme.ThemeManager
import org.climasense.theme.compose.climasenseTheme
import org.climasense.theme.compose.DayNightTheme
import org.climasense.theme.resource.providers.ResourceProvider
import org.climasense.theme.weatherView.WeatherViewController

class AirQualityViewHolder(parent: ViewGroup) : AbstractMainCardViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.container_main_aqi, parent, false)
) {
    private val mTitle: TextView = itemView.findViewById(R.id.container_main_aqi_title)
    private val mTime: TextView = itemView.findViewById(R.id.container_main_aqi_time)
    private val mProgress: ArcProgress = itemView.findViewById(R.id.container_main_aqi_progress)
    private val mRecyclerView: RecyclerView = itemView.findViewById(R.id.container_main_aqi_recyclerView)
    private val mDialog: ComposeView = itemView.findViewById(R.id.container_main_aqi_dialog)
    private var mAdapter: AqiAdapter? = null
    private var mAqiIndex = 0
    private var mEnable = false
    private var mAttachAnimatorSet: AnimatorSet? = null

    private val _dialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dialogState = _dialogState.asStateFlow()

    @SuppressLint("DefaultLocale")
    override fun onBindView(
        activity: GeoActivity, location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean, firstCard: Boolean
    ) {
        super.onBindView(
            activity, location, provider,
            listAnimationEnabled, itemAnimationEnabled, firstCard
        )

        val isDaily = (location.weather?.current?.airQuality == null || !location.weather.current.airQuality.isValid)
        location.weather!!.validAirQuality?.let { airQuality ->
            mAqiIndex = airQuality.getIndex() ?: 0
            mEnable = true
            mTitle.setTextColor(
                ThemeManager.getInstance(context)
                    .weatherThemeDelegate
                    .getThemeColors(
                        context,
                        WeatherViewController.getWeatherKind(location.weather),
                        location.isDaylight
                    )[0]
            )
            mTime.text = if (isDaily) {
                if (location.weather.dailyForecast[0].isToday(location.timeZone)) {
                    context.getString(R.string.short_today)
                } else location.weather.dailyForecast[0].getWeek(context, location.timeZone)
            } else location.weather.base.updateDate.getFormattedTime(location.timeZone, context.is12Hour)
            if (itemAnimationEnabled) {
                mProgress.apply {
                    progress = 0f
                    setText(String.format("%d", 0))
                    setProgressColor(ContextCompat.getColor(context, R.color.colorLevel_1), MainThemeColorProvider.isLightTheme(context, location))
                    setArcBackgroundColor(MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOutline))
                }
            } else {
                val aqiColor = airQuality.getColor(mProgress.context)
                mProgress.apply {
                    progress = mAqiIndex.toFloat()
                    setText(String.format("%d", mAqiIndex))
                    setProgressColor(aqiColor, MainThemeColorProvider.isLightTheme(context, location))
                    setArcBackgroundColor(ColorUtils.setAlphaComponent(aqiColor, (255 * 0.1).toInt()))
                }
            }
            mProgress.apply {
                setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
                setBottomText(airQuality.getName(context))
                setBottomTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))
                contentDescription = mAqiIndex.toString() + ", " + airQuality.getName(context)
                max = PollutantIndex.indexExcessivePollution.toFloat()
            }
            mDialog.setContent {
                climasenseTheme(lightTheme = !isSystemInDarkTheme()) {
                    AirQualityDialogView(airQuality)
                }
            }
            itemView.setOnClickListener {
                _dialogState.value = true
            }
        }
        mAdapter = AqiAdapter(context, location, itemAnimationEnabled)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
    }


    @Composable
    private fun AirQualityDialogView(airQuality: AirQuality) {
        val dialogOpenState by dialogState.collectAsState()

        if (dialogOpenState) {
            AlertDialog(
                onDismissRequest = { _dialogState.value = false },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.air_quality_index),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.little_margin)))
                        Text(
                            text = mAqiIndex.toString(),
                            color = Color(airQuality.getColor(mProgress.context)),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = airQuality.getName(context) ?: "",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = airQuality.getDescription(context) ?: "",
                            color = DayNightTheme.colors.bodyColor
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            _dialogState.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.action_close),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            )
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onEnterScreen() {
        if (itemAnimationEnabled && mEnable) {
            mLocation!!.weather!!.validAirQuality?.let { airQuality ->
                val aqiColor = airQuality.getColor(mProgress.context)
                val progressColor = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    ContextCompat.getColor(context, R.color.colorLevel_1),
                    aqiColor
                )
                progressColor.addUpdateListener { animation: ValueAnimator ->
                    mProgress.setProgressColor(animation.animatedValue as Int, MainThemeColorProvider.isLightTheme(context, mLocation!!))
                }
                val backgroundColor = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    MainThemeColorProvider.getColor(mLocation!!.isDaylight, com.google.android.material.R.attr.colorOutline),
                    ColorUtils.setAlphaComponent(aqiColor, (255 * 0.1).toInt())
                )
                backgroundColor.addUpdateListener { animation: ValueAnimator -> mProgress.setArcBackgroundColor((animation.animatedValue as Int)) }
                val aqiNumber = ValueAnimator.ofObject(FloatEvaluator(), 0, mAqiIndex)
                aqiNumber.addUpdateListener { animation: ValueAnimator ->
                    mProgress.apply {
                        progress = (animation.animatedValue as Float)
                        setText(String.format("%d", mProgress.progress.toInt()))
                    }
                }
                mAttachAnimatorSet = AnimatorSet().apply {
                    playTogether(progressColor, backgroundColor, aqiNumber)
                    interpolator = DecelerateInterpolator()
                    duration = (1500 + mAqiIndex / 400f * 1500).toLong()
                    start()
                }
                mAdapter!!.executeAnimation()
            }
        }
    }

    override fun onRecycleView() {
        super.onRecycleView()
        mAttachAnimatorSet?.let {
            if (it.isRunning) it.cancel()
        }
        mAttachAnimatorSet = null
        mAdapter?.cancelAnimation()
    }
}