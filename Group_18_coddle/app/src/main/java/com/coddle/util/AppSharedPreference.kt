package com.coddle.util

import android.content.Context
import android.content.SharedPreferences
import com.coddle.util.Constants.EMAIL
import com.coddle.util.Constants.IS_LOGIN
import com.coddle.util.Constants.PET_BREED
import com.coddle.util.Constants.PET_DOB
import com.coddle.util.Constants.PET_IMAGE
import com.coddle.util.Constants.PET_NAME
import com.coddle.util.Constants.PET_TYPE
import com.coddle.util.Constants.PREFERENCE_MODE
import com.coddle.util.Constants.PREFERENCE_NAME
import com.coddle.util.Constants.USERNAME
import com.coddle.util.Constants.USER_ID

class AppSharedPreference {
    companion object {

        private fun Context.getSharedPreference(): SharedPreferences {
            return getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE)
        }

        fun Context.isLogin(): Boolean {
            return getSharedPreference().getBoolean(IS_LOGIN, false)
        }

        fun Context.login(
            email: String,
            username:String,
            user_id: String,
            pet_name: String,
            pet_breed: String,
            pet_dob: String,
            pet_image: String,
            pet_type:String
        ) {
            getSharedPreference().edit().apply {
                putBoolean(IS_LOGIN, true)
                putString(USERNAME,username)
                putString(EMAIL, email)
                putString(USER_ID, user_id)
                putString(PET_BREED, pet_breed)
                putString(PET_DOB, pet_dob)
                putString(PET_IMAGE, pet_image)
                putString(PET_NAME, pet_name)
                putString(PET_TYPE,pet_type)
            }.apply()
        }

        fun Context.getImage(): String {
            return getSharedPreference().getString(PET_IMAGE,"").toString()
        }

        fun Context.getPetName(): String {
            return getSharedPreference().getString(PET_NAME,"").toString()
        }

        fun Context.getPetBreed(): String {
            return getSharedPreference().getString(PET_BREED,"").toString()
        }

        fun Context.getPetType(): String {
            return getSharedPreference().getString(PET_TYPE,"").toString()
        }

        fun Context.getPetDob(): String {
            return getSharedPreference().getString(PET_DOB,"").toString()
        }

        fun Context.getUserId(): String {
            return getSharedPreference().getString(USER_ID,"").toString()
        }

        fun Context.getUsername(): String {
            return getSharedPreference().getString(USERNAME,"").toString()
        }

        fun Context.updateUser(
                username:String,
                pet_name:String,
                pet_breed: String,
                pet_type: String,
                pet_dob: String
        ){
            getSharedPreference().edit().apply {
                putString(USERNAME,username)
                putString(PET_TYPE,pet_type)
                putString(PET_NAME,pet_name)
                putString(PET_BREED,pet_breed)
                putString(PET_DOB,pet_dob)
            }.apply()
        }

    }
}