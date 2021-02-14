package com.franckrj.hvlov

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * A dialog for selection a version of a video in a group of videos.
 */
@AndroidEntryPoint
class VideoGroupSelectorDialog : DialogFragment() {
    companion object {
        const val RESULT_VIDEO_TAG_CHOSEN: String = "RESULT_VIDEO_TAG_CHOSEN"
        const val ARG_VIDEO_TAG_CHOSEN: String = "ARG_VIDEO_TAG_CHOSEN"
    }

    /**
     * Arguments passed to this DialogFragment (list of available tags).
     */
    private val _args: VideoGroupSelectorDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val videoTagsHumanReadable = _args.videoTags.map { tag ->
            val context = requireContext()

            when (tag) {
                "uhd" -> context.getString(R.string.uhdTag)
                "hd" -> context.getString(R.string.hdTag)
                "md" -> context.getString(R.string.mdTag)
                "sd" -> context.getString(R.string.sdTag)
                "usd" -> context.getString(R.string.usdTag)
                else -> tag
            }
        }

        val builder = AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.selectVersionInVideoGroup)
            setItems(videoTagsHumanReadable.toTypedArray()) { dialog, which ->
                setFragmentResult(RESULT_VIDEO_TAG_CHOSEN, bundleOf(ARG_VIDEO_TAG_CHOSEN to _args.videoTags[which]))
                dialog.dismiss()
            }
        }

        return builder.create()
    }
}
