package com.tech.riri.androidApp.textList

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tech.riri.androidApp.MainCoroutineRule
import com.tech.riri.androidApp.data.FakeRepository
import com.tech.riri.androidApp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
class TextListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //class under test
    private lateinit var textListViewModel: TextListViewModel

    //fake repository class
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setUpTextListViewModel() {
        fakeRepository = FakeRepository()
        textListViewModel = TextListViewModel(fakeRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTextList_returnEmptyList() = mainCoroutineRule.runBlockingTest {
        textListViewModel.getTextList()
        val list = textListViewModel.list.getOrAwaitValue()
        assertThat(list, IsEqual(mutableListOf()))
    }

}