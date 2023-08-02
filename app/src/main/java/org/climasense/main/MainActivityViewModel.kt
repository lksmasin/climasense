package org.climasense.main

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.climasense.common.basic.GeoViewModel
import org.climasense.common.basic.livedata.BusLiveData
import org.climasense.common.basic.livedata.EqualtableLiveData
import org.climasense.common.basic.models.Location
import org.climasense.common.extensions.hasPermission
import org.climasense.main.utils.RequestErrorType
import org.climasense.main.utils.StatementManager
import org.climasense.settings.SettingsManager
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainActivityRepository,
    val statementManager: StatementManager,
) : GeoViewModel(application),
    MainActivityRepository.WeatherRequestCallback {

    // live data.
    val currentLocation = EqualtableLiveData<DayNightLocation?>()
    private val _validLocationList = MutableStateFlow<Pair<List<Location>, String?>>(Pair(emptyList(), null))
    val validLocationList = _validLocationList.asStateFlow()
    private val _totalLocationList = MutableStateFlow<Pair<List<Location>, String?>>(Pair(emptyList(), null))
    val totalLocationList = _totalLocationList.asStateFlow()

    private val _dialogChooseCurrentLocationWeatherSourceOpen = MutableStateFlow(false)
    val dialogChooseCurrentLocationWeatherSourceOpen = _dialogChooseCurrentLocationWeatherSourceOpen.asStateFlow()

    val loading = EqualtableLiveData<Boolean>()
    val indicator = EqualtableLiveData<Indicator>()

    val locationPermissionsRequest = MutableLiveData<PermissionsRequest?>()
    val requestErrorType = BusLiveData<RequestErrorType?>(Handler(Looper.getMainLooper()))

    // inner data.

    private var initCompleted = false
    private var updating = false

    companion object {
        private const val KEY_FORMATTED_ID = "formatted_id"
    }

    // life cycle.

    override fun onCleared() {
        super.onCleared()
    }

    fun init(formattedId: String? = null) {
        onCleared()

        var id = formattedId ?: savedStateHandle[KEY_FORMATTED_ID]

        // init live data.
        val totalList = repository.initLocations(formattedId = id)
        val validList = Location.excludeInvalidResidentLocation(getApplication(), totalList)

        id = formattedId ?: validList.getOrNull(0)?.formattedId
        val current = validList.firstOrNull { item -> item.formattedId == id }

        initCompleted = false

        current?.let {
            currentLocation.setValue(DayNightLocation(location = it))
        }
        _validLocationList.value = Pair(validList, id)
        _totalLocationList.value = Pair(totalList, id)

        loading.setValue(false)
        indicator.setValue(
            Indicator(
                total = validList.size,
                index = validList.indexOfFirst { it.formattedId == id }
            )
        )

        locationPermissionsRequest.value = null
        requestErrorType.setValue(null)

        // read weather caches.
        repository.getWeatherCacheForLocations(
            oldList = totalList,
            ignoredFormattedId = id,
        ) { newList, _ ->
            initCompleted = true
            if (newList.isNotEmpty()) { updateInnerData(newList) }
        }
    }

    // update inner data.
    private fun updateInnerData(location: Location) {
        val total = ArrayList(totalLocationList.value.first)
        for (i in total.indices) {
            if (total[i].formattedId == location.formattedId) {
                total[i] = location
                break
            }
        }

        updateInnerData(total)
    }

    fun openChooseCurrentLocationWeatherSourceDialog() {
        _dialogChooseCurrentLocationWeatherSourceOpen.value = true
    }

    fun closeChooseCurrentLocationWeatherSourceDialog() {
        _dialogChooseCurrentLocationWeatherSourceOpen.value = false
    }

    private fun updateInnerData(total: List<Location>) {
        // get valid locations and current index.
        val valid = Location.excludeInvalidResidentLocation(
            getApplication(),
            total,
        )

        var index = 0
        for (i in valid.indices) {
            if (valid[i].formattedId == currentLocation.value?.location?.formattedId) {
                index = i
                break
            }
        }

        indicator.postValue(Indicator(total = valid.size, index = index))

        // update current location.
        setCurrentLocation(valid[index])

        // check difference in valid locations.
        val diffInValidLocations = validLocationList.value.first != valid
        if (diffInValidLocations || validLocationList.value.second != valid[index].formattedId) {
            _validLocationList.value = Pair(valid, valid[index].formattedId)
        }

        // update total locations.
        _totalLocationList.value = Pair(total, valid[index].formattedId)
    }

    private fun setCurrentLocation(location: Location) {
        currentLocation.postValue(DayNightLocation(location = location))
        savedStateHandle[KEY_FORMATTED_ID] = location.formattedId

        checkToUpdateCurrentLocation()
    }

    /**
     * Called on background thread
     */
    private fun onUpdateResult(
        location: Location,
        requestErrorTypeResult: RequestErrorType?
    ) {
        if (requestErrorTypeResult != null) {
            requestErrorType.postValue(requestErrorTypeResult)
        }

        updateInnerData(location)

        loading.postValue(false)
        updating = false
    }

    private fun checkToUpdateCurrentLocation() {
        // is not loading
        if (!updating) {
            // if already valid, just return.
            if (currentLocationIsValid()) return

            // If we don't have any location yet, just return
            if (currentLocation.value?.location == null) {
                updating = false
                return
            }

            // if is not valid, we need:
            // update if init completed.
            // otherwise, mark a loading state and wait the init progress complete.
            if (initCompleted) {
                updateWithUpdatingChecking(
                    triggeredByUser = false,
                    checkPermissions = true,
                )
            } else {
                loading.postValue(true)
                updating = false
            }
            return
        }

        // is loading, do nothing.
    }

    private fun currentLocationIsValid() =
        currentLocation.value?.location?.weather?.isValid(
            SettingsManager
                .getInstance(getApplication())
                .updateInterval
                .intervalInHour
        ) ?: false

    // update.
    fun updateWithUpdatingChecking(
        triggeredByUser: Boolean,
        checkPermissions: Boolean,
    ) {
        if (updating) return
        val locationToCheck = currentLocation.value?.location
        if (locationToCheck == null) {
            loading.postValue(true)
            loading.postValue(false)
            return
        }

        loading.postValue(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || !checkPermissions) {
            updating = true
            viewModelScope.launch {
                repository.getWeather(
                    getApplication(),
                    locationToCheck,
                    locationToCheck.isCurrentPosition,
                    this@MainActivityViewModel
                )
            }
            return
        }

        // check permissions.
        val locationPermissionList: MutableList<String> = mutableListOf()
        if (locationToCheck.isCurrentPosition) {
            locationPermissionList.addAll(repository
                .getLocatePermissionList(getApplication())
                .filter { !(getApplication() as Application).hasPermission(it) }
                .toMutableList())
        }
        if (locationPermissionList.isEmpty()) {
            // already got all permissions -> request data directly.
            updating = true
            viewModelScope.launch(Dispatchers.IO) {
                repository.getWeather(
                    getApplication(),
                    locationToCheck,
                    locationToCheck.isCurrentPosition,
                    this@MainActivityViewModel
                )
            }
        } else {
            updating = false
            locationPermissionsRequest.value = PermissionsRequest(
                locationPermissionList,
                locationToCheck,
                triggeredByUser
            )
        }
    }

    fun cancelRequest() {
        updating = false
        loading.postValue(false)
    }

    fun checkToUpdate() {
        checkToUpdateCurrentLocation()
    }

    fun updateLocationFromBackground(location: Location) {
        if (!initCompleted) {
            return
        }

        if (currentLocation.value?.location?.formattedId == location.formattedId) {
            cancelRequest()
        }
        updateInnerData(location)
    }

    // set location.

    fun setLocation(index: Int) {
        validLocationList.value.first.let {
            setLocation(it[index].formattedId)
        }
    }

    fun setLocation(formattedId: String) {
        cancelRequest()

        validLocationList.value.first.let { locationList ->
            val index = locationList.indexOfFirst { it.formattedId == formattedId }

            if (index >= 0) {
                setCurrentLocation(locationList[index])

                indicator.setValue(Indicator(total = locationList.size, index = index))

                _totalLocationList.value = Pair(totalLocationList.value.first, formattedId)
                _validLocationList.value = Pair(validLocationList.value.first, formattedId)
            }
        }
    }

    // return true if current location changed.
    fun offsetLocation(offset: Int): Boolean {
        cancelRequest()

        val oldFormattedId = currentLocation.value?.location?.formattedId ?: ""

        // ensure current index.
        var index = 0
        validLocationList.value.first.let {
            for (i in it.indices) {
                if (it[i].formattedId == currentLocation.value?.location?.formattedId) {
                    index = i
                    break
                }
            }
        }

        // update index.
        index = (index + offset + (validLocationList.value.first.size)) % (validLocationList.value.first.size)

        // update location.
        setCurrentLocation(validLocationList.value.first[index])

        indicator.postValue(
            Indicator(total = validLocationList.value.first.size, index = index)
        )

        _totalLocationList.value = Pair(totalLocationList.value.first, currentLocation.value?.location?.formattedId ?: "")
        _validLocationList.value = Pair(validLocationList.value.first, currentLocation.value?.location?.formattedId ?: "")

        return currentLocation.value?.location?.formattedId != oldFormattedId
    }

    // list.

    // return false if failed.
    fun addLocation(
        location: Location,
        index: Int? = null,
    ): Boolean {
        // do not add an existing location.
        if (totalLocationList.value.first.firstOrNull {
            it.formattedId == location.formattedId
        } != null) {
            return false
        }

        val total = ArrayList(totalLocationList.value.first)
        total.add(index ?: total.size, location)

        updateInnerData(total)
        repository.writeLocationList(locationList = total)

        return true
    }

    fun swapLocations(from: Int, to: Int) {
        /*val fromItem = _totalLocationList.value.first[from]
        val toItem = _totalLocationList.value.first[to]
        val newList = _totalLocationList.value.first.toMutableList()
        newList[from] = toItem
        newList[to] = fromItem

        _totalLocationList.value = Pair(newList, _totalLocationList.value.second)*/
        if (from == to) {
            return
        }

        val total = ArrayList(totalLocationList.value.first)
        total.add(to, total.removeAt(from))

        updateInnerData(total)

        repository.writeLocationList(
            locationList = totalLocationList.value.first
        )
    }

    fun updateLocation(location: Location) {
        updateInnerData(location)
        repository.writeLocationList(
            locationList = totalLocationList.value.first,
        )
    }

    fun deleteLocation(position: Int): Location {
        val total = ArrayList(totalLocationList.value.first)
        val location = total.removeAt(position)

        updateInnerData(total)
        repository.deleteLocation(location = location)

        return location
    }

    // MARK: - getter.
    fun getValidLocation(offset: Int?): Location? {
        if (offset == null) return null
        // ensure current index.
        var index: Int? = null
        validLocationList.value.first.let {
            for (i in it.indices) {
                if (it[i].formattedId == currentLocation.value?.location?.formattedId) {
                    index = i
                    break
                }
            }
        }

        return index?.let {
            return validLocationList.value.first.getOrNull((it + offset + validLocationList.value.first.size) % validLocationList.value.first.size)
        }
    }

    // impl.

    override fun onCompleted(location: Location, requestErrorType: RequestErrorType?) {
        onUpdateResult(location, requestErrorType)
    }
}