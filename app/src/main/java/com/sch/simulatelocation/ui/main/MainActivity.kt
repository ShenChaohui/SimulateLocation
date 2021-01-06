package com.sch.simulatelocation.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder.zzq.smartshow.dialog.DialogBtnClickListener
import com.coder.zzq.smartshow.dialog.EnsureDialog
import com.coder.zzq.smartshow.dialog.SmartDialog
import com.coder.zzq.smartshow.toast.SmartToast
import com.sch.simulatelocation.R
import com.sch.simulatelocation.adapter.HistoryLocationAdapter
import com.sch.simulatelocation.entrys.HistoryLocation
import com.sch.simulatelocation.ui.selectLocation.SelectLocationActivity
import com.sch.simulatelocation.utils.MapHelperUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var locationManager: LocationManager? = null
    private val historyLocationAdapter by lazy { HistoryLocationAdapter() }
    private lateinit var mainViewModel: MainViewModel
    private val errorDialog by lazy {
        EnsureDialog().title("启用模拟位置")
            .message(
                "请依次前往：\n" +
                        "设置 - 开发者模式 - 选择模拟位置信息应用，设置\"Mock Location\"为模拟位置应用\n" +
                        "*若设置中没有开发者选项，请先开启开发者选项，开启方式自行百度搜索：手机品牌+开启开发者选项"
            ).cancelBtn("取消")
            .confirmBtn("前往设置", object : DialogBtnClickListener<SmartDialog<*>> {
                override fun onBtnClick(p0: SmartDialog<*>?, p1: Int, p2: Any?) {
                    startDevelopmentActivity()
                    p0?.dismiss()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                MainViewModel::class.java
            )

        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = historyLocationAdapter
        mainViewModel.historyLocationListLiveData?.observe(this) {
            historyLocationAdapter.setList(it)
            if (!it.isNullOrEmpty()) {
                mainViewModel.showLocation.value = it[0]
            }
        }
        mainViewModel.showLocation.observe(this) {
            tvAddress.text = it.formatted_address
            tvCity.text = "${it.country}·${it.province}"
            tvLocation.text = it.location
        }
        mainViewModel.isRun.observe(this) {
            if (it) {
                MainScope().launch(Dispatchers.Default) {
                    while (mainViewModel.isRun.value!!) {
                        delay(100)
                        locationManager?.setTestProviderLocation(
                            LocationManager.GPS_PROVIDER,
                            mainViewModel.getCoustenerLocation()
                        )
                    }
                }
                btn.text = "停止模拟"
            } else {
                btn.text = "开始模拟"
            }
        }
        fab.setOnClickListener {
            startActivity(Intent(this, SelectLocationActivity::class.java))
        }
        btn.setOnClickListener {
            if (mainViewModel.isRun.value!!) {
                mainViewModel.isRun.value = false
            } else {
                if (mainViewModel.showLocation.value == null) {
                    SmartToast.error("选择一个位置")
                    return@setOnClickListener
                }
                initLocation()
            }
        }
        historyLocationAdapter.setOnItemClickListener { adapter, view, position ->
            mainViewModel.showLocation.value = adapter.getItem(position) as HistoryLocation?
        }
        historyLocationAdapter.setOnItemLongClickListener { adapter, view, position ->
            EnsureDialog().message("是否删除该项？")
                .cancelBtn("取消")
                .confirmBtn("删除", object : DialogBtnClickListener<SmartDialog<*>> {
                    override fun onBtnClick(p0: SmartDialog<*>?, p1: Int, p2: Any?) {
                        mainViewModel.deleteLocation(adapter.getItem(position) as HistoryLocation)
                        p0?.dismiss()
                    }
                }).showInActivity(this)
            true
        }
    }

    private fun initLocation() {
        val enableAdb =
            (Settings.Secure.getInt(contentResolver, Settings.Secure.ADB_ENABLED, 0) > 0)
        if (!enableAdb) {
            SmartToast.error("请先打开开发者模式")
            return
        }
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false, true, false, false,
                true, true, true, 1, 1
            )
            locationManager?.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)

            mainViewModel.isRun.value = true
            SmartToast.success("模拟成功")
        } catch (e: Exception) {
            mainViewModel.isRun.value = false
            errorDialog.showInActivity(this)
        }
    }

    var lastTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - this.lastTime > 2000L) {
            SmartToast.show("再按一次退出程序")
            this.lastTime = System.currentTimeMillis()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.isRun.value = false
        locationManager?.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false)
        locationManager?.removeTestProvider(LocationManager.GPS_PROVIDER)
        locationManager = null
    }

    /**
     * 打开开发者模式界面
     */
    private fun startDevelopmentActivity() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val componentName = ComponentName(
                    "com.android.settings",
                    "com.android.settings.DevelopmentSettings"
                )
                val intent = Intent()
                intent.component = componentName
                intent.action = "android.intent.action.View"
                startActivity(intent)
            } catch (e1: Exception) {
                try {
                    val intent =
                        Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS") //部分小米手机采用这种方式跳转
                    startActivity(intent)
                } catch (e2: Exception) {
                }
            }
        }
    }


}