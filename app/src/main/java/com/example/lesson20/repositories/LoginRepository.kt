package com.example.lesson20.repositories

import android.util.Log
import bolts.Task
import bolts.TaskCompletionSource
import com.example.lesson20.*
import com.example.lesson20.interfaces.LoginService
import com.example.lesson20.models.LoginRequestBody
import com.example.lesson20.models.LoginResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRepository {
    companion object {
        const val LOGIN_URL = "senla-training-addition/lesson-21.php?method=login"
    }

    fun getLogin(
        email: String,
        password: String
    ): Task<LoginResponseBody>? {
        return startRequestLogin(email, password).task
    }

    private fun startRequestLogin(
        email: String,
        password: String
    ): TaskCompletionSource<LoginResponseBody> {
        val retrofitLogin = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstanceLogin = retrofitLogin.create(LoginService::class.java)

        val requestBody = LoginRequestBody(
            email = email,
            password = password
        )
        return getCompletable(serviceInstanceLogin, requestBody)
    }

    private fun getCompletable(
        serviceInstance: LoginService,
        requestBody: LoginRequestBody
    ): TaskCompletionSource<LoginResponseBody> {
        val completable = TaskCompletionSource<LoginResponseBody>()

        serviceInstance
            .getLoginResponseBody(requestBody)
            .enqueue(object : Callback<LoginResponseBody> {
                override fun onResponse(
                    call: Call<LoginResponseBody>,
                    response: Response<LoginResponseBody>
                ) {
                    val body = response.body()

                    if (body != null) {
                        Log.e("LoginRepository", body.toString())
                    }
                    completable.setResult(body)
                }

                override fun onFailure(call: Call<LoginResponseBody>, t: Throwable) {
                    completable.setError(t as Exception?)
                }
            })
        return completable
    }
}



