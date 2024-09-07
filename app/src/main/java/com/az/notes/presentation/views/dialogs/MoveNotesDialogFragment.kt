package com.az.notes.presentation.views.dialogs

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.az.notes.R
import com.az.notes.databinding.FragmentMoveNotesDialogBinding
import com.az.notes.domain.Folder

class MoveNotesDialogFragment : DialogFragment() {

    private var _binding: FragmentMoveNotesDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var folders: List<Folder>
    private lateinit var notesIds: List<Int>
    private lateinit var listener: OnNotesMovedListener

    interface OnNotesMovedListener {
        fun onNotesMoved(targetFolder: Folder, notesIds: List<Int>)
    }

    companion object {
        private const val ARG_FOLDERS = "arg_folders"
        private const val ARG_NOTES = "arg_notes"
        private const val DIALOG_TAG = "MoveNotesDialog"

        fun newInstance(folders: List<Folder>, notes: List<Int>): MoveNotesDialogFragment {
            return MoveNotesDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_FOLDERS, ArrayList(folders))
                    putIntegerArrayList(ARG_NOTES, ArrayList(notes))
                }
            }
        }

        fun show(
            activity: FragmentActivity,
            folders: List<Folder>,
            notesIds: List<Int>,
            listener: OnNotesMovedListener
        ) {
            val dialog = newInstance(folders, notesIds)
            dialog.listener = listener
            dialog.show(activity.supportFragmentManager, "MoveNotesDialog")
        }

        fun show(
            fragment: Fragment,
            folders: List<Folder>,
            notesIds: List<Int>,
            listener: OnNotesMovedListener
        ) {
            val dialog = newInstance(folders, notesIds)
            dialog.listener = listener

            dialog.show(fragment.parentFragmentManager, "MoveNotesDialog")
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(DIALOG_TAG) as? MoveNotesDialogFragment)?.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.TransparentDialog)
        folders = getParcelables<Folder>(ARG_FOLDERS) ?: emptyList()
        notesIds = arguments?.getIntegerArrayList(ARG_NOTES) ?: emptyList()
    }
    private inline fun <reified T : Parcelable?> DialogFragment.getParcelables(tag: String): ArrayList<T>? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(tag, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList<T>(tag) as? ArrayList<T>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoveNotesDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFoldersRadioGroup()
        setupButtons()
    }

    private fun setupFoldersRadioGroup() {
        binding.rgFoldersList.gravity = Gravity.START
        binding.rgFoldersList.setOnCheckedChangeListener { _, _ ->
            binding.btnConfirm.isEnabled = true
        }
        if (folders.isNotEmpty()) {
            for (folder in folders) {
                if (folder.folderId != 1){
                    val radioButton = RadioButton(requireContext())
                    radioButton.text = folder.name
                    radioButton.id = folder.folderId
                    radioButton.buttonDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.radio_button_selector)
                    radioButton.buttonTintList = ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_checked)
                        ),
                        intArrayOf(
                            ContextCompat.getColor(requireContext(), R.color.textGray),
                            ContextCompat.getColor(requireContext(), R.color.colorYellowPrimary)
                        )
                    )
                    radioButton.setPadding(16, 32, 16, 32)
                    binding.rgFoldersList.addView(radioButton)
                }
            }
        }

    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            if (::listener.isInitialized && binding.rgFoldersList.checkedRadioButtonId != -1) {
                listener.onNotesMoved(
                    folders.find { it.folderId == binding.rgFoldersList.checkedRadioButtonId }!!,
                    notesIds
                )
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}