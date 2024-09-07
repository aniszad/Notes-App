package com.az.notes.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.databinding.NotesFolderLayoutBinding
import com.az.notes.domain.Folder
import com.az.notes.domain.interfaces.OnFolderClickListener


class FolderNamesRvAdapter(
    private var selectedPosition: Int = 0
) :
    RecyclerView.Adapter<FolderNamesRvAdapter.ViewHolder>() {
    private lateinit var onFolderClickListener: OnFolderClickListener

    private var folders: List<Folder> = emptyList()
    inner class ViewHolder(private val binding: NotesFolderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder) {
            binding.tvFolderName.text = folder.name
            binding.cardMain.isSelected = adapterPosition == selectedPosition
            if (folder.isPinned) binding.imFolderPinned.visibility =
                ViewGroup.VISIBLE else binding.imFolderPinned.visibility = ViewGroup.GONE
            binding.cardMain.setOnClickListener {
                onFolderClickListener.onFolderClicked(folders[adapterPosition].folderId)
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotesFolderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(folders[position])
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    fun setOnFolderClickListener(onFolderClickListener: OnFolderClickListener) {
        this.onFolderClickListener = onFolderClickListener
    }


    fun getSelectedFolder(): Folder {
        return if (selectedPosition != 0)
            folders[selectedPosition] else folders.find { it.name == "Uncategorized" && it.folderId == 2 }!!
    }

    fun getSelectedFolderPosition(): Int {
        return selectedPosition
    }

    @Suppress("NotifyDataSetChanged")
    fun updateFolders(newFoldersList: List<Folder>, selectedFolderId : Int? = null) {

        val specialFolders = newFoldersList.filter { it.folderId == 1 || it.folderId == 2 }
        val remainingFolders = newFoldersList.filter { it.folderId != 1 && it.folderId != 2 }

        // Separate pinned and non-pinned folders from the remaining folders
        val pinnedFolders =
            remainingFolders.filter { it.isPinned }.sortedByDescending { it.pinnedAt }
        val nonPinnedFolders =
            remainingFolders.filter { !it.isPinned }.sortedByDescending { it.timestamp }

        // Combine lists with special folders first
        this.folders = specialFolders + pinnedFolders + nonPinnedFolders
        if (selectedFolderId!=null){
            this.selectedPosition = this.folders.indexOfFirst { folder ->
                folder.folderId == selectedFolderId
            }
        }

        notifyDataSetChanged()
    }

}