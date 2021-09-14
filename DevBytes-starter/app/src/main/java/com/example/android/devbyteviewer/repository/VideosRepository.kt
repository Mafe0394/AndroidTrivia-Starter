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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** [database] is a [VideosDatabase] object that works as the class's constructor
 * parameter to access the Dao methods
 * */
class VideosRepository(private val database:VideosDatabase){

    // Transformations.map uses a conversion function to convert one LiveData object into
    // another LiveData object. it only is calculated when an activity or fragment
    // is observing the returned LiveData property
    val videos:LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()){
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    // Refresh the offline cache.
    suspend fun refreshVideos(){
        withContext(Dispatchers.IO){
            // Fetch from the network
            val playlist=DevByteNetwork.devbytes.getPlaylist()
            // Store the playlist in the Room Database
            database.videoDao.insertAll(playlist.asDatabaseModel())
        }
    }
}