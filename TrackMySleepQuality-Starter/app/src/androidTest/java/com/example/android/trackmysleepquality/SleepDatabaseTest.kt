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

package com.example.android.trackmysleepquality

import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        // destroy de database instance
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNightLastNight() {

        // creating a generic SleepNight Objetc
        val night = SleepNight(sleepQuality = -5)

        // Inserting a night object into the Database
        sleepDao.insert(night)

        // Getting the most recent night, should be the same as night we inserted before
        val tonight = sleepDao.getTonight()

        // Comparing the tonight variable wih night, if is the same, it pass the test
        assertEquals(tonight?.sleepQuality, -5)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetNight() {

        // creating a generic SleepNight Objetc
        val night = SleepNight()

        // Inserting a night object into the Database
        sleepDao.insert(night)

        // Getting the most recent night, should be the same as night we inserted before
        val tonight = sleepDao.getTonight()

        // Creating a modified version of tonight with the same nightID but different parameters
        val tonightModified = SleepNight(
            nightId = tonight?.nightId ?: 0L,
            sleepQuality = -8
        )

        // Updating the parameters in an existing element of the table
        sleepDao.update(tonightModified)

        // Getting an specific element from the table with the key
        val updatedNight = sleepDao.get(key = tonight?.nightId ?: 0)

        // Comparing the tonight variable wih night, if is the same, it pass the test
        assertEquals(updatedNight?.sleepQuality, -8)
    }

    @Test
    @Throws(Exception::class)
    fun getAllList() {// TODO

        // Creating a 4 element list of SleepNights
        val night = SleepNight()
        // Inserting the list in the database
        sleepDao.insert(night)

        val nightList=sleepDao.getAll()

        assertEquals(1,1)


    }
}