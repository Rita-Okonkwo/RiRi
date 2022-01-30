package com.tech.riri.androidApp.uploadImage

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tech.riri.androidApp.MainCoroutineRule
import com.tech.riri.androidApp.data.FakeRepository
import com.tech.riri.androidApp.getOrAwaitValue
import com.tech.riri.shared.data.TextObjectRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UploadImageViewModelTest {

    private lateinit var uploadImageViewModel: UploadImageViewModel
    private lateinit var textObjectRepository: TextObjectRepositoryInterface

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        textObjectRepository = FakeRepository()
        uploadImageViewModel = UploadImageViewModel(textObjectRepository, Dispatchers.Main)
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Test
    fun uploadImageUrl_urlString_returnSucceeded() = mainCoroutineRule.runBlockingTest {
        //given a fresh view model and image string

        val urlString = "https://firebasestorage.googleapis.com/v0/b/riri-2c18f.appspot.com/o/uploads%2F1f7c4b62-db87-4af1-8098-2f5318d2868f?alt=media&token=63c01983-5838-4da7-ab60-3efdaa73801a"

        //when image url is uploaded
        uploadImageViewModel.uploadImgUrl(urlString)
        //then the image status event is triggered
        val value = uploadImageViewModel.imageStatus.getOrAwaitValue()
        assertThat(value, `is`("succeeded"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun uploadImageUrl_urlString_returnNull() = mainCoroutineRule.runBlockingTest {
        //given a fresh view model and image string

        val urlString = ""

        //when image url is uploaded
        uploadImageViewModel.uploadImgUrl(urlString)
        //then the image status event is triggered
        val value = uploadImageViewModel.imageStatus.getOrAwaitValue()
        assertThat(value, nullValue())
    }
}