package com.example.mobile.ui.common

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FormDataUtil {
    fun getCsvMultipart(key: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = key,
            filename = file.name,
            body = file.asRequestBody("text/plain".toMediaType())
        )
    }
}