package com.sch.simulatelocation.entrys

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
@Entity
class HistoryLocation {
    @PrimaryKey
    @NonNull
    var id: Long
    var location: String = ""
    var country: String = ""
    var province: String = ""
    var formatted_address: String = ""
    init {
        id = System.currentTimeMillis()
    }

}