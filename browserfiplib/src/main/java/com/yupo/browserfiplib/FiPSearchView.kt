package com.yupo.browserfiplib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.CollapsibleActionView
import android.view.View
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class FiPSearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs), WebView.FindListener, CollapsibleActionView {

    val searchView: SearchView by lazy { findViewById(R.id.view_manifest_search_view) }
    private val buttonNext: ImageButton by lazy { findViewById(R.id.button_next) }
    private val buttonPrevious: ImageButton by lazy { findViewById(R.id.button_previous) }
    private val buttonClose: ImageButton by lazy { findViewById(R.id.button_close) }
    private val countTextView: TextView by lazy { findViewById(R.id.tv_matches_counter) }
    private val divider: View by lazy { findViewById(R.id.fip_divider) }

    var onNavigationClicked: (ClickEvent) -> Unit = {}
    private lateinit var typedArray: TypedArray
    private var webView: WebView? = null
    private lateinit var settings: Settings

    data class Settings(
        var hint: String,
        var nextArrowIcon: Drawable,
        var previousArrowIcon: Drawable,
        var closeArrowIcon: Drawable,
        var dividerVisibility: Boolean,
        var dividerColor: Int,
        var counterEmptyColor: Int,
        var counterMatchedColor: Int,
        var textColor: Int,
        var hintColor: Int
    )


    init {
        inflate(context, R.layout.fip_search_view, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        initAttrs(context, attrs)
        initView()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FiPSearchView)
        this.typedArray = typedArray
        settings = Settings(
            hint = typedArray.getString(R.styleable.FiPSearchView_fip_hint)
                ?: context.getString(R.string.fip_hint_text),
            nextArrowIcon = typedArray.getDrawableOrDefault(
                R.styleable.FiPSearchView_fip_next_icon,
                R.drawable.arrow_down_24
            ),
            previousArrowIcon = typedArray.getDrawableOrDefault(
                R.styleable.FiPSearchView_fip_previous_icon,
                R.drawable.arrow_up_24
            ),
            closeArrowIcon = typedArray.getDrawableOrDefault(
                R.styleable.FiPSearchView_fip_close_icon,
                R.drawable.ic_close
            ),
            dividerVisibility =
            typedArray.getBoolean(R.styleable.FiPSearchView_fip_divider_visible, true),
            dividerColor =
            typedArray.getColor(
                R.styleable.FiPSearchView_fip_divider_color,
                getColor(R.color.fip_divider)
            ),
            counterEmptyColor = typedArray.getColor(
                R.styleable.FiPSearchView_fip_counter_empty_color,
                getColor(R.color.fip_empty_counter_color)
            ),
            counterMatchedColor = typedArray.getColor(
                R.styleable.FiPSearchView_fip_counter_matched_color,
                getColor(R.color.fip_matched_counter_color)
            ),
            hintColor = typedArray.getColor(
                R.styleable.FiPSearchView_fip_hint_color,
                getColor(R.color.fip_hint_color)
            ),
            textColor = typedArray.getColor(
                R.styleable.FiPSearchView_fip_text_color,
                getColor(R.color.fip_black)
            )
        )
        typedArray.recycle()
    }

    private fun initView() {
        searchView.queryHint = settings.hint
        buttonNext.setOnClickListener {
            onNavigationClicked(ClickEvent.NEXT)
            webView?.findNext(true) ?: errorMessage()
        }
        buttonPrevious.setOnClickListener {
            onNavigationClicked(ClickEvent.PREVIOUS)
            webView?.findNext(false) ?: errorMessage()
        }
        buttonClose.setOnClickListener {
            webView?.clearMatches() ?: errorMessage()
            searchView.clearFocus()
            searchView.setQuery("", true)
            onNavigationClicked(ClickEvent.CLOSE)
        }
        enableButtons(false)
        buttonNext.setImageDrawable(settings.nextArrowIcon)
        buttonPrevious.setImageDrawable(settings.previousArrowIcon)
        buttonClose.setImageDrawable(settings.closeArrowIcon)
        searchView.changeHintColor(settings.hintColor)
        searchView.changeTextColor(settings.textColor)
        divider.visibility = if (settings.dividerVisibility) View.VISIBLE else View.INVISIBLE
        divider.setBackgroundColor(settings.dividerColor)
    }

    fun setupView(body: Settings.() -> Unit) {
        settings = settings.copy().apply(body)
        initView()
    }

    fun setupSearchComponent(webView: WebView) {
        this.webView = webView
        webView.setFindListener(this)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                handleTextChanges(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    countTextView.isVisible = false
                    clearCounter()
                }
                handleTextChanges(newText)
                return false
            }
        })
    }

    private fun handleTextChanges(newText: String?) {
        if (newText.isNullOrEmpty()) {
            webView?.clearMatches()
        } else {
            webView?.findAllAsync(newText)
        }
    }

    private fun enableButtons(state: Boolean) {
        buttonNext.isEnabled = state
        buttonPrevious.isEnabled = state
    }

    override fun onFindResultReceived(
        activeMatchOrdinal: Int,
        numberOfMatches: Int,
        isDoneCounting: Boolean
    ) {
        enableButtons(numberOfMatches != 0)
        val matchesNumber = if (numberOfMatches != 0) activeMatchOrdinal + 1 else activeMatchOrdinal
        if (isDoneCounting) {
            updateMatchesCounter(matchesNumber, numberOfMatches)
        } else {
            clearCounter()
        }
        countTextView.isVisible = isDoneCounting
    }

    override fun onActionViewExpanded() {
        this.searchView.onActionViewExpanded()
    }

    override fun onActionViewCollapsed() {
        this.searchView.onActionViewCollapsed()
    }

    private fun updateMatchesCounter(counter: Int, matchesCount: Int) {
        countTextView.setTextColor(if (matchesCount == 0) settings.counterEmptyColor else settings.counterMatchedColor)
        countTextView.text =
            String.format(context.getString(R.string.fip_counter), counter, matchesCount)
    }

    private fun clearCounter() {
        countTextView.text = String.format(context.getString(R.string.fip_counter), 0, 0)
    }

    /**
     * Method performs cleaning callbacks and WebView, call
     * @see setupSearchComponent to setup widget again
     * Also method automatically called in onDetachedFromWindow() method
     */
    fun release() {
        this.onNavigationClicked = {}
        this.searchView.setOnQueryTextListener(null)
        this.webView = null
    }

    private fun errorMessage(): Nothing =
        throw NullPointerException("Call firstly setupSearchComponent(webView: WebView) to use component with WebView")


    enum class ClickEvent {
        NEXT, PREVIOUS, CLOSE
    }

    private fun SearchView.changeTextColor(@ColorInt color: Int) {
        this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            .setTextColor(color)
    }

    private fun SearchView.changeHintColor(@ColorInt color: Int) {
        this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            .setHintTextColor(color)
    }

    private fun getDrawable(@DrawableRes resId: Int) = ContextCompat.getDrawable(context, resId)
    private fun getColor(@ColorRes resId: Int) = ContextCompat.getColor(context, resId)

    private fun TypedArray.getDrawableOrDefault(
        @StyleableRes resId: Int,
        @DrawableRes default: Int
    ): Drawable {
        return this.getDrawable(resId) ?: this@FiPSearchView.getDrawable(default)!!
    }

}
