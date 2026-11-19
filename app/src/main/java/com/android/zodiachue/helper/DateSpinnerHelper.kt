package com.android.zodiachue.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.android.zodiachue.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

object DateSpinnerHelper {
    fun setupBirthDateSpinners(
        context: Context,
        spinnerMonth: Spinner,
        spinnerDay: Spinner,
        spinnerYear: Spinner,
        initialBirthDate: String? = null,
        onDateChanged: (month: Int, day: Int, year: Int, zodiacSign: String) -> Unit
    ) {
        val months = (1..12).map { it.toString().padStart(2, '0') }
        spinnerMonth.adapter = createAdapter(context, months)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1900..currentYear).map { it.toString() }.reversed()
        spinnerYear.adapter = createAdapter(context, years)

        val parsed = initialBirthDate?.let { ZodiacCalculator.parseBirthDate(it) }
        val defaultMonth = parsed?.first ?: 1
        val defaultYear = parsed?.third ?: (currentYear - 20)
        val defaultDay = parsed?.second ?: 1

        spinnerMonth.setSelection((defaultMonth - 1).coerceIn(0, months.lastIndex))
        val yearIndex = years.indexOf(defaultYear.toString()).takeIf { it >= 0 } ?: 0
        spinnerYear.setSelection(yearIndex)

        fun refreshDays(preserveDay: Int) {
            val month = spinnerMonth.selectedItem.toString().toInt()
            val year = spinnerYear.selectedItem.toString().toInt()
            val maxDays = ZodiacCalculator.daysInMonth(month, year)
            val days = (1..maxDays).map { it.toString().padStart(2, '0') }
            spinnerDay.adapter = createAdapter(context, days)
            val targetDay = preserveDay.coerceIn(1, maxDays)
            spinnerDay.setSelection(targetDay - 1)
        }

        refreshDays(defaultDay)

        val notify = {
            val m = spinnerMonth.selectedItem.toString().toInt()
            val d = spinnerDay.selectedItem.toString().toInt()
            val y = spinnerYear.selectedItem.toString().toInt()
            val sign = ZodiacCalculator.getZodiacSign(m, d)
            onDateChanged(m, d, y, sign)
        }

        val monthOrYearListener = object : AdapterView.OnItemSelectedListener {
            var initialized = false
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!initialized) {
                    initialized = true
                    notify()
                    return
                }
                val currentSelectedDay = spinnerDay.selectedItem?.toString()?.toIntOrNull() ?: 1
                refreshDays(currentSelectedDay)
                notify()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val dayListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                notify()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerMonth.onItemSelectedListener = monthOrYearListener
        spinnerYear.onItemSelectedListener = monthOrYearListener
        spinnerDay.onItemSelectedListener = dayListener
    }

    private fun createAdapter(context: Context, items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, R.layout.spinner_item, items).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
    }
}

object ImagePreviewDialog {
    fun show(
        activity: Activity,
        resId: Int,
        positiveLabel: String,
        onPositiveClick: () -> Unit
    ) {
        val imageView = ImageView(activity).apply {
            setImageResource(resId)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(24, 24, 24, 8)
        }

        MaterialAlertDialogBuilder(activity, R.style.CustomAlertDialog)
            .setView(imageView)
            .setPositiveButton(positiveLabel) { _, _ -> onPositiveClick() }
            .setNegativeButton("Close", null)
            .show()
    }
}
