package com.example.featureremoteconfig.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.example.featureremoteconfig.R
import com.example.featureremoteconfig.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        FirebaseApp.initializeApp(baseContext)
        setUpListeners()
        setUpFirebase()
        fetchRemoteConfig()
        setupVisualToggle()
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener {
            showSnackBar(it)
        }
        binding.content.featureA.setOnClickListener {
            if (checkFeatureToogle(FEATURE_A)) {
                showSnackBar(it)
            }
        }
        binding.content.featureB.setOnClickListener {
            if (checkFeatureToogle(FEATURE_B)) {
                showSnackBar(it)
            }
        }
    }

    private fun setupVisualToggle() {
        binding.content.card.apply {
            isVisible = checkFeatureToogle(FEATURE_A)
        }
    }

    private fun showSnackBar(view: View) {
        Snackbar.make(view, "A Feature está Habilitada", Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.fab)
            .setAction("Action", null).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpFirebase() {
        remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = FETCH_INTEVAL_IN_SECONDS
        })
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("Valores -> ", "Config params updated: $updated")
                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkFeatureToogle(feature: String) = remoteConfig[feature].asBoolean()

    companion object {
        //intervalo mínimo de busca para permitir atualizações frequentes:
        const val FETCH_INTEVAL_IN_SECONDS = 30L
        const val FEATURE_A = "FEATURE_A"
        const val FEATURE_B = "FEATURE_B"
    }
}