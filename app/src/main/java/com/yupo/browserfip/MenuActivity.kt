package com.yupo.browserfip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.yupo.browserfip.databinding.ActivityMenuBinding
import com.yupo.browserfiplib.FiPSearchView

class MenuActivity : AppCompatActivity() {

    private val binding: ActivityMenuBinding by lazy { ActivityMenuBinding.inflate(layoutInflater) }
    private var fip: FiPSearchView? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MenuActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.mainWebView.apply {
            defaultSetup()
            loadUrl("file:///android_asset/sample_page.html")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        setupSearchView(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        fip = searchItem.actionView as FiPSearchView
        fip?.setupSearchComponent(binding.mainWebView)
        fip?.onActionViewExpanded()
        fip?.onNavigationClicked = {
            if (it == FiPSearchView.ClickEvent.CLOSE) menu.setMenuItemsVisibility(true)
        }
    }

    private fun Menu.setMenuItemsVisibility(visible: Boolean) {
        for (i in 0 until size()) {
            val item = getItem(i)
            item.isVisible = visible
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fip?.release()
    }
}