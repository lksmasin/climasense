package org.climasense.search

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.climasense.climasense
import org.climasense.common.basic.GeoViewModel
import org.climasense.common.basic.models.Location
import org.climasense.common.utils.helpers.SnackbarHelper
import org.climasense.main.utils.RequestErrorType
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application?,
    repository: SearchActivityRepository
) : GeoViewModel(application!!) {
    private val _listResource = MutableStateFlow<Pair<List<Location>, LoadableLocationStatus>>(Pair(emptyList(), LoadableLocationStatus.SUCCESS))
    val listResource = _listResource.asStateFlow()
    private val _enabledSource: MutableStateFlow<String> = MutableStateFlow(repository.lastSelectedWeatherSource)
    val enabledSource = _enabledSource.asStateFlow()
    private val mRepository: SearchActivityRepository = repository

    fun requestLocationList(str: String) {
        mRepository.cancel()
        mRepository.searchLocationList(
            getApplication(),
            str,
            enabledSource.value
        ) { result: Pair<List<Location>?, RequestErrorType?>?, _: Boolean ->
            result?.second?.let { msg ->
                msg.showDialogAction?.let { showDialogAction ->
                    SnackbarHelper.showSnackbar(
                        content = (getApplication() as Application).getString(msg.shortMessage),
                        action = (getApplication() as Application).getString(msg.actionButtonMessage)
                    ) {
                        climasense.instance.topActivity?.let { topActivity ->
                            showDialogAction(topActivity)
                        }
                    }
                } ?: SnackbarHelper.showSnackbar((getApplication() as Application).getString(msg.shortMessage))
                _listResource.value = Pair(emptyList(), LoadableLocationStatus.ERROR)
            } ?: run {
                result?.first?.let {
                    _listResource.value = Pair(it, LoadableLocationStatus.SUCCESS)
                } ?: {
                    _listResource.value = Pair(emptyList(), LoadableLocationStatus.ERROR)
                }
            }
        }
        _listResource.value = Pair(emptyList(), LoadableLocationStatus.LOADING)
    }

    fun setEnabledSource(weatherSource: String) {
        mRepository.lastSelectedWeatherSource = weatherSource
        _enabledSource.value = weatherSource
    }

    override fun onCleared() {
        super.onCleared()
        mRepository.cancel()
    }
}
