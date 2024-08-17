package com.pcbtraining.pcb.function

import android.app.ProgressDialog
import android.content.Context

class LoadingDialog(context: Context) {

    private val progressDialog: ProgressDialog = ProgressDialog(context)

    init {
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait...")
    }

    fun show() {
        progressDialog.show()
    }

    fun dismiss() {
        progressDialog.dismiss()
    }
}
