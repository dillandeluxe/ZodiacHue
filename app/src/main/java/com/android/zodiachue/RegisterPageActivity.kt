package com.android.zodiachue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.zodiachue.helper.RegisterHelper

class RegisterPageActivity : Activity() {
    private lateinit var helper: RegisterHelper
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerDay: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var textviewZodiacSign: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        window.setBackgroundDrawableResource(R.drawable.background)
        helper = RegisterHelper(this)

        initializeUIElements()
    }

    private fun initializeUIElements() {
        val edittextFirstName = findViewById<EditText>(R.id.edittext_firstname)
        val edittextLastName = findViewById<EditText>(R.id.edittext_lastname)
        textviewZodiacSign = findViewById(R.id.edittext_zodiac_sign)
        val edittextUsername = findViewById<EditText>(R.id.edittext_username)
        val edittextPassword = findViewById<EditText>(R.id.edittext_password)
        val edittextConfirmPassword = findViewById<EditText>(R.id.edittext_confirm_password)
        val passwordStrengthView = findViewById<TextView>(R.id.password_strength_indicator)
        val buttonSignUp = findViewById<Button>(R.id.btn_signup)

        edittextPassword.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        edittextConfirmPassword.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        setUpScrollView()
        setUpPasswordVisibilityToggle(edittextPassword, edittextConfirmPassword)
        setUpDateSpinners()
        edittextPassword.addTextChangedListener(helper.createPasswordStrengthWatcher(passwordStrengthView))

        buttonSignUp.setOnClickListener {
            val month = spinnerMonth.selectedItem.toString().toInt()
            val day = spinnerDay.selectedItem.toString().toInt()
            val year = spinnerYear.selectedItem.toString()

            val zodiacSign = textviewZodiacSign.text.toString()
            val birthDate = "$month/$day/$year"
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()

            val result = helper.registerUser(
                context = this,
                firstName = edittextFirstName.text.toString(),
                lastName = edittextLastName.text.toString(),
                zodiacSign = zodiacSign,
                username = username,
                password = password,
                confirmPassword = edittextConfirmPassword.text.toString(),
                birthDate = birthDate
            )

            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginPageActivity::class.java).apply {
                    putExtra("username", username)
                    putExtra("password", password)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setUpScrollView() {
        findViewById<ScrollView>(R.id.scrollview_register).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
        }
    }

    private fun setUpPasswordVisibilityToggle(edittextPassword: EditText, edittextConfirmPassword: EditText) {
        val imageviewTogglePassword1 = findViewById<ImageView>(R.id.imageview_toggle_password1)
        val imageviewTogglePassword2 = findViewById<ImageView>(R.id.imageview_toggle_password2)

        imageviewTogglePassword1.setOnClickListener {
            helper.togglePasswordVisibility(edittextPassword, edittextConfirmPassword, imageviewTogglePassword1, imageviewTogglePassword2)
        }

        imageviewTogglePassword2.setOnClickListener {
            helper.togglePasswordVisibility(edittextPassword, edittextConfirmPassword, imageviewTogglePassword1, imageviewTogglePassword2)
        }
    }

    private fun setUpDateSpinners() {
        spinnerMonth = findViewById(R.id.spinner_month)
        spinnerDay = findViewById(R.id.spinner_day)
        spinnerYear = findViewById(R.id.spinner_year)

        val months = (1..12).map { it.toString().padStart(2, '0') }
        spinnerMonth.adapter = ArrayAdapter(this, R.layout.spinner_item, months).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        val days = (1..31).map { it.toString().padStart(2, '0') }
        spinnerDay.adapter = ArrayAdapter(this, R.layout.spinner_item, days).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val years = (1900..currentYear).map { it.toString() }.reversed()
        spinnerYear.adapter = ArrayAdapter(this, R.layout.spinner_item, years).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        val dateChangeListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateZodiacSign()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerMonth.onItemSelectedListener = dateChangeListener
        spinnerDay.onItemSelectedListener = dateChangeListener
        spinnerYear.onItemSelectedListener = dateChangeListener
    }

    private fun updateZodiacSign() {
        val month = spinnerMonth.selectedItem.toString().toInt()
        val day = spinnerDay.selectedItem.toString().toInt()
        val zodiacSign = helper.getZodiacSign(month, day)
        textviewZodiacSign.setText(zodiacSign)
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