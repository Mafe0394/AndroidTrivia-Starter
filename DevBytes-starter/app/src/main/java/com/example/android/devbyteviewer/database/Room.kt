/*
 * Copyright (C) 2019 Google Inc.
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

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao{

    /* Fetch all the videos from the database.
    * This method returns LiveData, so that the data displayed in the UI is refreshed
    * whenever the data in the database is changed*/
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    /* Insert a list of videos fetched from the network into the database.
    * Overwrite the database entry if the video is already present in the database,
    * to do this we use onConflict argument to OnConflictStrategy.REPLACE*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(video:List<DatabaseVideo>)

}

@Database(entities = [DatabaseVideo::class],version = 1)
abstract class VideosDatabase:RoomDatabase(){
    abstract val videoDao:VideoDao
}

private lateinit var INSTANCE: VideosDatabase

fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                VideosDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}