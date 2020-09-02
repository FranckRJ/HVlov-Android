package com.franckrj.hvlov

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.franckrj.hvlov.databinding.ActivityVideolibBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// TODO: Log a lot more stuff.

/**
 * Activity for browsing the [HvlovEntry]s of an HVlov server.
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class VideoLibActivity : AppCompatActivity() {
    /**
     * View binding instance.
     */
    private lateinit var _binding: ActivityVideolibBinding

    /**
     * Function that init the views and related objects, stuff that cannot be done in XML.
     */
    private fun initViews() {
        setSupportActionBar(_binding.toolbarVideolib)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideolibBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initViews()

        if (savedInstanceState == null) {
            val argBundle = Bundle().apply {
                putString(VideoLibFolderFragment.ARG_FOLDER_PATH, "")
            }

            val videoLibFolderFragment = VideoLibFolderFragment().apply {
                arguments = argBundle
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_videolib, videoLibFolderFragment, "VideoLibFolderFragment").commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_videolib, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings_videolib) {
            val hvlovSettingsDialog = HvlovSettingsDialog()
            hvlovSettingsDialog.show(supportFragmentManager, "HvlovSettingsDialog")
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
