package com.franckrj.hvlov

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.franckrj.hvlov.databinding.DialogHvlovsettingsBinding
import com.google.android.material.textfield.TextInputLayout

class HvlovSettingsDialog : DialogFragment() {
    companion object {
        const val ARG_SERVER_ADRESS = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_ADRESS"
        const val ARG_SERVER_PASSWORD = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_PASSWORD"
    }

    private lateinit var _binding: DialogHvlovsettingsBinding

    var onDialogResult: ((serverAdress: String, serverPassword: String) -> Unit)? = null

    fun unanimatlySetTextInputLayoutText(textInputLayout: TextInputLayout, text: String) {
        val oldAnimEnabledValue: Boolean = textInputLayout.isHintAnimationEnabled
        textInputLayout.isHintAnimationEnabled = false
        textInputLayout.editText?.setText(text)
        textInputLayout.isHintAnimationEnabled = oldAnimEnabledValue
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogHvlovsettingsBinding.inflate(requireActivity().layoutInflater, null, false)

        val serverAdress: String = arguments?.getString(ARG_SERVER_ADRESS, null) ?: ""
        val serverPassword: String = arguments?.getString(ARG_SERVER_PASSWORD, null) ?: ""

        unanimatlySetTextInputLayoutText(_binding.layoutServeradressHvlovsettings, serverAdress)
        unanimatlySetTextInputLayoutText(_binding.layoutServerpasswordHvlovsettings, serverPassword)

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.settings)
            setView(_binding.root)
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(R.string.apply) { dialog, _ ->
                onDialogResult?.invoke(
                    _binding.layoutServeradressHvlovsettings.editText?.text?.toString() ?: "",
                    _binding.layoutServerpasswordHvlovsettings.editText?.text?.toString() ?: ""
                )
                dialog.dismiss()
            }
        }

        return builder.create()
    }
}
