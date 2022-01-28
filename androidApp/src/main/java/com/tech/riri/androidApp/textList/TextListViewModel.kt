package com.tech.riri.androidApp.textList

import android.app.Application
import androidx.lifecycle.*
import com.tech.riri.androidApp.uploadImage.UploadImageViewModel
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextListViewModel(private val textObjectRepository: TextObjectRepository) : ViewModel() {

    private val _list = MutableLiveData<List<TextObjectDataModel>>()
    val list: LiveData<List<TextObjectDataModel>>
        get() = _list

    fun getTextList() {
        viewModelScope.launch {
            _list.postValue(textObjectRepository.getTexts())
        }
    }

    fun deleteText(textObjectDataModel: TextObjectDataModel) {
        viewModelScope.launch {
            textObjectRepository.deleteText(textObjectDataModel.id)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class TextListViewModelFactory (
    private val textObjectRepository: TextObjectRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (TextListViewModel(textObjectRepository) as T)
}