package edu.vt.cs5254.dreamcatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.ListItemDreamBinding
import java.util.UUID

class DreamHolder(val binding: ListItemDreamBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var boundDream: Dream
        private set

    fun bind(dream: Dream, onDreamClicked: (UUID) -> Unit){
        boundDream = dream
        if(dream.title.isBlank()) binding.listItemTitle.text = binding.root.context.getString(R.string.untitled_dream)
        else binding.listItemTitle.text = dream.title

        binding.root.setOnClickListener {
            onDreamClicked(dream.id)
        }

        binding.listItemReflectionCount.text = binding.root.context.getString(R.string.reflections,
            dream.entries.count { it.kind == DreamEntryKind.REFLECTION })

        when{
            dream.isDeferred -> {
                binding.listItemImage.visibility = View.VISIBLE
                binding.listItemImage.setImageResource(R.drawable.ic_dream_deferred)
            }
            dream.isFulfilled -> {
                binding.listItemImage.visibility = View.VISIBLE
                binding.listItemImage.setImageResource(R.drawable.ic_dream_fulfilled)
            }
            else -> {
                binding.listItemImage.visibility = View.GONE
            }
        }
    }
}

class DreamListAdapter(
    private val dreams: List<Dream>,
    private val onDreamClicked: (UUID) -> Unit
): RecyclerView.Adapter<DreamHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDreamBinding.inflate(inflater, parent, false)
        return DreamHolder(binding)
    }

    override fun getItemCount(): Int {
        return dreams.size
    }

    override fun onBindViewHolder(holder: DreamHolder, position: Int) {
        holder.bind(dreams[position], onDreamClicked)
    }
}