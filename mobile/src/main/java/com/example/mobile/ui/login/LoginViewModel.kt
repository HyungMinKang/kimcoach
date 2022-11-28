package com.example.mobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.LoginRepository
import com.example.mobile.domain.model.LogInForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository):ViewModel() {
    private val _logInFlag = MutableStateFlow<Boolean>(false)
    val logInFlag= _logInFlag.asStateFlow()

    fun loginSubmission(form: LogInForm){
        viewModelScope.launch {
           _logInFlag.value = repository.loginSubmission()

        }

   }
}