package com.az.notes.presentation.views.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.az.notes.R
import com.az.notes.databinding.AgreeDialogLayoutBinding
import com.az.notes.databinding.CreateFolderDialogLayoutBinding
import com.az.notes.databinding.EditFolderDialogLayoutBinding

/**
 * Dialogs
 *
 * @property context
 * @constructor Create empty Create file dialog
 */
class MyDialogs(private val context: Context) {
    private var mAgreeDialog: Dialog? = null
    private lateinit var mCreateFolderDialog : Dialog
    private lateinit var mEditFolderDialog : Dialog
    private lateinit var createFolderBinding : CreateFolderDialogLayoutBinding
    private lateinit var editFolderBinding : EditFolderDialogLayoutBinding

    /**
     * Show create folder dialog
     *
     * @param callback
     * @receiver
     */
    fun showCreateFolderDialog(callback : (folderName : String) -> Unit){
        mCreateFolderDialog = Dialog(context)
        createFolderBinding = CreateFolderDialogLayoutBinding.inflate(LayoutInflater.from(context))
        mCreateFolderDialog.setCancelable(true)
        mCreateFolderDialog.setCanceledOnTouchOutside(true)
        mCreateFolderDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mCreateFolderDialog.setContentView(createFolderBinding.root)
        createFolderBinding.apply {
            etNewFolderName.addTextChangedListener {
                if (it.toString().isNotBlank()){
                    btnCreateFolder.isEnabled = true
                    btnCreateFolder.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
                }else{
                    btnCreateFolder.isEnabled = false
                    btnCreateFolder.setTextColor(ContextCompat.getColorStateList(context, android.R.color.darker_gray))
                }
            }
            btnCreateFolder.setOnClickListener {
                showLoadingButton()
                val newFolderName = createFolderBinding.etNewFolderName.text.toString()
                if (newFolderName.isNotBlank()) {
                    callback.invoke(newFolderName)
                }

            }
            btnCancel.setOnClickListener {
                hideCreateFolderDialog()
            }
        }
        createFolderBinding.btnCreateFolder.text  = buildString{
            append("Create folder")
        }
        mCreateFolderDialog.show()
    }

    private fun showLoadingButton() {
        createFolderBinding.piLoginLoad.visibility = View.VISIBLE
        createFolderBinding.btnCreateFolder.text  = ""
    }

    /**
     * Hide create folder dialog
     *
     */
    fun hideCreateFolderDialog(){
        this.mCreateFolderDialog.hide()
    }



    fun showEditFolderNameDialog(callback: (folderName: String) -> Unit) {
        mEditFolderDialog = Dialog(context)
        editFolderBinding = EditFolderDialogLayoutBinding.inflate(LayoutInflater.from(context))
        mEditFolderDialog.setCancelable(true)
        mEditFolderDialog.setCanceledOnTouchOutside(true)
        mEditFolderDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mEditFolderDialog.setContentView(editFolderBinding.root)

        editFolderBinding.apply {
            etNewFolderName.addTextChangedListener {
                if (it.toString().isNotBlank()) {
                    btnEditFolder.isEnabled = true
                    btnEditFolder.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
                } else {
                    btnEditFolder.isEnabled = false
                    btnEditFolder.setTextColor(ContextCompat.getColorStateList(context, android.R.color.darker_gray))
                }
            }
            btnEditFolder.setOnClickListener {
                showEditLoadingButton()
                val newFolderName = editFolderBinding.etNewFolderName.text.toString()
                if (newFolderName.isNotBlank()) {
                    callback.invoke(newFolderName)
                }
            }
            btnCancel.setOnClickListener {
                hideEditFolderNameDialog()
            }
        }
        editFolderBinding.btnEditFolder.text = buildString {
            append("Edit folder name")
        }
        mEditFolderDialog.show()
    }

    private fun showEditLoadingButton() {
        editFolderBinding.piLoginLoad.visibility = View.VISIBLE
        editFolderBinding.btnEditFolder.text = ""
    }

    /**
     * Hide edit folder name dialog
     */
    fun hideEditFolderNameDialog() {
        mEditFolderDialog.hide()
    }


    fun showAgreeDialog(title : String, content : String, drawable: Drawable?=null, onConfirmed : () -> Unit) {
        mAgreeDialog = Dialog(context)
        val bindingDialog = AgreeDialogLayoutBinding.inflate(LayoutInflater.from(context))
        mAgreeDialog?.setContentView(bindingDialog.root)
        mAgreeDialog?.setCancelable(true)
        mAgreeDialog?.setCanceledOnTouchOutside(true)
        mAgreeDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.tvTitle.text = title
        bindingDialog.tvContent.text = content
        bindingDialog.btnConfirm.setOnClickListener {
            onConfirmed.invoke()
            mAgreeDialog?.dismiss()
        }
        if (drawable != null){
            bindingDialog.imIcon.setImageDrawable(drawable)
        }else{
            bindingDialog.imIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_warning))
        }

        mAgreeDialog?.show()
    }



}