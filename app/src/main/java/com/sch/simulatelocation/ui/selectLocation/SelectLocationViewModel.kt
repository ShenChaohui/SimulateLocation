package com.sch.simulatelocation.ui.selectLocation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.maps.model.LatLng
import com.coder.zzq.smartshow.snackbar.SmartSnackbar
import com.sch.simulatelocation.database.AppDatabase
import com.sch.simulatelocation.entrys.HistoryLocation
import com.sch.simulatelocation.network.NetWorkManager
import com.zydl.cq.dirtdispose.database.HistoryLocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
class SelectLocationViewModel(application: Application) : AndroidViewModel(application) {
    private var appDatabase: AppDatabase = AppDatabase.getInstance(getApplication())
    private var historyLocationDao: HistoryLocationDao
    val historyLocationLiveData by lazy { MutableLiveData<HistoryLocation>() }

    init {
        historyLocationDao = appDatabase.historyLocationDao
    }

    fun getLocationInfo(latLng: LatLng) {
        viewModelScope.launch {
            val result = NetWorkManager.apiService.getLocationInfo(
                "5024ea2b7cae39bb0cf249ca805ce041",
                "${latLng.longitude},${latLng.latitude}"
            )
            if (result.status == "1") {
                val historyLocation = HistoryLocation()
                historyLocation.location = "${latLng.longitude},${latLng.latitude}"
                historyLocation.country = result.regeocode.addressComponent.country
                historyLocation.province = result.regeocode.addressComponent.province
                historyLocation.formatted_address = result.regeocode.formatted_address
                historyLocationLiveData.value = historyLocation
            }
        }
    }

    fun saveLocation() {
        viewModelScope.launch(Dispatchers.Default) {
            historyLocationLiveData.value?.let { historyLocationDao.insert(it) }
        }
    }

}