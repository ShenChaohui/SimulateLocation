package com.sch.simulatelocation

import android.app.Application
import android.content.Context
import com.coder.zzq.smartshow.core.SmartShow

/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        baseApplication = this
        SmartShow.init(this)
    }

    companion object {
        private var baseApplication: Application? = null
        fun getContext(): Context {
            return baseApplication!!
        }
    }
}