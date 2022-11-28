package com.example.mobile.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.SignUpRepository
import com.example.mobile.domain.model.SignUpForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: SignUpRepository):ViewModel() {

    private val _signUpFlag = MutableStateFlow<Boolean>(false)
    val signUpFlag = _signUpFlag.asStateFlow()
    fun requestSignUp(form: SignUpForm){
        viewModelScope.launch {
            _signUpFlag.value = repository.requestSignUp(form)
        }
    }
}