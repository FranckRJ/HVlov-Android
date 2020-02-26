package com.franckrj.hvlov

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.franckrj.hvlov.databinding.ItemHvloventryBinding

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
        val bindingForViewHolder = ItemHvloventryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HvlovViewHolder(bindingForViewHolder, ::entryClicked)
    }

    override fun onBindViewHolder(holder: HvlovViewHolder, position: Int) {
        holder.bind(listOfEntries[position], position)
    }

    inner class HvlovViewHolder(private val _binding: ItemHvloventryBinding, private val _clickCallback: (Int) -> Unit) :
        RecyclerView.ViewHolder(_binding.root) {
        init {
            _binding.root.setOnClickListener {
                _clickCallback(_binding.root.tag as Int)
            }
        }

        fun bind(entry: HvlovEntry, position: Int) {
            _binding.root.tag = position
            _binding.textTitleHvloventry.text = entry.title
        }
    }
}
