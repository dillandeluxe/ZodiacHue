package com.android.zodiachue

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.DateSpinnerHelper
import com.android.zodiachue.helper.RegisterHelper
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider

class RegisterPageActivity : AppCompatActivity() {
    private lateinit var helper: RegisterHelper
    private var selectedBirthDate: String = "1/1/2005"
    private var selectedZodiacSign: String = "Capricorn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        helper = RegisterHelper(this)
        setupUi()
    }

    private fun setupUi() {
        findViewById<ImageButton>(R.id.btn_back_register).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txt_already_have_account).setOnClickListener { finish() }

        findViewById<ScrollView>(R.id.scrollview_register).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
        }

        val editFirstName = findViewById<EditText>(R.id.edittext_firstname)
        val editLastName = findViewById<EditText>(R.id.edittext_lastname)
        val editUsername = findViewById<EditText>(R.id.edittext_username)
        val editPassword = findViewById<EditText>(R.id.edittext_password)
        val editConfirmPassword = findViewById<EditText>(R.id.edittext_confirm_password)
        val strengthText = findViewById<TextView>(R.id.password_strength_indicator)
        val strengthBar = findViewById<ProgressBar>(R.id.password_strength_bar)
        val togglePass1 = findViewById<ImageView>(R.id.imageview_toggle_password1)
        val togglePass2 = findViewById<ImageView>(R.id.imageview_toggle_password2)
        val buttonSignUp = findViewById<Button>(R.id.btn_signup)

        editPassword.addTextChangedListener(helper.createPasswordStrengthWatcher(strengthText, strengthBar))

        togglePass1.setOnClickListener {
            helper.togglePasswordVisibility(editPassword, editConfirmPassword, togglePass1, togglePass2)
        }
        togglePass2.setOnClickListener {
            helper.togglePasswordVisibility(editPassword, editConfirmPassword, togglePass1, togglePass2)
        }

        setupDateSpinners()

        buttonSignUp.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val error = helper.registerUser(
                firstName = editFirstName.text.toString(),
                lastName = editLastName.text.toString(),
                zodiacSign = selectedZodiacSign,
                username = username,
                password = password,
                confirmPassword = editConfirmPassword.text.toString(),
                birthDate = selectedBirthDate
            )

            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginPageActivity::class.java).apply {
                    putExtra("username", username)
                    putExtra("password", password)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupDateSpinners() {
        val spinnerMonth = findViewById<Spinner>(R.id.spinner_month)
        val spinnerDay = findViewById<Spinner>(R.id.spinner_day)
        val spinnerYear = findViewById<Spinner>(R.id.spinner_year)

        val emojiView = findViewById<TextView>(R.id.txt_register_zodiac_emoji)
        val nameView = findViewById<TextView>(R.id.txt_register_zodiac_name)
        val metaView = findViewById<TextView>(R.id.txt_register_zodiac_meta)
        val swatchView = findViewById<View>(R.id.view_register_zodiac_swatch)

        DateSpinnerHelper.setupBirthDateSpinners(
            context = this,
            spinnerMonth = spinnerMonth,
            spinnerDay = spinnerDay,
            spinnerYear = spinnerYear
        ) { month, day, year, sign ->
            selectedBirthDate = ZodiacCalculator.formatBirthDate(month, day, year)
            selectedZodiacSign = sign
            val data = ZodiacProvider.getZodiacDataForSign(sign)
            emojiView.text = data.emoji
            nameView.text = "${data.sign} • ${data.colorName}"
            metaView.text = "${data.dateRange} • ${data.element}"
            (swatchView.background?.mutate() as? GradientDrawable)?.setColor(
                Color.parseColor(data.colorHex)
            )
        }
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
