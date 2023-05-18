package com.example.runningapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog(): DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener:()-> Unit){
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel the Run")
            .setMessage("Are you sure to cancel the run and delete all the data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_->
            yesListener?.let {
                it()
            }
            }
            .setNegativeButton("No"){dialogInterface,_ ->
                dialogInterface.cancel()
            }
            .create()
    }
}