package com.example.lesson20.interfaces

import com.example.lesson20.models.ProfileRequestBody
import com.example.lesson20.models.ProfileResponseBody
import com.example.lesson20.repositories.ProfileRepository.Companion.PROFILE_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileService {

    @POST(PROFILE_URL)
    fun getProfileResponseBody(@Body profileRequestBody: ProfileRequestBody): Call<ProfileResponseBody>
}