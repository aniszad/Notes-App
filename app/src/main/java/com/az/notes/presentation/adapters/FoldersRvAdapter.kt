package com.az.notes.presentation.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.R
import com.az.notes.databinding.FolderLayoutBinding
import com.az.notes.domain.Folder
import com.az.notes.domain.interfaces.OnFolderClickListener
import com.az.notes.domain.interfaces.OnFolderSelectedListener
import com.az.notes.domain.interfaces.OnLongClickListener

class FoldersRvAdapter(
    private val context: Context,
    private var selectedPosition: Int = RecyclerView.NO_POSITION
) : RecyclerView.Adapter<FoldersRvAdapter.ViewHolder>() {

    private var folders: List<Folder> = emptyList()

    private var onFolderClickListener: OnFolderClickListener? = null
    private var onFolderLongClickListener: OnLongClickListener? = null
    private var onFolderSelectedListener: OnFolderSelectedListener? = null
    var inSelectionMode = false
    var selectedFoldersIds = mutableSetOf<Int>()

    inner class ViewHolder(private val binding: FolderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder) {
            val isSpecialFolder = folder.folderId == 1 || folder.folderId == 2

            // Update the selected state
            updateSelectedState(adapterPosition == selectedPosition)

            // Set checkbox visibility
            binding.cbSelectFolder.visibility = if (inSelectionMode && !isSpecialFolder) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (folder.isPinned && !isSpecialFolder) {
                binding.imFolderPinned.visibility = View.VISIBLE
            } else {
                binding.imFolderPinned.visibility = View.GONE
            }

            if (!isSpecialFolder) {
                // Set checkbox state
                binding.cbSelectFolder.isChecked = folder.folderId in selectedFoldersIds

                // Set click listener only once in the ViewHolder initialization
                if (binding.cardMainFolder.hasOnClickListeners().not()) {
                    binding.cardMainFolder.setOnClickListener {
                        if (!inSelectionMode) {
                            val previousPosition = selectedPosition
                            selectedPosition = adapterPosition
                            notifyItemChanged(previousPosition)
                            notifyItemChanged(selectedPosition)
                            onFolderClickListener?.onFolderClicked(folderId = folder.folderId)
                        } else {
                            toggleSelectionMode(folders[adapterPosition].folderId)
                        }
                    }

                    binding.cardMainFolder.setOnLongClickListener {
                        toggleSelectionMode(folders[adapterPosition].folderId)
                        onFolderLongClickListener?.onLongClick()
                        true
                    }
                }

                binding.cbSelectFolder.setOnCheckedChangeListener { _, isChecked ->
                    val folderId = folders[adapterPosition].folderId
                    onFolderSelectedListener?.onFolderSelectedListener(folderId = folder.folderId)
                    if (isChecked) {
                        selectedFoldersIds.add(folderId)
                    } else {
                        selectedFoldersIds.remove(folderId)
                    }
                }
            }else{
                binding.cardMainFolder.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onFolderClickListener?.onFolderClicked(folder.folderId)
                }
            }


            // Set folder name
            binding.tvFolderName.text = folder.name
        }

        private fun toggleSelectionMode(folderId: Int) {
            Toast.makeText(context, "$folderId", Toast.LENGTH_SHORT).show()
            onFolderSelectedListener?.onFolderSelectedListener(folderId = folderId)
            if (selectedFoldersIds.contains(folderId)) {
                selectedFoldersIds.remove(folderId)
            } else {
                selectedFoldersIds.add(folderId)
            }
            if (!inSelectionMode) {
                inSelectionMode = true
                notifyItemRangeChanged(0, itemCount)
            }
            notifyItemChanged(adapterPosition)
        }

        private fun updateSelectedState(isSelected: Boolean) {
            binding.imSelectedFolder.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.colorYellowPrimary else android.R.color.transparent
                )
            )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(folders[position])
    }

    override fun getItemCount(): Int = folders.size

    fun setOnFolderClickListener(onFolderClickListener: OnFolderClickListener) {
        this.onFolderClickListener = onFolderClickListener
    }

    fun setOnLongClickListener(onFolderLongClickListener: OnLongClickListener) {
        this.onFolderLongClickListener = onFolderLongClickListener
    }

    fun setOnFolderSelectedListener(onFolderSelectedListener: OnFolderSelectedListener) {
        this.onFolderSelectedListener = onFolderSelectedListener
    }

    fun updateSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    fun updateDeletedFolders(deletedFoldersIds: Set<Int>) {
        val removedPositions = setOf<Int>()
        for (folderId in deletedFoldersIds) {
            val position = folders.indexOfFirst { it.folderId == folderId }
            removedPositions.plus(position)
        }
        val updatedList = folders.toMutableList()
        for(removedPosition in removedPositions){
            updatedList.removeAt(removedPosition)
            this.folders = updatedList
            notifyItemRemoved(removedPosition)
        }

    }

    fun closeSelectionMode() {
        inSelectionMode = false
        selectedFoldersIds.clear()
        notifyDataSetChanged()
    }

    fun selectOrDeselectAll() {
        if (selectedFoldersIds.size == folders.size) {
            selectedFoldersIds.clear()
        } else {
            selectedFoldersIds.clear()
            selectedFoldersIds.addAll(folders.map { it.folderId })
        }
        notifyDataSetChanged()
    }

    fun updateFolders(newFoldersList: List<Folder>) {
        val specialFolders = newFoldersList.filter { it.folderId == 1 || it.folderId == 2 }
        val remainingFolders = newFoldersList.filter { it.folderId != 1 && it.folderId != 2 }

        // Separate pinned and non-pinned folders from the remaining folders
        val pinnedFolders =
            remainingFolders.filter { it.isPinned }.sortedByDescending { it.pinnedAt }
        val nonPinnedFolders =
            remainingFolders.filter { !it.isPinned }.sortedByDescending { it.timestamp }

        // Combine lists with special folders first
        this.folders = specialFolders + pinnedFolders + nonPinnedFolders
        notifyDataSetChanged()
    }


}