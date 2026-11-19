package com.android.zodiachue.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.zodiachue.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryListAdapter(
    context: Context,
    private val diaryEntries: MutableList<String>,
    private val entryKeys: MutableList<String>,
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Boolean
) : ArrayAdapter<String>(context, R.layout.list_item, R.id.txt_list_title, diaryEntries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val titleView = view.findViewById<TextView>(R.id.txt_list_title)
        val dateView = view.findViewById<TextView>(R.id.txt_list_date)

        titleView.text = diaryEntries[position]

        val currentUserId = UserSession.userData?.username ?: return view
        val sharedPreferences = context.getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val index = entryKeys[position].toInt()
        val dateMillis = sharedPreferences.getLong("${currentUserId}_note_date_$index", System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        dateView.text = dateFormat.format(Date(dateMillis))

        view.setOnClickListener { onItemClick(position) }
        view.setOnLongClickListener { onItemLongClick(position) }

        return view
    }
}