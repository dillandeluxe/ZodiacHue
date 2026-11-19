package com.android.zodiachue

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.zodiachue.helper.BottomNavHelper
import com.android.zodiachue.helper.DiaryRepository
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.ImagePreviewDialog
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var diaryRepository: DiaryRepository
    private lateinit var imageManager: ImageManager
    private var currentUsername: String = ""

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some providers do not grant persistable permissions; still load for active session.
        }
        imageManager.saveProfileImageUri(uri)
        findViewById<ShapeableImageView>(R.id.profile_image)?.let {
            imageManager.loadProfileImage(this, it)
        }
        Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        userRepository = UserRepository(this)
        diaryRepository = DiaryRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
            return
        }

        currentUsername = user.username
        imageManager = ImageManager(this, currentUsername)

        BottomNavHelper.setup(this, BottomNavHelper.Tab.PROFILE)
        setupActions()
        refreshAllProfileData()
    }

    override fun onResume() {
        super.onResume()
        val user = userRepository.getCurrentUser() ?: return
        currentUsername = user.username
        imageManager = ImageManager(this, currentUsername)
        BottomNavHelper.setup(this, BottomNavHelper.Tab.PROFILE)
        refreshAllProfileData()
    }

    private fun setupActions() {
        findViewById<View>(R.id.layout_avatar_picker).setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        val openEdit = {
            startActivity(Intent(this, EditProfilePageActivity::class.java))
        }
        findViewById<Button>(R.id.btn_editprofile).setOnClickListener { openEdit() }
        findViewById<ImageButton>(R.id.btn_edit_profile_top).setOnClickListener { openEdit() }
    }

    private fun refreshAllProfileData() {
        refreshUserData()
        displayNoteCount()
        loadAvatarAndGallery()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUserData() {
        val user = userRepository.getCurrentUser() ?: return
        val zodiac = ZodiacProvider.getZodiacDataForSign(user.zodiacSign)
        findViewById<TextView>(R.id.txt_username).text = "${user.firstName} ${user.lastName}"
        findViewById<TextView>(R.id.txt_handle).text = "@${user.username} • Tap photo to change"
        findViewById<TextView>(R.id.txt_zodiac).text =
            createStatSpannable("${zodiac.emoji} ${user.zodiacSign}", "Zodiac Sign")
        findViewById<TextView>(R.id.txt_birthday).text =
            createStatSpannable(ZodiacCalculator.formatPrettyBirthDate(user.birthDate), "Birth Date")
    }

    private fun displayNoteCount() {
        val count = diaryRepository.getEntryCount(currentUsername)
        findViewById<TextView>(R.id.txt_notes).text =
            createStatSpannable(count.toString(), if (count == 1) "Note" else "Notes")
    }

    private fun createStatSpannable(primary: String, secondary: String): SpannableString {
        val fullText = "$primary\n$secondary"
        val primaryColor = ContextCompat.getColor(this, R.color.text_primary)
        val secondaryColor = ContextCompat.getColor(this, R.color.indigo_accent)

        return SpannableString(fullText).apply {
            setSpan(RelativeSizeSpan(1.15f), 0, primary.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, primary.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(primaryColor), 0, primary.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(0.85f), primary.length + 1, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(secondaryColor), primary.length + 1, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadAvatarAndGallery() {
        findViewById<ShapeableImageView>(R.id.profile_image)?.let {
            imageManager.loadProfileImage(this, it)
        }

        val savedImages = imageManager.getSavedImages()
        val container = findViewById<LinearLayout>(R.id.images_container)
        val emptyGallery = findViewById<View>(R.id.layout_empty_gallery)
        val savedCountText = findViewById<TextView>(R.id.txt_saved_looks_count)

        savedCountText.text = "${savedImages.size} saved"
        container.removeAllViews()

        if (savedImages.isEmpty()) {
            container.visibility = View.GONE
            emptyGallery.visibility = View.VISIBLE
            return
        }

        container.visibility = View.VISIBLE
        emptyGallery.visibility = View.GONE

        savedImages.chunked(2).forEach { rowImages ->
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12.dpToPx()
                }
                orientation = LinearLayout.HORIZONTAL
            }

            rowImages.forEachIndexed { index, resId ->
                rowLayout.addView(createGalleryCard(resId, index))
            }

            if (rowImages.size == 1) {
                rowLayout.addView(View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, 1, 1f).apply {
                        marginStart = 6.dpToPx()
                    }
                })
            }

            container.addView(rowLayout)
        }
    }

    private fun createGalleryCard(resId: Int, columnIndex: Int): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 240.dpToPx(), 1f).apply {
                if (columnIndex == 0) marginEnd = 6.dpToPx() else marginStart = 6.dpToPx()
            }
            radius = 16.dpToPx().toFloat()
            cardElevation = 3.dpToPx().toFloat()
            strokeWidth = 1.dpToPx()
            strokeColor = ContextCompat.getColor(this@ProfilePageActivity, R.color.cosmic_stroke)
            setCardBackgroundColor(ContextCompat.getColor(this@ProfilePageActivity, R.color.cosmic_surface_solid))
        }

        val frame = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(resId)
            setOnClickListener {
                ImagePreviewDialog.show(
                    activity = this@ProfilePageActivity,
                    resId = resId,
                    positiveLabel = "Remove"
                ) {
                    confirmRemoveSavedImage(resId)
                }
            }
            setOnLongClickListener {
                confirmRemoveSavedImage(resId)
                true
            }
        }

        val deleteBadge = ImageButton(this).apply {
            layoutParams = FrameLayout.LayoutParams(32.dpToPx(), 32.dpToPx()).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
            }
            setBackgroundResource(R.drawable.card_cosmic_solid)
            setImageResource(R.drawable.ic_delete)
            setPadding(7.dpToPx(), 7.dpToPx(), 7.dpToPx(), 7.dpToPx())
            imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5C77"))
            setOnClickListener { confirmRemoveSavedImage(resId) }
        }

        frame.addView(imageView)
        frame.addView(deleteBadge)
        card.addView(frame)
        return card
    }

    private fun confirmRemoveSavedImage(resId: Int) {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Remove Outfit")
            .setMessage("Remove this look from your saved aesthetics?")
            .setPositiveButton("Remove") { _, _ ->
                imageManager.removeImage(resId)
                loadAvatarAndGallery()
                Toast.makeText(this, "Removed from profile", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
