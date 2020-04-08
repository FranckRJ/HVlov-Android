package com.franckrj.hvlov

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.franckrj.hvlov.databinding.DialogHvlovsettingsBinding
import com.google.android.material.textfield.TextInputLayout

/**
 * A dialog for entering an address / password for an HVlov server.
 *
 * The dialog can receive two arguments:
 * [ARG_SERVER_ADDRESS]: The default content of the address text field. It will be empty if not specified.
 * [ARG_SERVER_PASSWORD]: The default content of the password text field. It will be empty if not specified.
 */
class HvlovSettingsDialog : DialogFragment() {
    companion object {
        const val ARG_SERVER_ADDRESS = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_ADDRESS"
        const val ARG_SERVER_PASSWORD = "com.franckrj.hvlov.HvlovSettingsDialog.ARG_SERVER_PASSWORD"
    }

    /**
     * View binding instance.
     */
    private lateinit var _binding: DialogHvlovsettingsBinding

    /**
     * Callback that will be called when the user validate the dialog (click on 'ok').
     */
    var onDialogResult: ((serverAdress: String, serverPassword: String) -> Unit)? = null

    /**
     * Set the text of a [TextInputLayout] without animating it.
     *
     * @param textInputLayout The [TextInputLayout] where to set the text.
     * @param text The text to set inside the [TextInputLayout].
     */
    private fun unanimatlySetTextInputLayoutText(textInputLayout: TextInputLayout, text: String) {
        val oldAnimEnabledValue: Boolean = textInputLayout.isHintAnimationEnabled
        textInputLayout.isHintAnimationEnabled = false
        textInputLayout.editText?.setText(text)
        textInputLayout.isHintAnimationEnabled = oldAnimEnabledValue
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogHvlovsettingsBinding.inflate(requireActivity().layoutInflater, null, false)

        val serverAdress: String = arguments?.getString(ARG_SERVER_ADDRESS, null) ?: ""
        val serverPassword: String = arguments?.getString(ARG_SERVER_PASSWORD, null) ?: ""

        unanimatlySetTextInputLayoutText(_binding.layoutServeraddressHvlovsettings, serverAdress)
        unanimatlySetTextInputLayoutText(_binding.layoutServerpasswordHvlovsettings, serverPassword)

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.settings)
            setView(_binding.root)
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(R.string.apply) { dialog, _ ->
                onDialogResult?.invoke(
                    _binding.layoutServeraddressHvlovsettings.editText?.text?.toString() ?: "",
                    _binding.layoutServerpasswordHvlovsettings.editText?.text?.toString() ?: ""
                )
                dialog.dismiss()
            }
        }

        return builder.create()
    }
}
