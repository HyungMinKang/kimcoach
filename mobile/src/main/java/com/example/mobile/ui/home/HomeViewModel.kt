package com.example.mobile.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.HomeRepository
import com.example.mobile.ui.common.FormDataUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val repository: HomeRepository):ViewModel() {

    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("Error", ": ${throwable.message}")
        }

    fun sendCsvToServer(file:File){
        viewModelScope.launch(coroutineExceptionHandler) {
            val part = FormDataUtil.getCsvMultipart("file", file)
            println(repository.uploadFile(part))
        }
    }
}