package com.az.notes.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.R
import com.az.notes.databinding.ThemeItemLayoutBinding
import com.az.notes.domain.Theme

class ThemeAdapter(private val context: Context, private val themeList: List<Theme>, private var selectedPosition:Int = RecyclerView.NO_POSITION) :
    RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

    private lateinit var onThemeSelectedListener: OnThemeSelectedListener

    interface OnThemeSelectedListener {
        fun onThemeSelected(theme: Theme?)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ThemeItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themeList[position]
        holder.bind(theme.themeId, position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return themeList.size
    }

    inner class ThemeViewHolder(private val binding: ThemeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drawableId: Int, isSelected: Boolean) {
            binding.imTheme.setImageDrawable(ContextCompat.getDrawable(context, drawableId))
            binding.cardTheme.strokeWidth = if (isSelected) 8 else 0
            binding.cardTheme.strokeColor = ContextCompat.getColor(context, R.color.colorYellowPrimary)

            binding.cardTheme.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    setSelectedPosition(position)
                    if (::onThemeSelectedListener.isInitialized) {
                        onThemeSelectedListener.onThemeSelected(themeList[position])
                    }
                }
            }
        }
    }

    fun setOnThemeSelectedListener(onThemeSelectedListener: OnThemeSelectedListener) {
        this.onThemeSelectedListener = onThemeSelectedListener
    }

    private fun setSelectedPosition(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedPosition)
    }

    fun resetSelectionPosition() {
        val previousSelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previousSelected)
    }
}