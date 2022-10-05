package com.example.lesson20.interfaces

import com.example.lesson20.models.LoginRequestBody
import com.example.lesson20.models.LoginResponseBody
import com.example.lesson20.repositories.LoginRepository.Companion.LOGIN_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST(LOGIN_URL)
    fun getLoginResponseBody(@Body loginRequestBody: LoginRequestBody): Call<LoginResponseBody>
}