package com.az.notes.presentation.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.R
import com.az.notes.databinding.FragmentThemeSelectionBottomSheetBinding
import com.az.notes.domain.Theme
import com.az.notes.presentation.adapters.ThemeAdapter
import com.az.notes.presentation.viewmodels.ViewModelEditNote
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThemeSelectionBottomSheetFragment : BottomSheetDialogFragment(), ThemeAdapter.OnThemeSelectedListener {

    private lateinit var binding: FragmentThemeSelectionBottomSheetBinding
    private lateinit var themeAdapter: ThemeAdapter
    private val viewModelEditNote: ViewModelEditNote by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThemeSelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup your RecyclerView or other UI elements for theme selection

        setThemeSelectionRv()
    }

    private fun setThemeSelectionRv() {
        val currentNoteTheme = viewModelEditNote.currentNote.value?.theme
        val position = getThemeList().indexOfFirst { it.themeId == currentNoteTheme?.themeId }
        updateRemoveThemeButton(currentNoteTheme)
        themeAdapter = ThemeAdapter(requireContext(), getThemeList(), if (currentNoteTheme != null) position else RecyclerView.NO_POSITION)
        themeAdapter.setOnThemeSelectedListener(this@ThemeSelectionBottomSheetFragment)
        binding.rvThemes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvThemes.adapter = themeAdapter
        if (currentNoteTheme!=null) binding.rvThemes.smoothScrollToPosition(position)
    }

    private fun updateRemoveThemeButton(currentNoteTheme: Theme?) {
        if (currentNoteTheme != null){
            binding.btnRemoveTheme.isEnabled = true
            binding.btnRemoveTheme.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.btnRemoveTheme.setOnClickListener {
                viewModelEditNote.updateCurrentNote(theme = null)
                themeAdapter.resetSelectionPosition()
            }
        }else{
            binding.btnRemoveTheme.isEnabled = false
            binding.btnRemoveTheme.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }
    }

    private fun getThemeList(): List<Theme> {
        return listOf(
            Theme(R.drawable.ic_theme_1, false),
            Theme(R.drawable.ic_theme_2_light, true),
            Theme(R.drawable.ic_theme_3_dark, false),
            Theme(R.drawable.ic_theme_4_light, true),
            Theme(R.drawable.ic_theme_5_light, true),
            Theme(R.drawable.ic_theme_6_light, true),
            Theme(R.drawable.ic_theme_7_dark, false),
            Theme(R.drawable.ic_theme_8_light, true),
            Theme(R.drawable.ic_theme9_light, true),
        )
    }

    override fun onThemeSelected(theme : Theme?) {
        viewModelEditNote.updateCurrentNote(theme = theme)
    }
}
