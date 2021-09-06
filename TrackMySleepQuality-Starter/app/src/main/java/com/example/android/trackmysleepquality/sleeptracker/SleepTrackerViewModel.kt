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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
/* AndroidViewModel is the same as ViewModel, but it takes the application context as a constructor
* parameter and makes it available as a property */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
    private var tonight = MutableLiveData<SleepNight>()
    private val nights=database.getAllNights()
    val nightsString=Transformations.map(nights){nights->
        formatNights(nights,application.resources)
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    // Async functions
    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night = database.getTonight()
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }
        return night
    }

    private suspend fun insert(newNight: SleepNight) {
        // Room uses Dispatchers.IO
        database.insert(newNight)
    }
    private suspend fun update(night: SleepNight){
        database.update(night)
    }
    private suspend fun clear(){
        database.clear()
    }

    // Click handlers
    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }
    fun onStopTracking(){
        viewModelScope.launch {
            val oldNight=tonight.value?:return@launch
            oldNight.endTimeMilli=System.currentTimeMillis()
            update(oldNight)
        }
    }
    fun onClear(){
        viewModelScope.launch {
            clear()
            tonight.value=null
        }
    }
}

