package com.franckrj.hvlov

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
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
     * Return the [NavController] associated with this activity.
     *
     * @return The [NavController] associated with this activity.
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_videolib) as NavHostFragment
        return navHostFragment.navController
    }

    /**
     * Function that init the views and related objects, stuff that cannot be done in XML.
     */
    private fun initViews() {
        setSupportActionBar(_binding.toolbarVideolib)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        _binding.toolbarVideolib.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideolibBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_videolib, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
