package com.gne.clothing.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gne.clothing.vo.Cloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Cloth::class), version = 1, exportSchema = false)
abstract class ClothDatabase : RoomDatabase() {
    abstract fun ClothDao():ClothDao

    private class ClothDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
//                    var clothDao = database.ClothDao()
                }
            }
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: ClothDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClothDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClothDatabase::class.java,
                    "cloth_database"
                ).addCallback(ClothDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}