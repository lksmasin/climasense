package org.climasense.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import org.climasense.common.basic.models.Location
import org.climasense.common.exceptions.ApiKeyMissingException
import org.climasense.common.exceptions.LocationSearchException
import org.climasense.common.exceptions.NoNetworkException
import org.climasense.common.exceptions.ParsingException
import org.climasense.common.rxjava.ObserverContainer
import org.climasense.common.rxjava.SchedulerTransformer
import org.climasense.main.utils.RequestErrorType
import org.climasense.settings.ConfigStore
import org.climasense.sources.WeatherHelper
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

class SearchActivityRepository @Inject internal constructor(
    @ApplicationContext context: Context,
    private val mWeatherHelper: WeatherHelper,
    private val mCompositeDisposable: CompositeDisposable
) {
    private val mConfig: ConfigStore = ConfigStore(context, PREFERENCE_SEARCH_CONFIG)

    fun searchLocationList(
        context: Context, query: String, enabledSource: String,
        callback: (t: Pair<List<Location>?, RequestErrorType?>?, done: Boolean) -> Unit
    ) {
        mWeatherHelper
            .requestSearchLocations(context, query, enabledSource)
            .compose(SchedulerTransformer.create())
            .subscribe(ObserverContainer(mCompositeDisposable, object : DisposableObserver<List<Location>>() {
                override fun onNext(t: List<Location>) {
                    callback(Pair<List<Location>, RequestErrorType?>(t, null), true)
                }

                override fun onError(e: Throwable) {
                    val requestErrorType = when (e) {
                        is NoNetworkException -> RequestErrorType.NETWORK_UNAVAILABLE
                        is HttpException -> {
                            when (e.code()) {
                                401, 403 -> RequestErrorType.API_UNAUTHORIZED
                                409, 429 -> RequestErrorType.API_LIMIT_REACHED
                                else -> {
                                    e.printStackTrace()
                                    RequestErrorType.LOCATION_SEARCH_FAILED
                                }
                            }
                        }
                        is SocketTimeoutException -> RequestErrorType.SERVER_TIMEOUT
                        is ApiKeyMissingException -> RequestErrorType.API_KEY_REQUIRED_MISSING
                        is MissingFieldException, is SerializationException, is ParsingException -> {
                            e.printStackTrace()
                            RequestErrorType.PARSING_ERROR
                        }
                        is LocationSearchException -> RequestErrorType.LOCATION_SEARCH_FAILED
                        else -> {
                            e.printStackTrace()
                            RequestErrorType.LOCATION_SEARCH_FAILED
                        }
                    }
                    callback(
                        Pair<List<Location>, RequestErrorType?>(emptyList(), requestErrorType),
                        true
                    )
                }

                override fun onComplete() {
                    // do nothing.
                }
            }))
    }

    var lastSelectedWeatherSource: String
        set(value) {
            mConfig.edit().putString(KEY_LAST_DEFAULT_SOURCE, value).apply()
        }
        get() = mConfig.getString(KEY_LAST_DEFAULT_SOURCE, "accu") ?: ""

    fun cancel() {
        mCompositeDisposable.clear()
    }

    companion object {
        private const val PREFERENCE_SEARCH_CONFIG = "SEARCH_CONFIG"
        private const val KEY_LAST_DEFAULT_SOURCE = "LAST_DEFAULT_SOURCE"
    }
}
