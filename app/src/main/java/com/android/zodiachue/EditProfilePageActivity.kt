package com.android.zodiachue

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.DateSpinnerHelper
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider

class EditProfilePageActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var zodiacPreviewText: TextView

    private var currentUsername: String = ""
    private var selectedBirthDate: String = ""
    private var selectedZodiacSign: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        userRepository = UserRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            finish()
            return
        }

        currentUsername = user.username
        selectedBirthDate = user.birthDate
        selectedZodiacSign = user.zodiacSign

        initViews(user.firstName, user.lastName, user.birthDate)
        setupSaveButton()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(firstName: String, lastName: String, birthDate: String) {
        findViewById<ImageButton>(R.id.btn_back_edit_profile).setOnClickListener { finish() }

        firstNameEditText = findViewById(R.id.edit_firstName)
        lastNameEditText = findViewById(R.id.edit_lastName)
        zodiacPreviewText = findViewById(R.id.txt_edit_zodiac_preview)

        firstNameEditText.setText(firstName)
        lastNameEditText.setText(lastName)

        DateSpinnerHelper.setupBirthDateSpinners(
            context = this,
            spinnerMonth = findViewById(R.id.spinner_edit_month),
            spinnerDay = findViewById(R.id.spinner_edit_day),
            spinnerYear = findViewById(R.id.spinner_edit_year),
            initialBirthDate = birthDate
        ) { month, day, year, sign ->
            selectedBirthDate = ZodiacCalculator.formatBirthDate(month, day, year)
            selectedZodiacSign = sign
            val data = ZodiacProvider.getZodiacDataForSign(sign)
            zodiacPreviewText.text = "Zodiac Sign: ${data.emoji} ${data.sign} (${data.colorName})"
        }
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.btn_saveProfile).setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "First name and last name are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = userRepository.updateProfile(
                username = currentUsername,
                firstName = firstName,
                lastName = lastName,
                birthDate = selectedBirthDate,
                zodiacSign = selectedZodiacSign
            )

            if (updated) {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Could not update profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
