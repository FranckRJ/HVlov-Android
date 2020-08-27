package com.franckrj.hvlov

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.franckrj.hvlov.databinding.ActivityVideolibBinding
import dagger.hilt.android.AndroidEntryPoint

// TODO: Log a lot more stuff.

/**
 * Activity for browsing the [HvlovEntry]s of an HVlov server.
 */
@AndroidEntryPoint
class VideoLibActivity : AppCompatActivity() {
    /**
     * View binding instance.
     */
    private lateinit var _binding: ActivityVideolibBinding

    /**
     * ViewModel for the activity.
     */
    private val _videoLibViewModel: VideoLibViewModel by viewModels()

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
            Toast.makeText(this, R.string.errorVlcNotFound, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Function that init the views and related objects, stuff that cannot be done in XML.
     */
    private fun initViews() {
        _binding.swiperefreshMainVideolib.isEnabled = false
        _binding.swiperefreshMainVideolib.setColorSchemeResources(R.color.colorAccent)

        _binding.listEntriesVideolib.layoutManager = LinearLayoutManager(this)
        _binding.listEntriesVideolib.adapter = _hvlovAdapter

        _hvlovAdapter.entryClickedCallback = { hvlovEntry ->
            when (hvlovEntry.type) {
                HvlovEntry.Type.VIDEO -> playVideoInVlc(hvlovEntry.url)
                HvlovEntry.Type.FOLDER -> _videoLibViewModel.currentPath = hvlovEntry.url
            }
        }
    }

    /**
     * Function that connect the [LiveData] events to corresponding UI updates.
     */
    private fun setupLiveDataObservers() {
        _videoLibViewModel.getListOfEntries().observe(this, Observer { loadableListOfEntries ->
            _binding.swiperefreshMainVideolib.isRefreshing =
                (loadableListOfEntries?.status == LoadableValue.Status.LOADING)

            if (loadableListOfEntries == null || loadableListOfEntries.status == LoadableValue.Status.ERROR) {
                _binding.textErrorVideolib.visibility = View.VISIBLE
                _binding.textErrorVideolib.text = getString(R.string.errorServerUnavailable)
            } else if (loadableListOfEntries.status == LoadableValue.Status.LOADED && loadableListOfEntries.value?.isEmpty() != false) { // true or null
                _binding.textErrorVideolib.visibility = View.VISIBLE
                _binding.textErrorVideolib.text = getString(R.string.warningListEmpty)
            } else {
                _binding.textErrorVideolib.visibility = View.GONE
            }

            if (loadableListOfEntries?.status == LoadableValue.Status.LOADED && loadableListOfEntries.value != null) {
                _hvlovAdapter.listOfEntries = loadableListOfEntries.value
            } else {
                _hvlovAdapter.listOfEntries = listOf()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideolibBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initViews()
        setupLiveDataObservers()
    }

    override fun onBackPressed() {
        if (!_videoLibViewModel.goToPreviousFolder()) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_videolib, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings_videolib) {
            // TODO: Move this code in its own function.
            val hvlovSettingsArg = Bundle().apply {
                putString(HvlovSettingsDialog.ARG_SERVER_ADDRESS, _videoLibViewModel.hvlovServerSettings.url)
                putString(HvlovSettingsDialog.ARG_SERVER_PASSWORD, _videoLibViewModel.hvlovServerSettings.password)
            }
            val hvlovSettingsDialog = HvlovSettingsDialog().apply {
                arguments = hvlovSettingsArg
                onDialogResult = { serverAdress, serverPassword ->
                    _videoLibViewModel.setServerAccessInfo(serverAdress, serverPassword)
                }
            }
            hvlovSettingsDialog.show(supportFragmentManager, "HvlovSettingsDialog")
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
