/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/* We supply SleepNight as the only item with the list of entities
* Everytime we chage the schema, we'll have to increase the version number
* With exportSchema to false, the database doesn't keep schema version history backups*/
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    /* The database needs to know about the DAO (Data Access Object)
    * We can have multiple DAOs*/
    abstract val sleepDatabaseDao: SleepDatabaseDao

    /* The companion object allows clients to access the methods for creating or getting the
    * database without instantiating the class. Since the only purpose of this class is to
    * purpose a database, there is no reason to ever instantiate it*/
    companion object {
        /* This variable will keep a reference to the database, when one has been created.
        * This helps to avoid repeatedly opening connections to the database,
        * which is computationally expensive */
        // The value of a volatile variable will never be cached, and all writes and reads will be
        // done to and from the main memory. This helps to make sure the value of INSTANCES is
        // always up-to-date and the same to all execution threads.
        @Volatile
        private var INSTANCE: SleepDatabase? = null
        fun getInstance(context: Context): SleepDatabase {
            // Only one thread at a time can enter this block
            synchronized(this) {
                var instance = INSTANCE
                // No database yet
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }
}
