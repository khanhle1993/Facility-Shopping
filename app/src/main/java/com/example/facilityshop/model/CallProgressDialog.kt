package com.example.facilityshop.model

import android.app.ProgressDialog
import android.content.Context

class CallProgressDialog {

    lateinit var progressDialog: ProgressDialog

    fun showProgress(context: Context, theme: Int, title: String, message: String) {
        progressDialog = ProgressDialog(context, theme)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }
    fun dismissProgress() {
        progressDialog.dismiss()
    }
}