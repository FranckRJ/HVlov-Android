package com.franckrj.hvlov

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.franckrj.hvlov.databinding.FragmentVideolibBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Fragment for displaying a folder of [HvlovEntry]s retrieved from an HVlov server.
 */
@AndroidEntryPoint
class VideoLibFolderFragment : Fragment() {
    /**
     * View binding instance.
     */
    private var _binding: FragmentVideolibBinding? = null

    /**
     * ViewModel about the whole HVlov server.
     */
    private val _videoLibViewModel: VideoLibViewModel by activityViewModels()

    /**
     * ViewModel about a specific folder of the HVlov server.
     */
    private val _videoLibFolderViewModel: VideoLibFolderViewModel by viewModels()

    /**
     * Adapter for showing the list of [HvlovEntry].
     */
    private val _hvlovAdapter = HvlovAdapter()

    /**
     * Launch the given URL in VLC, starting from the start of the video.
     *
     * @param videoUrl The URL of the video to launch in VLC.
     */
    private fun playVideoInVlc(videoUrl: String) {
        val videoUri: Uri = Uri.parse(videoUrl)
        val vlcIntent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("org.videolan.vlc")
            setDataAndTypeAndNormalize(videoUri, "video/*")
            putExtra("from_start", true)
        }

        try {
            startActivity(vlcIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.errorVlcNotFound, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Function that init the views and related objects, stuff that cannot be done in XML.
     */
    private fun initViews() {
        val binding = _binding!!

        binding.swiperefreshMainVideolib.isEnabled = false
        binding.swiperefreshMainVideolib.setColorSchemeResources(R.color.colorAccent)

        binding.listEntriesVideolib.layoutManager = LinearLayoutManager(requireContext())
        binding.listEntriesVideolib.adapter = _hvlovAdapter

        _hvlovAdapter.entryClickedCallback = { hvlovEntry ->
            when (hvlovEntry) {
                is HvlovEntry.Folder -> {
                    val newDirection =
                        VideoLibFolderFragmentDirections.actionVideoLibFolderFragmentToVideoLibFolderFragment(
                            hvlovEntry.path
                        )

                    findNavController().navigate(newDirection)
                }
                is HvlovEntry.Video -> playVideoInVlc(_videoLibViewModel.hvlovServerUrl.value + "/" + hvlovEntry.relativeUrl)
                is HvlovEntry.VideoGroup -> {
                    val newDirection =
                        VideoLibFolderFragmentDirections.actionVideoLibFolderFragmentToVideoGroupSelectorDialog(
                            hvlovEntry.relativeUrlPerTags.keys.toTypedArray()
                        )

                    setFragmentResultListener(VideoGroupSelectorDialog.RESULT_VIDEO_TAG_CHOSEN) { _, bundle ->
                        val videoTagChosen = bundle.getString(VideoGroupSelectorDialog.ARG_VIDEO_TAG_CHOSEN)!!
                        playVideoInVlc(_videoLibViewModel.hvlovServerUrl.value + "/" + hvlovEntry.relativeUrlPerTags[videoTagChosen])
                        clearFragmentResultListener(VideoGroupSelectorDialog.RESULT_VIDEO_TAG_CHOSEN)
                    }

                    findNavController().navigate(newDirection)
                }
            }
        }
    }

    /**
     * Function that connect the [LiveData] events to corresponding UI updates.
     */
    private fun setupLiveDataObservers() {
        _videoLibFolderViewModel.listOfEntries.observe(viewLifecycleOwner, { loadableListOfEntries ->
            val binding = _binding!!

            binding.swiperefreshMainVideolib.isRefreshing =
                (loadableListOfEntries?.status == LoadableValue.Status.LOADING)

            if (loadableListOfEntries == null || loadableListOfEntries.status == LoadableValue.Status.ERROR) {
                binding.textErrorVideolib.visibility = View.VISIBLE
                binding.textErrorVideolib.text = getString(R.string.errorServerUnavailable)
            } else if (loadableListOfEntries.status == LoadableValue.Status.LOADED && loadableListOfEntries.value?.isEmpty() != false) { // true or null
                binding.textErrorVideolib.visibility = View.VISIBLE
                binding.textErrorVideolib.text = getString(R.string.warningListEmpty)
            } else {
                binding.textErrorVideolib.visibility = View.GONE
            }

            if (loadableListOfEntries?.status == LoadableValue.Status.LOADED && loadableListOfEntries.value != null) {
                _hvlovAdapter.listOfEntries = loadableListOfEntries.value
            } else {
                _hvlovAdapter.listOfEntries = listOf()
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            _videoLibViewModel.hvlovServerSettingsChanged.collect {
                val newDirection = VideoLibFolderFragmentDirections.actionGlobalReplaceAllWithVideoLibFolderFragment("")

                findNavController().navigate(newDirection)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentVideolibBinding.inflate(inflater, container, false)
        _binding = binding
        initViews()
        setupLiveDataObservers()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
