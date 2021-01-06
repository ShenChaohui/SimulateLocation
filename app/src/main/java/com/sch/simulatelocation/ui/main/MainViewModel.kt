package com.sch.simulatelocation.ui.main

import android.app.Application
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sch.simulatelocation.database.AppDatabase
import com.sch.simulatelocation.entrys.HistoryLocation
import com.sch.simulatelocation.utils.MapHelperUtil
import com.zydl.cq.dirtdispose.database.HistoryLocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var appDatabase: AppDatabase = AppDatabase.getInstance(getApplication())
    private var historyLocationDao: HistoryLocationDao
    val historyLocationListLiveData: LiveData<List<HistoryLocation>>?
    val showLocation by lazy { MutableLiveData<HistoryLocation>() }
    val isRun by lazy { MutableLiveData<Boolean>() }

    init {
        isRun.value = false
        historyLocationDao = appDatabase.historyLocationDao
        historyLocationListLiveData = historyLocationDao.queryAll()
    }

    fun getCoustenerLocation(): Location {
        val wgs84Point = MapHelperUtil.gcj02_To_wgs84(
            showLocation.value!!.location.split(",")[0].toDouble(),
            showLocation.value!!.location.split(",")[1].toDouble()
        )
        val loc = Location("gps")
        loc.accuracy = (Math.random() * 15.0 + 5).toFloat()
        loc.altitude = 33.5
        loc.bearing = 1.0f
        val bundle = Bundle()
        bundle.putInt("satellites", 7)
        loc.extras = bundle
        loc.longitude = wgs84Point[0]
        loc.latitude = wgs84Point[1]
        loc.time = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= 17) {
            loc.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
        return loc
    }


    fun deleteLocation(historyLocation: HistoryLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            historyLocationDao.delete(historyLocation)
        }
    }
}