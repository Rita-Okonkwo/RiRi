package com.tech.riri.shared.data

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tech.riri.androidApp.MainCoroutineRule
import com.tech.riri.androidApp.data.FakeDataSource
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.AnalyzeResult
import com.tech.riri.shared.entity.Image
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
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

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        localDataSource = FakeDataSource(database)
        remoteDataSource = FakeDataSource(database)
        repository = TextObjectRepository(mainCoroutineRule.dispatcher, remoteDataSource, localDataSource)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addText_emptyString_returnEmptyMap() = mainCoroutineRule.runBlockingTest {
        val text = ""
        repository.addText(text)
        assertThat(database, `is`((emptyMap())))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addText_nonEmptyString_returnFilledMap() = mainCoroutineRule.runBlockingTest {
        val text = "hello"
        repository.addText(text)
        assertThat(database, `is`(not(((emptyMap())))))
    }

    @Test
    fun deleteText_validId_returnNull() = mainCoroutineRule.runBlockingTest {
        repository.addText("hello")
        repository.deleteText(1)
        assertThat(database[1], IsEqual(null))
    }

    @Test
    fun getTexts_returnList() = mainCoroutineRule.runBlockingTest {
        database[1] = "hello"
        database[2] = "rita"
        val list = mutableListOf<TextObjectDataModel>()
        list.add(TextObjectDataModel("hello", 1))
        list.add(TextObjectDataModel("rita", 2))
        val dataList = repository.getTexts()
        assertThat(dataList, IsEqual(list))
    }

    @Test
    fun getResponse_emptyParameter_returnEmptyImage() = mainCoroutineRule.runBlockingTest {
        val response = Image("", "", "", null)
        val actualResponse = repository.getResponse("", "", "")
        assertThat(response, IsEqual(actualResponse))
    }

    @Test
    fun getResponse_filledParameter_returnImage() = mainCoroutineRule.runBlockingTest {
        val response = Image("200", "10", "20", AnalyzeResult(mutableListOf(), "1"))
        val actualResponse = repository.getResponse("test", "test", "test")
        assertThat(response, IsEqual(actualResponse))
    }
}