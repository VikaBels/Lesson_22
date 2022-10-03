package com.example.lesson20.tasks

import android.util.Log
import bolts.TaskCompletionSource
import com.example.lesson20.*
import com.example.lesson20.interfaces.ProfileService
import com.example.lesson20.models.ProfileRequestBody
import com.example.lesson20.models.ProfileResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileRepository {
    companion object {
        const val PROFILE_URL = "senla-training-addition/lesson-21.php?method=profile"
    }

    fun startTask(
        token: String
    ): TaskCompletionSource<ProfileResponseBody> {
        return sendRequestProfile(token)
    }

    private fun sendRequestProfile(token: String): TaskCompletionSource<ProfileResponseBody> {
        val retrofitProfile = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstanceProfile = retrofitProfile.create(ProfileService::class.java)

        val requestBody = ProfileRequestBody(
            token = token
        )

        return getCompletable(serviceInstanceProfile, requestBody)
    }

    private fun getCompletable(
        serviceInstance: ProfileService,
        requestBody: ProfileRequestBody
    ): TaskCompletionSource<ProfileResponseBody> {
        val completable = TaskCompletionSource<ProfileResponseBody>()

        serviceInstance
            .getProfileResponseBody(requestBody)
            .enqueue(object : Callback<ProfileResponseBody> {
                override fun onResponse(
                    call: Call<ProfileResponseBody>,
                    response: Response<ProfileResponseBody>
                ) {
                    val body = response.body()

                    if (body != null) {
                        Log.e("ProfileRepository", body.toString())
                    }
                    completable.setResult(body)
                }

                override fun onFailure(call: Call<ProfileResponseBody>, t: Throwable) {
                    completable.setError(t as Exception?)
                }
            })
        return completable
    }
}