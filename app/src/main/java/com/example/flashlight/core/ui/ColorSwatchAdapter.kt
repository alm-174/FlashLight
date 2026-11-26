package com.example.flashlight.core.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.flashlight.databinding.ItemColorActionBinding
import com.example.flashlight.databinding.ItemColorSwatchBinding

class ColorSwatchAdapter(
    @DrawableRes private val actionIconRes: Int? = null,
    private val onActionClick: (() -> Unit)? = null,
    private val onColorSelected: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var colors: List<Int> = emptyList()
    @ColorInt private var selectedColor: Int? = null

    private val hasAction: Boolean get() = actionIconRes != null

    fun submitColors(newColors: List<Int>, @ColorInt selected: Int? = selectedColor) {
        colors = newColors
        selectedColor = selected
        notifyDataSetChanged()
    }

    fun setSelectedColor(@ColorInt color: Int?) {
        selectedColor = color
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (hasAction && position == 0) TYPE_ACTION else TYPE_SWATCH

    override fun getItemCount(): Int = colors.size + if (hasAction) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ACTION) {
            ActionVH(ItemColorActionBinding.inflate(inflater, parent, false))
        } else {
            SwatchVH(ItemColorSwatchBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActionVH -> {
                actionIconRes?.let { holder.binding.ivAction.setImageResource(it) }
                holder.itemView.setOnClickListener { onActionClick?.invoke() }
            }
            is SwatchVH -> {
                val color = colors[if (hasAction) position - 1 else position]
                holder.binding.vColor.backgroundTintList = ColorStateList.valueOf(color)
                holder.binding.vSelectedRing.isVisible = (color == selectedColor)
                holder.itemView.setOnClickListener {
                    setSelectedColor(color)
                    onColorSelected(color)
                }
            }
        }
    }

    private class ActionVH(val binding: ItemColorActionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SwatchVH(val binding: ItemColorSwatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    private companion object {
        const val TYPE_ACTION = 0
        const val TYPE_SWATCH = 1
    }
}