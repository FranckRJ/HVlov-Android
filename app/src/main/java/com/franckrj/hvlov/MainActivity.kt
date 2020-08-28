package com.franckrj.hvlov

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Dumb activity that will only launch the real main activity.
 *
 * Used to always have an unique entry point so the real main activity can change its name and it won't remove user
 * shortcuts on its home screen. Can also be used for splashscreen effect.
 */
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val newVideoLibIntent = Intent(this, VideoLibActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(newVideoLibIntent)
        finish()
    }
}
