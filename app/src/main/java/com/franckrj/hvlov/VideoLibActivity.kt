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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.franckrj.hvlov.databinding.ActivityVideolibBinding

class VideoLibActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideolibBinding
    private val _videoLibViewModel: VideoLibViewModel by viewModels()
    private val _hvlovAdapter = HvlovAdapter()

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

    private fun initViews() {
        binding.swiperefreshMainVideolib.isEnabled = false
        binding.swiperefreshMainVideolib.setColorSchemeResources(R.color.colorAccent)

        binding.listEntriesVideolib.layoutManager = LinearLayoutManager(this)
        binding.listEntriesVideolib.adapter = _hvlovAdapter

        _hvlovAdapter.entryClickedCallback = { hvlovEntry ->
            when (hvlovEntry.type) {
                HvlovEntry.Type.VIDEO -> playVideoInVlc(hvlovEntry.url)
                HvlovEntry.Type.FOLDER -> _videoLibViewModel.currentPath = hvlovEntry.url
            }
        }
    }

    private fun setupLiveDataObservers() {
        _videoLibViewModel.getListOfEntries().observe(this, Observer { loadableListOfEntries ->
            binding.swiperefreshMainVideolib.isRefreshing = (loadableListOfEntries?.status == LoadableValue.Status.LOADING)

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideolibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupLiveDataObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_videolib, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings_videolib) {
            val hvlovSettingsArg = Bundle().apply {
                putString(HvlovSettingsDialog.ARG_SERVER_ADRESS, _videoLibViewModel.hvlovServerSettings.url)
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
