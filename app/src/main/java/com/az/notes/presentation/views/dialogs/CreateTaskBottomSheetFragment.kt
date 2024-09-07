package com.az.notes.presentation.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.az.notes.R
import com.az.notes.databinding.FragmentCreateTaskBottomSheetBinding
import com.az.notes.domain.Task
import com.az.notes.presentation.viewmodels.ViewModelTasks
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.sql.Timestamp

class CreateTaskBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModelTasks: ViewModelTasks by activityViewModels()
    private lateinit var binding: FragmentCreateTaskBottomSheetBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTaskBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEditTextObserver()
        setButtonFunc()
    }


    private fun setButtonFunc() {
        binding.btnCreateTask.setOnClickListener {
            viewModelTasks.createTask(
                Task(
                    content = binding.etTaskContent.text.toString(),
                    done = false,
                    timestamp = Timestamp(System.currentTimeMillis()).time
                )
            )
            dismiss()
        }
    }

    private fun setEditTextObserver() {
        binding.etTaskContent.addTextChangedListener { editable ->
            if (editable.toString().isNotBlank()) {
                binding.btnCreateTask.isEnabled = true
                binding.btnCreateTask.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }else{
                binding.btnCreateTask.isEnabled = false
                binding.btnCreateTask.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
        }
    }

}
