package com.example.labworks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.NotifDao

// Export schema left at true, should be needed later down the line
@Database(entities = [
    Notif::class,
    NotifComponent::class
], version = 1, exportSchema = false)
abstract class NotifDatabase : RoomDatabase() {

    abstract fun notifDao() : NotifDao

    companion object{
        @Volatile
        private var INSTANCE : NotifDatabase? = null

        fun getDatabase(context: Context): NotifDatabase{
            val tempInstance = INSTANCE

            if (tempInstance != null){
                return tempInstance
            }
            else{
                synchronized(this){
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotifDatabase::class.java,
                        "notif_database"
                    ).build()

                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}