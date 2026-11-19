package com.android.zodiachue.helper

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.zodiachue.DiaryListActivity
import com.android.zodiachue.LandingPageActivity
import com.android.zodiachue.ProfilePageActivity
import com.android.zodiachue.R
import com.android.zodiachue.SettingsPageActivity

object BottomNavHelper {
    enum class Tab {
        HOME, DIARY, PROFILE, SETTINGS
    }

    private data class NavItem(
        val tab: Tab,
        val containerId: Int,
        val iconId: Int,
        val labelId: Int,
        val activityClass: Class<out Activity>
    )

    private val navItems = listOf(
        NavItem(Tab.HOME, R.id.nav_home_container, R.id.btn_home, R.id.nav_home_label, LandingPageActivity::class.java),
        NavItem(Tab.DIARY, R.id.nav_diary_container, R.id.btn_diary, R.id.nav_diary_label, DiaryListActivity::class.java),
        NavItem(Tab.PROFILE, R.id.nav_profile_container, R.id.btn_profile, R.id.nav_profile_label, ProfilePageActivity::class.java),
        NavItem(Tab.SETTINGS, R.id.nav_settings_container, R.id.btn_settings, R.id.nav_settings_label, SettingsPageActivity::class.java)
    )

    fun setup(activity: Activity, currentTab: Tab) {
        val activeColor = ContextCompat.getColor(activity, R.color.indigo_accent)
        val activeTextColor = ContextCompat.getColor(activity, R.color.text_primary)
        val inactiveColor = ContextCompat.getColor(activity, R.color.text_muted)

        navItems.forEach { item ->
            val container = activity.findViewById<LinearLayout>(item.containerId) ?: return@forEach
            val icon = activity.findViewById<ImageView>(item.iconId)
            val label = activity.findViewById<TextView>(item.labelId)
            val isActive = item.tab == currentTab

            container.isSelected = isActive
            icon?.imageTintList = ColorStateList.valueOf(if (isActive) activeColor else inactiveColor)
            label?.setTextColor(if (isActive) activeTextColor else inactiveColor)

            val clickListener = {
                if (item.tab != currentTab) {
                    val intent = Intent(activity, item.activityClass).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    }
                    activity.startActivity(intent)
                    @Suppress("DEPRECATION")
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }

            container.setOnClickListener { clickListener() }
            icon?.setOnClickListener { clickListener() }
        }
    }
}
