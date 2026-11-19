package com.android.zodiachue

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.UserRepository

class LoginPageActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var checkboxRememberMe: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        userRepository = UserRepository(this)

        editUsername = findViewById(R.id.edittext_username)
        editPassword = findViewById(R.id.edittext_password)
        val buttonLogin = findViewById<Button>(R.id.button_login)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)
        checkboxRememberMe = findViewById(R.id.checkbox_remember_me)
        val togglePassword = findViewById<ImageView>(R.id.imageview_toggle_password)

        editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        setupPasswordToggle(editPassword, togglePassword)
        prefillCredentials(intent)

        val submitLogin = {
            performLogin(editUsername, editPassword, checkboxRememberMe)
        }

        buttonLogin.setOnClickListener { submitLogin() }
        editPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitLogin()
                true
            } else {
                false
            }
        }

        buttonSignup.setOnClickListener {
            startActivity(Intent(this, RegisterPageActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        prefillCredentials(intent)
    }

    private fun prefillCredentials(targetIntent: Intent?) {
        val registeredUsername = targetIntent?.getStringExtra("username")
        val registeredPassword = targetIntent?.getStringExtra("password")
        if (!registeredUsername.isNullOrBlank()) {
            editUsername.setText(registeredUsername)
            if (!registeredPassword.isNullOrBlank()) {
                editPassword.setText(registeredPassword)
            }
            checkboxRememberMe.isChecked = true
            return
        }

        val remembered = userRepository.getRememberedUsername()
        if (remembered.isNotBlank()) {
            editUsername.setText(remembered)
            checkboxRememberMe.isChecked = userRepository.isRememberMeEnabled()
        }
    }

    private fun setupPasswordToggle(editPassword: EditText, toggleView: ImageView) {
        var visible = false
        toggleView.setOnClickListener {
            visible = !visible
            editPassword.transformationMethod =
                if (visible) null else PasswordTransformationMethod.getInstance()
            toggleView.setImageResource(
                if (visible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )
            editPassword.setSelection(editPassword.text.length)
        }
    }

    private fun performLogin(
        editUsername: EditText,
        editPassword: EditText,
        checkboxRememberMe: CheckBox
    ) {
        val username = editUsername.text.toString()
        val password = editPassword.text.toString()
        val rememberMe = checkboxRememberMe.isChecked

        when (val result = userRepository.login(username, password, rememberMe)) {
            is UserRepository.LoginResult.Success -> {
                Toast.makeText(this, "Welcome back, ${result.user.firstName}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LandingPageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            is UserRepository.LoginResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
