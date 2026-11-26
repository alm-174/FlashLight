package com.example.flashlight.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashlight.databinding.ItemSelectableRowBinding

class LanguageAdapter(
    private val items: List<LanguageOption>,
    private val onSelect: (LanguageOption) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.VH>() {

    private var selectedCode: String? = null

    fun setSelectedCode(code: String?) {
        selectedCode = code
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSelectableRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.ivIcon.setImageResource(item.flagRes)
        holder.binding.tvName.text = item.displayName
        holder.itemView.isSelected = (item.code == selectedCode)
        holder.itemView.setOnClickListener { onSelect(item) }
    }

    override fun getItemCount(): Int = items.size

    class VH(val binding: ItemSelectableRowBinding) : RecyclerView.ViewHolder(binding.root)
}