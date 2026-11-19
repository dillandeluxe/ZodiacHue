package com.android.zodiachue.helper

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.zodiachue.R
import com.android.zodiachue.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryAdapter(
    private val onEntryClick: (DiaryEntry) -> Unit,
    private val onDeleteClick: (DiaryEntry) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    private val items = mutableListOf<DiaryEntry>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())

    fun submitList(newItems: List<DiaryEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.txt_list_title)
        private val previewText: TextView = itemView.findViewById(R.id.txt_list_preview)
        private val dateText: TextView = itemView.findViewById(R.id.txt_list_date)
        private val moodBadge: TextView = itemView.findViewById(R.id.txt_list_mood)
        private val colorDot: View = itemView.findViewById(R.id.view_entry_dot)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete_entry)

        fun bind(entry: DiaryEntry) {
            titleText.text = entry.title
            previewText.text = entry.content
            dateText.text = dateFormat.format(Date(entry.updatedAt))
            moodBadge.text = entry.mood.ifBlank { "Inspired" }

            val parsedColor = try {
                Color.parseColor(entry.colorHex.ifBlank { "#5B7CFA" })
            } catch (_: Exception) {
                Color.parseColor("#5B7CFA")
            }

            (colorDot.background?.mutate() as? GradientDrawable)?.setColor(parsedColor)

            itemView.setOnClickListener { onEntryClick(entry) }
            deleteButton.setOnClickListener { onDeleteClick(entry) }
        }
    }
}
