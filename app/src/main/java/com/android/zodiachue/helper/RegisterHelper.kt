package com.android.zodiachue.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.core.content.ContextCompat
import com.android.zodiachue.R
import com.android.zodiachue.data.UserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterHelper(context: Context) {
    companion object {
        private const val PREFS_NAME = "ZodiacHuePrefs"
        private const val USERS_KEY = "users"
    }

    private var isPasswordVisible = false

    private val eyeOpen: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_eye_open)
    private val eyeClosed: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_eye_closed)

    fun togglePasswordVisibility(
        editTextPassword: EditText,
        editTextConfirmPassword: EditText,
        imageView1: ImageView,
        imageView2: ImageView
    ) {
        isPasswordVisible = !isPasswordVisible
        val method = if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()

        editTextPassword.transformationMethod = method
        editTextConfirmPassword.transformationMethod = method

        imageView1.setImageDrawable(if (isPasswordVisible) eyeOpen else eyeClosed)
        imageView2.setImageDrawable(if (isPasswordVisible) eyeOpen else eyeClosed)

        editTextPassword.postDelayed({
            editTextPassword.setSelection(editTextPassword.text.length)
            editTextConfirmPassword.setSelection(editTextConfirmPassword.text.length)
        }, 16)
    }

    fun createPasswordStrengthWatcher(passwordStrengthView: TextView): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val (label, color, _) = evaluatePasswordStrength(s.toString())
                passwordStrengthView.text = label
                passwordStrengthView.setTextColor(color)
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun evaluatePasswordStrength(password: String): Triple<String, Int, Boolean> {
        val lengthOK = password.length >= 8
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpec = password.any { !it.isLetterOrDigit() }

        return when {
            !lengthOK -> Triple("Weak", Color.RED, false)
            lengthOK && hasLower && hasDigit && hasUpper && hasSpec -> Triple("Strong", Color.GREEN, true)
            lengthOK && hasLower && hasDigit && hasUpper -> Triple("Moderate", Color.parseColor("#FFA500"), true)
            else -> Triple("Weak", Color.RED, false)
        }
    }

    fun getZodiacSign(month: Int, day: Int): String {
        return when (month) {
            1 -> if (day < 20) "Capricorn" else "Aquarius"
            2 -> if (day < 19) "Aquarius" else "Pisces"
            3 -> if (day < 21) "Pisces" else "Aries"
            4 -> if (day < 20) "Aries" else "Taurus"
            5 -> if (day < 21) "Taurus" else "Gemini"
            6 -> if (day < 21) "Gemini" else "Cancer"
            7 -> if (day < 23) "Cancer" else "Leo"
            8 -> if (day < 23) "Leo" else "Virgo"
            9 -> if (day < 23) "Virgo" else "Libra"
            10 -> if (day < 23) "Libra" else "Scorpio"
            11 -> if (day < 22) "Scorpio" else "Sagittarius"
            12 -> if (day < 22) "Sagittarius" else "Capricorn"
            else -> ""
        }
    }

    fun registerUser(
        context: Context,
        firstName: String,
        lastName: String,
        zodiacSign: String,
        username: String,
        password: String,
        confirmPassword: String,
        birthDate: String
    ): String? {
        if (firstName.isBlank() || lastName.isBlank() || zodiacSign.isBlank()
            || username.isBlank() || password.isBlank() || confirmPassword.isBlank()
        ) {
            return "Please fill all fields"
        }

        if (password != confirmPassword) {
            return "Passwords do not match"
        }

        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val usersJson = sharedPrefs.getString(USERS_KEY, null)

        val usersMap: MutableMap<String, UserData> = if (usersJson != null) {
            gson.fromJson(usersJson, object : TypeToken<MutableMap<String, UserData>>() {}.type)
        } else {
            mutableMapOf()
        }

        if (usersMap.containsKey(username)) {
            return "Username already exists"
        }

        val newUser = UserData(firstName, lastName, birthDate, zodiacSign, username, password)
        usersMap[username] = newUser

        val updatedJson = gson.toJson(usersMap)
        sharedPrefs.edit().putString(USERS_KEY, updatedJson).apply()

        return null
    }
}
