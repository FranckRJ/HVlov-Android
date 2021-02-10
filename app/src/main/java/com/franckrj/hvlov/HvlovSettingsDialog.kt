package com.franckrj.hvlov

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.franckrj.hvlov.databinding.DialogHvlovsettingsBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A dialog for setting an address / password for an HVlov server that will be saved in the preferences.
 */
@AndroidEntryPoint
class HvlovSettingsDialog : DialogFragment() {
    /**
     * View binding instance.
     */
    private lateinit var _binding: DialogHvlovsettingsBinding

    /**
     * The service used to access HVlov preferences.
     */
    @Inject
    lateinit var hvlovPreferencesService: HvlovPreferencesService

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

        val hvlovServerSettings: HvlovServerSettings = hvlovPreferencesService.hvlovServerSettings.value

        unanimatlySetTextInputLayoutText(_binding.layoutServeraddressHvlovsettings, hvlovServerSettings.url)
        unanimatlySetTextInputLayoutText(_binding.layoutServerpasswordHvlovsettings, hvlovServerSettings.password)

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.settings)
            setView(_binding.root)
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(R.string.apply) { dialog, _ ->
                val serverAdressText = _binding.layoutServeraddressHvlovsettings.editText?.text?.toString() ?: ""
                val serverPasswordText = _binding.layoutServerpasswordHvlovsettings.editText?.text?.toString() ?: ""
                hvlovPreferencesService.setServerAccessInfo(serverAdressText, serverPasswordText)
                dialog.dismiss()
            }
        }

        return builder.create()
    }
}
