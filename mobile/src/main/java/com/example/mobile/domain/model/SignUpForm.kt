package com.example.mobile.domain.model

import java.time.LocalDate


data class SignUpForm(val id: String,
val password: String,
val name: String,
val birthDate: LocalDate)
