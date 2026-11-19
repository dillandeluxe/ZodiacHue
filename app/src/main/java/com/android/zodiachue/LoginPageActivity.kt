package com.android.zodiachue

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.zodiachue.data.UserData
import com.android.zodiachue.helper.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginPageActivity : Activity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val edittextUsername = findViewById<EditText>(R.id.edittext_username)
        val edittextPassword = findViewById<EditText>(R.id.edittext_password)
        val buttonLogin = findViewById<Button>(R.id.button_login)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)
        val checkboxRememberMe = findViewById<CheckBox>(R.id.checkbox_remember_me)
        val imageviewTogglePassword = findViewById<ImageView>(R.id.imageview_toggle_password)


        sharedPrefs = getSharedPreferences("ZodiacHuePrefs", Context.MODE_PRIVATE)
        gson = Gson()


        edittextPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        prefillLoginCredentials(edittextUsername, edittextPassword, checkboxRememberMe)

        setupPasswordVisibilityToggle(edittextPassword, imageviewTogglePassword)

        loadRememberedCredentials(edittextUsername, edittextPassword, checkboxRememberMe)

        buttonLogin.setOnClickListener {
            handleLoginButtonClick(edittextUsername, edittextPassword, checkboxRememberMe)
        }

        buttonSignup.setOnClickListener {
            navigateToSignUpPage()
        }
    }

    private fun prefillLoginCredentials(
        edittextUsername: EditText,
        edittextPassword: EditText,
        checkboxRememberMe: CheckBox
    ) {
        intent?.let {
            val registeredUsername = it.getStringExtra("username")
            val registeredPassword = it.getStringExtra("password")

            if (!registeredUsername.isNullOrEmpty() && !registeredPassword.isNullOrEmpty()) {
                edittextUsername.setText(registeredUsername)
                edittextPassword.setText(registeredPassword)
                checkboxRememberMe.isChecked = true
            }
        }
    }

    private fun setupPasswordVisibilityToggle(
        edittextPassword: EditText,
        imageviewTogglePassword: ImageView
    ) {
        var isPasswordVisible = false
        imageviewTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            edittextPassword.transformationMethod =
                if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
            imageviewTogglePassword.setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )
            edittextPassword.setSelection(edittextPassword.text.length)
        }
    }

    private fun loadRememberedCredentials(
        edittextUsername: EditText,
        edittextPassword: EditText,
        checkboxRememberMe: CheckBox
    ) {
        val rememberedUsername = sharedPrefs.getString("remembered_username", "")
        val rememberedPassword = sharedPrefs.getString("remembered_password", "")
        val isRemembered = sharedPrefs.getBoolean("remember_me", false)

        if (isRemembered) {
            edittextUsername.setText(rememberedUsername)
            edittextPassword.setText(rememberedPassword)
            checkboxRememberMe.isChecked = true
        }
    }

    private fun handleLoginButtonClick(
        edittextUsername: EditText,
        edittextPassword: EditText,
        checkboxRememberMe: CheckBox
    ) {
        val username = edittextUsername.text.toString().trim()
        val password = edittextPassword.text.toString().trim()
        val rememberMe = checkboxRememberMe.isChecked

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both username and password", Toast.LENGTH_LONG).show()
            return
        }

        val usersMap = getUsersMapFromSharedPrefs()

        val savedUser = usersMap[username]
        if (savedUser == null) {
            Toast.makeText(this, "Account doesn't exist. Please sign up first.", Toast.LENGTH_LONG).show()
            return
        }

        if (password == savedUser.password) {
            saveLoginPreferences(rememberMe, username, password)
            UserSession.userData = savedUser

            navigateToLandingPage(username)
        } else {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUsersMapFromSharedPrefs(): MutableMap<String, UserData> {
        val usersJson = sharedPrefs.getString("users", null)
        return if (usersJson != null) {
            gson.fromJson(usersJson, object : TypeToken<MutableMap<String, UserData>>() {}.type)
        } else {
            mutableMapOf()
        }
    }

    private fun saveLoginPreferences(rememberMe: Boolean, username: String, password: String) {
        with(sharedPrefs.edit()) {
            if (rememberMe) {
                putBoolean("remember_me", true)
                putString("remembered_username", username)
                putString("remembered_password", password)
            } else {
                remove("remember_me")
                remove("remembered_username")
                remove("remembered_password")
            }
            apply()
        }
    }

    private fun navigateToLandingPage(username: String) {
        val intent = Intent(this, LandingPageActivity::class.java)
        intent.putExtra("username", username)
        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show()
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    private fun navigateToSignUpPage() {
        val intent = Intent(this, RegisterPageActivity::class.java)
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
