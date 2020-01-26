package com.franckrj.hvlov

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout

class HvlovSettingsDialog : DialogFragment() {
    companion object {
        const val ARG_SERVER_ADRESS = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_ADRESS"
        const val ARG_SERVER_PASSWORD = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_PASSWORD"
    }

    var onDialogResult: ((serverAdress: String, serverPassword: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogLayout: View = requireActivity().layoutInflater.inflate(R.layout.dialog_hvlovsettings, null)

        val serverAdress: String = arguments?.getString(ARG_SERVER_ADRESS, null) ?: ""
        val serverPassword: String = arguments?.getString(ARG_SERVER_PASSWORD, null) ?: ""

        val serverAdressLayout: TextInputLayout = dialogLayout.findViewById(R.id.layout_serveradress_hvlovsettings)
        val serverPasswordLayout: TextInputLayout = dialogLayout.findViewById(R.id.layout_serverpassword_hvlovsettings)

        serverAdressLayout.editText?.setText(serverAdress)
        serverPasswordLayout.editText?.setText(serverPassword)

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.settings)
            setView(dialogLayout)
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(R.string.apply) { dialog, _ ->
                onDialogResult?.invoke(
                    serverAdressLayout.editText?.text?.toString() ?: "",
                    serverPasswordLayout.editText?.text?.toString() ?: ""
                )
                dialog.dismiss()
            }
        }

        return builder.create()
    }
}
