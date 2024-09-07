package com.az.notes.domain.interfaces

import com.az.notes.domain.Note
import com.google.android.material.card.MaterialCardView

interface OnNoteClickListener {
    fun onNoteClicked(note: Note, cardNoteMain: MaterialCardView)
}