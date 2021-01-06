package com.zydl.cq.dirtdispose.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sch.simulatelocation.entrys.HistoryLocation

/**
 * Created by Sch.
 * Date: 2020/11/27
 * description:
 */
@Dao
interface HistoryLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyLocation: HistoryLocation)

    @Query("SELECT * FROM HistoryLocation ORDER BY id DESC")
    fun queryAll(): LiveData<List<HistoryLocation>>?
    @Delete()
    fun delete(historyLocation: HistoryLocation)
}