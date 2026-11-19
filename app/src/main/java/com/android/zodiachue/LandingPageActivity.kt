package com.android.zodiachue

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.zodiachue.data.ZodiacData
import com.android.zodiachue.helper.BottomNavHelper
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.ImagePreviewDialog
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LandingPageActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var imageManager: ImageManager
    private var userZodiacSign: String = "Leo"
    private var activeSign: String = "Leo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        userRepository = UserRepository(this)
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
            return
        }

        imageManager = ImageManager(this, currentUser.username)
        userZodiacSign = currentUser.zodiacSign
        activeSign = userZodiacSign

        BottomNavHelper.setup(this, BottomNavHelper.Tab.HOME)
        setupHeaderActions()
        renderUserHeader()
        displayZodiacDetails(activeSign)
    }

    override fun onResume() {
        super.onResume()
        val currentUser = userRepository.getCurrentUser() ?: return
        val wasFollowingOwnSign = activeSign.equals(userZodiacSign, ignoreCase = true)
        userZodiacSign = currentUser.zodiacSign
        if (wasFollowingOwnSign) {
            activeSign = userZodiacSign
        }
        BottomNavHelper.setup(this, BottomNavHelper.Tab.HOME)
        renderUserHeader()
        displayZodiacDetails(activeSign)
    }

    @SuppressLint("SetTextI18n")
    private fun renderUserHeader() {
        val user = userRepository.getCurrentUser() ?: return
        val dateText = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.textview_today_date).text = dateText
        findViewById<TextView>(R.id.textview_hellouser).text = "Hello, ${user.firstName}"
    }

    private fun setupHeaderActions() {
        findViewById<View>(R.id.btn_switch_sign).setOnClickListener {
            showZodiacPickerBottomSheet()
        }
        findViewById<View>(R.id.banner_preview_sign).setOnClickListener {
            activeSign = userZodiacSign
            displayZodiacDetails(activeSign)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayZodiacDetails(zodiacSign: String) {
        val data = ZodiacProvider.getZodiacDataForSign(zodiacSign)
        val parsedColor = Color.parseColor(data.colorHex)

        findViewById<TextView>(R.id.textview_header_sign).text = "${data.emoji} ${data.sign} ▾"

        val previewBanner = findViewById<View>(R.id.banner_preview_sign)
        val isPreviewingOther = !data.sign.equals(userZodiacSign, ignoreCase = true)
        previewBanner.visibility = if (isPreviewingOther) View.VISIBLE else View.GONE
        if (isPreviewingOther) {
            findViewById<TextView>(R.id.textview_preview_banner).text =
                "Previewing ${data.sign} • Tap to return to $userZodiacSign"
        }

        (findViewById<View>(R.id.view_color_swatch).background?.mutate() as? GradientDrawable)
            ?.setColor(parsedColor)

        findViewById<TextView>(R.id.textview_color_name).apply {
            text = data.colorName
            setTextColor(parsedColor)
        }

        findViewById<TextView>(R.id.textview_sign_meta).text =
            "${data.sign} • ${data.dateRange} • ${data.element}"

        findViewById<TextView>(R.id.btn_copy_hex).apply {
            text = data.colorHex
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("ZodiacHue Color", data.colorHex))
                Toast.makeText(this@LandingPageActivity, "Copied ${data.colorHex}", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.imageview_main_color).setImageResource(data.mainImageRes)
        findViewById<TextView>(R.id.textview_color_description).text = data.overview
        findViewById<TextView>(R.id.textview_mood_influence).text = data.moodInfluence
        findViewById<TextView>(R.id.textview_aesthetic_wear).text = data.aestheticToWear
        findViewById<TextView>(R.id.textview_avoid_colors).text = data.avoid

        findViewById<View>(R.id.view_reading_accent).setBackgroundColor(parsedColor)
        findViewById<TextView>(R.id.textview_color_reading).text = data.reading

        findViewById<View>(R.id.btn_reflect_reading).setOnClickListener {
            val intent = Intent(this, DiaryPageActivity::class.java).apply {
                putExtra("prefill_title", "${data.sign} • ${data.colorName}")
                putExtra("prefill_content", "Today's hue is ${data.colorName} (${data.colorHex}).\n\n")
            }
            startActivity(intent)
        }

        setupOutfitCard(
            imageRes = data.galleryImages[0],
            imageView = findViewById(R.id.imageview_1),
            saveButton = findViewById(R.id.btn_save_look_1),
            saveIcon = findViewById(R.id.icon_save_look_1),
            saveLabel = findViewById(R.id.txt_save_look_1)
        )
        setupOutfitCard(
            imageRes = data.galleryImages[1],
            imageView = findViewById(R.id.imageview_2),
            saveButton = findViewById(R.id.btn_save_look_2),
            saveIcon = findViewById(R.id.icon_save_look_2),
            saveLabel = findViewById(R.id.txt_save_look_2)
        )
    }

    private fun setupOutfitCard(
        imageRes: Int,
        imageView: ImageView,
        saveButton: View,
        saveIcon: ImageView,
        saveLabel: TextView
    ) {
        imageView.setImageResource(imageRes)

        fun refreshSavedBadge() {
            val saved = imageManager.isImageSaved(imageRes)
            saveIcon.setImageResource(
                if (saved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
            )
            saveLabel.text = if (saved) "Saved" else "Save"
        }

        fun toggleSave() {
            if (imageManager.isImageSaved(imageRes)) {
                imageManager.removeImage(imageRes)
                Toast.makeText(this, "Removed from your profile moodboard", Toast.LENGTH_SHORT).show()
            } else {
                imageManager.addImage(imageRes)
                Toast.makeText(this, "Saved to your profile moodboard!", Toast.LENGTH_SHORT).show()
            }
            refreshSavedBadge()
        }

        refreshSavedBadge()
        saveButton.setOnClickListener { toggleSave() }

        imageView.setOnClickListener {
            val isSaved = imageManager.isImageSaved(imageRes)
            ImagePreviewDialog.show(
                activity = this,
                resId = imageRes,
                positiveLabel = if (isSaved) "Remove" else "Save to Profile"
            ) {
                toggleSave()
            }
        }

        imageView.setOnLongClickListener {
            toggleSave()
            true
        }
    }

    private fun showZodiacPickerBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.CosmicBottomSheetTheme)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_zodiac_picker, null)
        val recycler = sheetView.findViewById<RecyclerView>(R.id.recycler_zodiac_picker)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ZodiacPickerAdapter(ZodiacProvider.allZodiacData) { chosen ->
            activeSign = chosen.sign
            displayZodiacDetails(activeSign)
            dialog.dismiss()
        }
        dialog.setContentView(sheetView)
        dialog.show()
    }

    private inner class ZodiacPickerAdapter(
        private val signs: List<ZodiacData>,
        private val onSelect: (ZodiacData) -> Unit
    ) : RecyclerView.Adapter<ZodiacPickerAdapter.Holder>() {

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val emoji: TextView = view.findViewById(R.id.txt_picker_emoji)
            val sign: TextView = view.findViewById(R.id.txt_picker_sign)
            val yoursBadge: TextView = view.findViewById(R.id.txt_picker_yours_badge)
            val subtitle: TextView = view.findViewById(R.id.txt_picker_subtitle)
            val swatch: View = view.findViewById(R.id.view_picker_swatch)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_zodiac_picker, parent, false)
            return Holder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = signs[position]
            holder.emoji.text = item.emoji
            holder.sign.text = item.sign
            holder.yoursBadge.visibility =
                if (item.sign.equals(userZodiacSign, ignoreCase = true)) View.VISIBLE else View.GONE
            holder.subtitle.text = "${item.dateRange} • ${item.colorName}"
            (holder.swatch.background?.mutate() as? GradientDrawable)
                ?.setColor(Color.parseColor(item.colorHex))
            holder.itemView.setOnClickListener { onSelect(item) }
        }

        override fun getItemCount(): Int = signs.size
    }
}
