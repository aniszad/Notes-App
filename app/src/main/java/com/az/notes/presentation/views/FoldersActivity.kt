package com.az.notes.presentation.views

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.az.notes.R
import com.az.notes.data.local.MySharedPreferences
import com.az.notes.databinding.ActivityFoldersBinding
import com.az.notes.domain.Folder
import com.az.notes.domain.interfaces.OnFolderClickListener
import com.az.notes.domain.interfaces.OnFolderSelectedListener
import com.az.notes.domain.interfaces.OnLongClickListener
import com.az.notes.presentation.adapters.FoldersRvAdapter
import com.az.notes.presentation.viewmodels.ViewModelFolders
import com.az.notes.presentation.views.dialogs.MyDialogs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FoldersActivity : BaseActivity(), OnFolderClickListener,
    OnLongClickListener, OnFolderSelectedListener {
    private lateinit var binding: ActivityFoldersBinding
    private val viewModelFolders: ViewModelFolders by viewModels()
    private lateinit var foldersRvAdapter: FoldersRvAdapter
    private val myDialogs: MyDialogs by lazy {
        MyDialogs(this@FoldersActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityFoldersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeSystemBars(
            binding.root.id)

        onBackPressedDispatcher.addCallback {
            if (foldersRvAdapter.inSelectionMode){
                viewModelFolders.setSelectionMode(false)
            }else{
                finish()
            }
        }
        setStatusBarLight(true)

        setUiFunc()
        setFoldersSelectionToolbar()

        setViewModelObservers()
        getAllFolders()
    }

    private fun setUiFunc() {
        binding.btnCreateFolder.setOnClickListener {
            myDialogs.showCreateFolderDialog { folderName ->
                viewModelFolders.createFolder(
                    Folder(
                        name = folderName,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getAllFolders() {
        viewModelFolders.getAllFolders()
    }

    private fun setViewModelObservers() {
        viewModelFolders.folders.observe(this@FoldersActivity) { foldersList ->
            val sortedFoldersList = foldersList.sortedBy { folder -> folder.timestamp }
            setFoldersRv(sortedFoldersList.toMutableList())
        }
        viewModelFolders.createFolderResult.observe(this@FoldersActivity) { result ->
            if (result.isSuccess) {
                getAllFolders()
                myDialogs.hideCreateFolderDialog()
            } else {
                Toast.makeText(this@FoldersActivity, "Failed to create folder", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModelFolders.pinFolderResult.observe(this@FoldersActivity){ result ->
            if (result.isSuccess){
                viewModelFolders.setSelectionMode(false)
                getAllFolders()
            }else{
                Toast.makeText(this@FoldersActivity, "failed to pin folders", Toast.LENGTH_SHORT).show()
            }
        }
        viewModelFolders.deleteFoldersResult.observe(this@FoldersActivity){ result ->
            if (result.isSuccess){
                viewModelFolders.setSelectionMode(false)
                getAllFolders()
            }else{
                Toast.makeText(this@FoldersActivity, "failed to delete folder", Toast.LENGTH_SHORT).show()
            }
        }
        viewModelFolders.updateFolderNameResult.observe(this@FoldersActivity){  result ->
            if (result.isSuccess){
                myDialogs.hideEditFolderNameDialog()
                viewModelFolders.setSelectionMode(false)
                getAllFolders()
            }else{
                Toast.makeText(this@FoldersActivity, "failed to update folder", Toast.LENGTH_SHORT).show()
            }
        }
        viewModelFolders.selectionMode.observe(this@FoldersActivity){   inSelectionMode ->
            setUi(inSelectionMode)
            if (!inSelectionMode){
                foldersRvAdapter.closeSelectionMode()
            }
        }
        viewModelFolders.selectedFoldersIds.observe(this@FoldersActivity){ selectedFoldersIds ->
            Log.e("selectedFoldersIds", selectedFoldersIds.toString())
            updateToolbar(selectedFoldersIds.size)
        }
    }

    private fun updateToolbar(numSelectedFolders : Int){
        if (numSelectedFolders == 0) {
            binding.toolbar.menu.removeItem(R.id.action_edit_folder)
            binding.toolbar.menu.removeItem(R.id.action_pin_selected_folders)
            binding.toolbar.menu.removeItem(R.id.action_delete_selected_folders)
            return
        }
        if (numSelectedFolders == 1) {
            binding.toolbar.menu.clear()
            binding.toolbar.inflateMenu(R.menu.menu_edit_folders)
        }
        if(numSelectedFolders > 1){
            binding.toolbar.menu.clear()
            binding.toolbar.inflateMenu(R.menu.menu_edit_folders)
            binding.toolbar.menu.removeItem(R.id.action_edit_folder)
        }
    }

    private fun setUi(inSelectionMode: Boolean) {
        with(binding){
            if (inSelectionMode){
                toolbar.navigationIcon = ContextCompat.getDrawable(this@FoldersActivity, R.drawable.ic_close)
                toolbar.menu.clear()
                toolbar.inflateMenu(R.menu.menu_edit_folders)
            }else{
                toolbar.navigationIcon = ContextCompat.getDrawable(this@FoldersActivity, R.drawable.ic_back_arrow)
                toolbar.menu.clear()
            }
        }

    }

    private fun setFoldersRv(foldersList: List<Folder>) {
        if (::foldersRvAdapter.isInitialized){
            foldersRvAdapter.updateFolders(foldersList)
        }else{
            foldersRvAdapter = FoldersRvAdapter(
                this@FoldersActivity,
                foldersList.indexOf(foldersList.find { it.folderId == MySharedPreferences(this@FoldersActivity).getSelectedFolder() })
            )
            foldersRvAdapter.updateFolders(foldersList)
            foldersRvAdapter.setOnFolderClickListener(this@FoldersActivity)
            foldersRvAdapter.setOnFolderSelectedListener(this@FoldersActivity)
            foldersRvAdapter.setOnLongClickListener(this@FoldersActivity)
            binding.rvFolders.layoutManager =
                LinearLayoutManager(this@FoldersActivity, LinearLayoutManager.VERTICAL, false)
            binding.rvFolders.adapter = foldersRvAdapter
        }

    }
    private fun setFoldersSelectionToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_select_all_folders -> {
                    foldersRvAdapter.selectOrDeselectAll()
                    true
                }

                R.id.action_edit_folder -> {
                    myDialogs.showEditFolderNameDialog { folderName ->
                        viewModelFolders.updateFolderName(
                            foldersRvAdapter.selectedFoldersIds.first(),
                            folderName
                        )
                    }
                    true
                }
                R.id.action_pin_selected_folders ->{
                    Toast.makeText(this@FoldersActivity, "Pinning folders", Toast.LENGTH_SHORT).show()
                    viewModelFolders.pinFolders(foldersRvAdapter.selectedFoldersIds)
                    true
                }

                R.id.action_delete_selected_folders -> {
                    myDialogs.showAgreeDialog("Delete Notes", "Are you sure you want to delete these notes?", onConfirmed = {
                        viewModelFolders.deleteFolders(foldersRvAdapter.selectedFoldersIds)
                    })

                    true
                }

                else -> false
            }
        }
    }

    override fun onFolderClicked(folderId: Int) {
        viewModelFolders.setSelectedFolderId(folderId)
    }

    override fun onLongClick() {
        viewModelFolders.setSelectionMode(true)
    }

    override fun onFolderSelectedListener(folderId: Int) {
        viewModelFolders.updateSelectedFolderIds(folderId)
    }
}