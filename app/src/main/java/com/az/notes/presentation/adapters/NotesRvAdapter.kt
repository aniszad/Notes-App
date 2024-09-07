package com.az.notes.presentation.adapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.R
import com.az.notes.databinding.EmptyNotesListLayoutBinding
import com.az.notes.databinding.NoteViewLayoutBinding
import com.az.notes.domain.Note
import com.az.notes.domain.Theme
import com.az.notes.domain.interfaces.OnLongClickListener
import com.az.notes.domain.interfaces.OnNoteClickListener
import com.az.notes.presentation.utils.setClickAnimationWithAction
import com.az.notes.utils.TimestampFormatter

@SuppressLint("NotifyDataSetChanged")
class NotesRvAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentList = mutableListOf<Note>()
    private var recyclerView: RecyclerView? = null

    private var onNoteClickListener: OnNoteClickListener? = null
    private var onNoteLongClickListener: OnLongClickListener? = null
    private var selectedItems = mutableSetOf<Int>()
    private var selectionMode = false

    companion object{
        private const val EMPTY_VIEW_TYPE = 0
        private const val NORMAL_VIEW_TYPE = 1
    }
    inner class NormalViewHolder(private val binding: NoteViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            Log.e("newList", note.toString())
            // this prevents bugs where some non-themed notes get themed
            resetTheme()
            // this updates the view in case of 'select all' or 'close selection mode' actions
            onAdapterUpdated()
            // seth the theme for the note
            if (note.theme != null) {
                setNoteTheme(note.theme!!)
            }
            // click listener for to view the note, or selecting fi in selecting mode
            binding.root.setClickAnimationWithAction {
                    if (!selectionMode) {
                        onNoteClickListener?.onNoteClicked(note, binding.cardNoteMain)
                    } else {
                        toggleSelection(binding, adapterPosition)
                    }
            }
            // long click listener for selection mode
            binding.root.setOnLongClickListener {
                    toggleSelection(binding, adapterPosition)
                    onNoteLongClickListener?.onLongClick()
                    true
            }
            // set the note info
            with(binding){
                tvNoteTitle.text = note.title
                tvNoteContent.text = note.content
                tvNoteDate.text = TimestampFormatter().timestampToDateAndTime(note.timestamp)
                ivIsPinned.visibility = if (note.isPinned) View.VISIBLE else View.GONE
            }

        }
        private fun setNoteTheme(theme: Theme) {
            binding.clMain.background = ((ContextCompat.getDrawable(context, theme.themeId)))
            if (theme.isLight) {
                binding.tvNoteTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvNoteDate.setTextColor(ContextCompat.getColor(context, R.color.textGray))
                binding.tvNoteContent.setTextColor(ContextCompat.getColor(context, R.color.textGray))
            } else {
                binding.tvNoteTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvNoteDate.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteThird))
                binding.tvNoteContent.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteThird))
            }
        }
        private fun resetTheme() {
            // Clear the background of the ConstraintLayout
            binding.clMain.background = null

            // Reset text colors to default (assuming black is the default)
            binding.tvNoteTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
            binding.tvNoteDate.setTextColor(ContextCompat.getColor(context, R.color.textGray))
            binding.tvNoteContent.setTextColor(ContextCompat.getColor(context, R.color.textGray))

            // Reset the card background to white (or your default color)
            binding.cardNoteMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        private fun onAdapterUpdated() {
            // if selection is active show checkbox else hide it
            binding.cbNoteSelect.isClickable = false
            if (selectionMode) binding.cbNoteSelect.visibility = View.VISIBLE
            else binding.cbNoteSelect.visibility = View.GONE
            if (selectionMode && adapterPosition in selectedItems){
                binding.cardNoteMain.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                binding.cbNoteSelect.isChecked = true
            } else {
                binding.cardNoteMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                binding.cbNoteSelect.isChecked = false
            }
        }
    }

    inner class EmptyViewHolder(private val binding: EmptyNotesListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            EMPTY_VIEW_TYPE -> EmptyViewHolder(
                EmptyNotesListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> NormalViewHolder(
                NoteViewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is NormalViewHolder -> holder.bind(currentList[position])
        }
    }

    override fun getItemCount(): Int = currentList.size

    private fun toggleSelection(binding: NoteViewLayoutBinding, position: Int) {
        if (!selectionMode) {
            selectionMode = true
            notifyDataSetChanged()
        }
        // active selecting
        if (selectedItems.contains(position)) {
            binding.apply {
                cardNoteMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                cbNoteSelect.isChecked = false
            }
            selectedItems.remove(position)
        } else {
            binding.apply {
                cardNoteMain.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                cbNoteSelect.isChecked = true
            }
            selectedItems.add(position)
        }
    }

    fun getSelectedItems(): List<Int> {
        return selectedItems.map { currentList[it].noteId }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.isEmpty()) {
            EMPTY_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    fun closeSelectionMode() {
        selectedItems.clear()
        selectionMode = false
        notifyDataSetChanged()
    }

    fun updateNotes(newNotes: List<Note>) {
        val pinnedNotes = newNotes.filter { it.isPinned }.sortedByDescending { it.timestamp }
        val unpinnedNotes = newNotes.filter { !it.isPinned }.sortedByDescending { it.timestamp }
        val sortedNotes = pinnedNotes + unpinnedNotes

        val oldNotesMap = currentList.associateBy { it.noteId }
        val updatePositions = mutableSetOf<Int>()
        var dataSetChanged = false // Flag to track if notifyDataSetChanged() should be called

        val allIdsEqual = currentList.all { oldNote ->
            sortedNotes.any { newNote ->
                oldNote.noteId == newNote.noteId
            }
        } && currentList.size == sortedNotes.size


        for ((index, newNote) in sortedNotes.withIndex()) {
            val oldNote = oldNotesMap[newNote.noteId]
            if (oldNote != null) {
                if (oldNote != newNote) {
                    currentList[index] = newNote
                    updatePositions.add(index)
                }
                // Check if isPinned field has changed
                if (oldNote.isPinned != newNote.isPinned) {
                    dataSetChanged = true
                }
            }
        }
        if (!allIdsEqual || dataSetChanged){
            currentList.clear()
            currentList.addAll(sortedNotes)
            performFadeInAnimation()
            notifyDataSetChanged()
            return // Exit early if lists are not equal
        }

        // Apply changes to the adapter
        if (updatePositions.isNotEmpty()) {
            updatePositions.forEach {
                notifyItemChanged(it)
            }
        }

        // Check if notifyDataSetChanged() should be called due to isPinned changes
        if (dataSetChanged) {
            notifyDataSetChanged()
        }
    }

    // selecting or deselecting all the elements
    fun selectOrDeselectAll(){
        if (selectedItems.size == currentList.size) deselectAllItems()
        else selectAllItems()

    }
    private fun selectAllItems() {
        selectedItems.addAll(currentList.indices)
        notifyDataSetChanged()
    }
    private fun deselectAllItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    // setting adapter interfaces
    fun setOnNoteClickListener(onNoteClickListener: OnNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener
    }
    fun setOnLongClickListener(onNoteLongClickListener: OnLongClickListener) {
        this.onNoteLongClickListener = onNoteLongClickListener
    }

    private fun performFadeInAnimation() {
        recyclerView?.let { rv ->
            rv.alpha = 0f
            val fadeIn = ObjectAnimator.ofFloat(rv, "alpha", 0f, 1f)
            fadeIn.duration = 500 // duration of the animation in milliseconds
            fadeIn.interpolator = AccelerateDecelerateInterpolator()
            fadeIn.start()
        }
    }

    fun setRecyclerView(rvNotes: RecyclerView) {
        this.recyclerView = rvNotes
    }

    fun updateDeletedNotes(deletedNotesIds: Set<Int>) {
        val updatePositions = mutableSetOf<Int>()

        // Remove deleted notes from currentList
        currentList.removeAll { it.noteId in deletedNotesIds }

        // Update positions for remaining notes after deletion
        val oldNotesMap = currentList.associateBy { it.noteId }
        val remainingNotes = currentList.toList() // Create a copy to iterate safely

        for ((index, note) in remainingNotes.withIndex()) {
            val oldNote = oldNotesMap[note.noteId]
            if (oldNote != null && oldNote != note) {
                currentList[index] = note
                updatePositions.add(index)
            }
        }

        // Apply changes to the adapter
        if (updatePositions.isNotEmpty()) {
            updatePositions.forEach {
                notifyItemChanged(it)
            }
        }
    }

}