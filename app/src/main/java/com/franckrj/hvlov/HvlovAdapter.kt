package com.franckrj.hvlov

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.franckrj.hvlov.databinding.ItemHvloventryBinding

/**
 * [RecyclerView] adapter for showing the [HvlovEntry]s.
 */
class HvlovAdapter : RecyclerView.Adapter<HvlovAdapter.HvlovViewHolder>() {
    /**
     * The callback called when an [HvlovEntry] is clicked, only if it's not null.
     */
    var entryClickedCallback: ((HvlovEntry) -> Unit)? = null

    /**
     * The list of [HvlovEntry]s displayed.
     */
    var listOfEntries = listOf<HvlovEntry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Function called when an item is clicked, convert the position to an [HvlovEntry] and call the callback with it.
     *
     * @param position The position of the item that have been clicked.
     */
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

    /**
     * View holder for an [HvlovEntry].
     *
     * @property _binding The view binding used to access held views.
     * @property _clickCallback The callback used when the main view is clicked.
     */
    inner class HvlovViewHolder(
        private val _binding: ItemHvloventryBinding,
        private val _clickCallback: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(_binding.root) {
        init {
            _binding.root.setOnClickListener {
                _clickCallback(_binding.root.tag as Int)
            }
        }

        /**
         * Bind this view holder to a specific [HvlovEntry], effectively updating held views.
         *
         * @param entry The [HvlovEntry] to bind the view holder to.
         * @param position The position of the [HvlovEntry] in the list.
         */
        fun bind(entry: HvlovEntry, position: Int) {
            _binding.root.tag = position
            _binding.textTitleHvloventry.text = when (entry) {
                is HvlovEntry.Video -> entry.title
                is HvlovEntry.Folder -> entry.title
            }
        }
    }
}
