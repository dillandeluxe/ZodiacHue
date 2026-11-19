package com.android.zodiachue.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.zodiachue.R

class RegisterHelper(context: Context) {
    private val userRepository = UserRepository(context)
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

        editTextPassword.post {
            editTextPassword.setSelection(editTextPassword.text.length)
            editTextConfirmPassword.setSelection(editTextConfirmPassword.text.length)
        }
    }

    fun createPasswordStrengthWatcher(
        passwordStrengthView: TextView,
        strengthBar: ProgressBar? = null
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val level = SecurityUtils.evaluatePasswordStrength(s?.toString().orEmpty())
                val color = Color.parseColor(level.colorHex)
                passwordStrengthView.text = level.label
                passwordStrengthView.setTextColor(color)
                strengthBar?.apply {
                    progress = level.progress
                    progressTintList = android.content.res.ColorStateList.valueOf(color)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    fun getZodiacSign(month: Int, day: Int): String {
        return ZodiacCalculator.getZodiacSign(month, day)
    }

    fun registerUser(
        firstName: String,
        lastName: String,
        zodiacSign: String,
        username: String,
        password: String,
        confirmPassword: String,
        birthDate: String
    ): String? {
        return userRepository.registerUser(
            firstName = firstName,
            lastName = lastName,
            zodiacSign = zodiacSign,
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            birthDate = birthDate
        )
    }
}
