package com.example.mobile.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.LoginRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository):ViewModel() {
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("Error", ": ${throwable.message}")
        }

    fun loginSubmission():Boolean{
        var res = ""
        viewModelScope.launch(coroutineExceptionHandler) {

            res = repository.loginSubmission()

        }
        return res=="OK"
   }
}