package com.franckrj.hvlov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HvlovAdapter : RecyclerView.Adapter<HvlovAdapter.HvlovViewHolder>() {
    var entryClickedCallback: ((HvlovEntry) -> Unit)? = null
    var listOfEntries = listOf<HvlovEntry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun entryClicked(position: Int) {
        if (position in listOfEntries.indices) {
            entryClickedCallback?.invoke(listOfEntries[position])
        }
    }

    override fun getItemCount(): Int = listOfEntries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HvlovViewHolder {
        val viewHolderMainView = LayoutInflater.from(parent.context).inflate(R.layout.item_hvloventry, parent, false)
        return HvlovViewHolder(viewHolderMainView, ::entryClicked)
    }

    override fun onBindViewHolder(holder: HvlovViewHolder, position: Int) {
        holder.bind(listOfEntries[position], position)
    }

    inner class HvlovViewHolder(private val mainView: View, private val clickCallback: (Int) -> Unit) :
        RecyclerView.ViewHolder(mainView) {
        private val title: TextView = mainView.findViewById(R.id.text_title_hvloventry)

        init {
            mainView.setOnClickListener {
                clickCallback(mainView.tag as Int)
            }
        }

        fun bind(entry: HvlovEntry, position: Int) {
            mainView.tag = position
            title.text = entry.title
        }
    }
}
