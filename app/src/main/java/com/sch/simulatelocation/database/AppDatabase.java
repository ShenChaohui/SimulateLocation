package com.sch.simulatelocation.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sch.simulatelocation.entrys.HistoryLocation;
import com.zydl.cq.dirtdispose.database.HistoryLocationDao;

/**
 * Created by Sch.
 * Date: 2020/9/23
 * description:
 */
@Database(entities = {HistoryLocation.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "my_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract HistoryLocationDao getHistoryLocationDao();
}
