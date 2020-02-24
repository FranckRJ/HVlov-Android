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
import kotlinx.android.synthetic.main.activity_videolib.*

class VideoLibActivity : AppCompatActivity() {
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
        swiperefresh_main_videolib.isEnabled = false
        swiperefresh_main_videolib.setColorSchemeResources(R.color.colorAccent)

        list_entries_videolib.layoutManager = LinearLayoutManager(this)
        list_entries_videolib.adapter = _hvlovAdapter

        _hvlovAdapter.entryClickedCallback = { hvlovEntry -> playVideoInVlc(hvlovEntry.url) }
    }

    private fun setupLiveDataObservers() {
        _videoLibViewModel.getListOfEntries().observe(this, Observer { loadableListOfEntries ->
            swiperefresh_main_videolib.isRefreshing = (loadableListOfEntries?.status == LoadableValue.Status.LOADING)

            if (loadableListOfEntries == null || loadableListOfEntries.status == LoadableValue.Status.ERROR) {
                text_error_videolib.visibility = View.VISIBLE
                text_error_videolib.text = getString(R.string.errorServerUnavailable)
            } else if (loadableListOfEntries.status == LoadableValue.Status.LOADED && loadableListOfEntries.value?.isEmpty() != false) { // true or null
                text_error_videolib.visibility = View.VISIBLE
                text_error_videolib.text = getString(R.string.warningListEmpty)
            } else {
                text_error_videolib.visibility = View.GONE
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
        setContentView(R.layout.activity_videolib)

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
