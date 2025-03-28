package edu.vt.cs5254.dreamcatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.ListItemDreamEntryBinding

class DreamEntryHolder(private val binding: ListItemDreamEntryBinding) : RecyclerView.ViewHolder(binding.root) {

    lateinit var boundEntry: DreamEntry
        private set

    fun bind(entry: DreamEntry) {
        boundEntry = entry
        binding.dreamEntryButton.configureForEntry(entry)
    }

    private fun Button.configureForEntry(entry: DreamEntry){
        text = entry.kind.toString()
        visibility = View.VISIBLE
        when(entry.kind){
            DreamEntryKind.REFLECTION -> {
                text = entry.text
                isAllCaps = false
                setBackgroundWithContrastingText("navy")
            }
            DreamEntryKind.DEFERRED -> {
                setBackgroundWithContrastingText("lightgrey")
            }
            DreamEntryKind.FULFILLED -> {
                setBackgroundWithContrastingText("purple")
            }
            DreamEntryKind.CONCEIVED -> {
                setBackgroundWithContrastingText("aqua")
            }
        }
    }
}

class DreamEntryAdapter(private val entries: List<DreamEntry>) : RecyclerView.Adapter<DreamEntryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamEntryHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemDreamEntryBinding.inflate(layoutInflater, parent, false)
        return DreamEntryHolder(binding)
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: DreamEntryHolder, position: Int) {
        holder.bind(entries[position])
    }

}