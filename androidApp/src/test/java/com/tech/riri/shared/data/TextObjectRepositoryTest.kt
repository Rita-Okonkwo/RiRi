package com.tech.riri.shared.data

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tech.riri.androidApp.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextObjectRepositoryTest {
    private lateinit var localDataSource: FakeDataSource
    private lateinit var remoteDataSource: FakeDataSource
    private val database = mutableMapOf<Long, String>()

    //class under test
    private lateinit var repository: TextObjectRepository

    @Before
    fun createRepository() {
        localDataSource = FakeDataSource(database)
        remoteDataSource = FakeDataSource(database)
        repository = TextObjectRepository(remoteDataSource, localDataSource)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addText_emptyString_returnEmptyMap() = runBlockingTest {
        val text = ""
        repository.addText(text)
        assertThat(database, `is`((emptyMap())))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addText_nonEmptyString_returnFilledMap() = runBlockingTest {
        val text = "hello"
        repository.addText(text)
        assertThat(database, `is`(not(((emptyMap())))))
    }

    @Test
    fun deleteText() {
        //to be done
    }

    @Test
    fun getTexts() {
        //to be done
    }

    @Test
    fun getResponse() {
        //to be done
    }
}