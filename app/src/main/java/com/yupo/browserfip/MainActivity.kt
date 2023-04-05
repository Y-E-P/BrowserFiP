package com.yupo.browserfip

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yupo.browserfip.databinding.ActivityMainBinding
import com.yupo.browserfiplib.FiPSearchView

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.mainWebView.apply {
            defaultSetup()
            loadUrl("file:///android_asset/sample_page.html")
        }
        binding.fip.setupSearchComponent(binding.mainWebView)
        binding.btnMenuActivity.setOnClickListener {
            MenuActivity.start(this)
        }
        binding.btnSearch.setOnClickListener {
            binding.searchContainer.visibility = View.VISIBLE
            binding.toolbar.visibility = View.GONE
            binding.fip.onActionViewExpanded()
        }
        binding.fip.onNavigationClicked = {
            when (it) {
                FiPSearchView.ClickEvent.NEXT -> {}
                FiPSearchView.ClickEvent.PREVIOUS -> {}
                FiPSearchView.ClickEvent.CLOSE -> {
                    binding.searchContainer.visibility = View.GONE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.fip.onActionViewCollapsed()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.fip.release()
    }
}