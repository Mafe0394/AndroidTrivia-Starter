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
import androidx.lifecycle.*
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
    // Variable to get data from Database
    private var tonight = MutableLiveData<SleepNight>()
    private val nights = database.getAllNights()

    // When the Stop button is clicked,
    // this gets a value and activates an observer in the fragment
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality
    private val _showSnackbarEvent=MutableLiveData<Boolean>()
    val showSnackbarEvent:LiveData<Boolean>
        get()=_showSnackbarEvent

    // Transformations
    // use a function in utils to format a LiveData<List<SleepNight>> in a striing
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }
    val startButtonVisible=Transformations.map(tonight){currentNight->
        currentNight==null
    }
    val stopButtonVisible=Transformations.map(tonight){currentNight->
        currentNight!=null
    }
    val clearButtonVisible=Transformations.map(nights){nights->
        nights.isNotEmpty()
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    fun doneNavigating() {
        // resets the variable
        _navigateToSleepQuality.value = null
    }

    fun doneShowingSnackbar(){
        _showSnackbarEvent.value=false
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

    private suspend fun update(night: SleepNight) {
        database.update(night)
    }

    private suspend fun clear() {
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

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value=true
        }
    }
}

