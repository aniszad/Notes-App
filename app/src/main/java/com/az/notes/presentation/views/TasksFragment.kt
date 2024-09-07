package com.az.notes.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.az.notes.databinding.FragmentTasksBinding
import com.az.notes.domain.Task
import com.az.notes.domain.interfaces.DeleteTaskListener
import com.az.notes.domain.interfaces.UpdateTaskStatusListener
import com.az.notes.presentation.adapters.TaskRvAdapter
import com.az.notes.presentation.utils.ViewAnimator
import com.az.notes.presentation.viewmodels.ViewModelTasks
import com.az.notes.presentation.views.dialogs.CreateTaskBottomSheetFragment
import com.az.notes.presentation.views.dialogs.MyDialogs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : Fragment() {
    private lateinit var binding: FragmentTasksBinding
    private val viewModelTasks: ViewModelTasks by activityViewModels()
    private var tasksFinishedAdapter: TaskRvAdapter? = null
    private var tasksUnfinishedAdapter: TaskRvAdapter? = null
    private var createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
    private val myDialogs: MyDialogs by lazy {
        MyDialogs(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModelObservers()
        getTasks()
        setFloatingButtonFunc()
    }

    private fun setFloatingButtonFunc() {
        binding.btnAddTask.setOnClickListener{
            createTaskBottomSheetFragment.show(parentFragmentManager, "createTaskBottomSheet")
        }
    }

    private fun setViewModelObservers() {
        viewModelTasks.createTaskResult.observe(viewLifecycleOwner) {
            createTaskBottomSheetFragment.dismiss()
            viewModelTasks.getUnfinishedTasks()
        }
        viewModelTasks.updateTaskStatusResult.observe(viewLifecycleOwner) {
            getTasks()
        }
        viewModelTasks.deleteTaskResult.observe(viewLifecycleOwner) {
            getTasks()
        }
        viewModelTasks.finishedTasks.observe(viewLifecycleOwner) { finishedTasks ->
            updateFinishedTasksRv(finishedTasks)
        }
        viewModelTasks.unfinishedTasks.observe(viewLifecycleOwner) { unfinishedTasks ->
            if (unfinishedTasks != null) updateUnfinishedTasksRv(unfinishedTasks)
        }
    }

    private fun hideFinishedTasksUi() {
        ViewAnimator.changeToGoneWithAnim(binding.llCompletedHistory)
        ViewAnimator.changeToGoneWithAnim(binding.rvTasksDone)
    }

    private fun showFinishedTasksUi() {
        ViewAnimator.changeToVisibleWithAnim(binding.llCompletedHistory)
        ViewAnimator.changeToVisibleWithAnim(binding.rvTasksDone)
    }


    private fun getTasks() {
        viewModelTasks.getFinishedTasks()
        viewModelTasks.getUnfinishedTasks()
    }


    private fun updateFinishedTasksRv(finishedTasks: List<Task>) {
        if (finishedTasks.isEmpty()) hideFinishedTasksUi()
        else showFinishedTasksUi()
        if (tasksFinishedAdapter != null){
            tasksFinishedAdapter?.updateDataset(finishedTasks)
        }else{
            Toast.makeText(requireContext(), "finished tasks: ${finishedTasks.size}", Toast.LENGTH_SHORT).show()
            tasksFinishedAdapter = TaskRvAdapter()
            tasksFinishedAdapter?.updateDataset(finishedTasks)
            tasksFinishedAdapter?.setDeleteTaskListener(object : DeleteTaskListener {
                override fun deleteTask(taskId: Int) {
                    myDialogs.showAgreeDialog(title="Delete Task", content = "Are you sure you want to delete this task?") {
                        viewModelTasks.deleteTask(
                            taskId
                        )
                    }
                }
            })
            tasksFinishedAdapter?.setUpdateStatusListener(object : UpdateTaskStatusListener {
                override fun updateTaskStatus(taskId: Int, isChecked: Boolean) {
                    viewModelTasks.updateTaskStatus(taskId, isChecked)
                }

            })
            binding.rvTasksDone.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvTasksDone.adapter = tasksFinishedAdapter
        }
    }


    private fun updateUnfinishedTasksRv(unfinishedTasks: List<Task>) {
        if (tasksUnfinishedAdapter != null){
            tasksUnfinishedAdapter?.updateDataset(unfinishedTasks)
        }else{
            tasksUnfinishedAdapter = TaskRvAdapter()
            tasksUnfinishedAdapter?.updateDataset(unfinishedTasks)
            tasksUnfinishedAdapter?.setDeleteTaskListener(object : DeleteTaskListener {
                override fun deleteTask(taskId: Int) {
                    myDialogs.showAgreeDialog(title="Delete Task", content = "Are you sure you want to delete this task?") {
                        viewModelTasks.deleteTask(
                            taskId
                        )
                    }
                }
            })
            tasksUnfinishedAdapter?.setUpdateStatusListener(object : UpdateTaskStatusListener {
                override fun updateTaskStatus(taskId: Int, isChecked: Boolean) {
                    Toast.makeText(requireContext(), "task id: $taskId", Toast.LENGTH_SHORT).show()
                    viewModelTasks.updateTaskStatus(taskId, isChecked)
                }

            })
            binding.rvTasks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvTasks.adapter = tasksUnfinishedAdapter
        }

    }
}