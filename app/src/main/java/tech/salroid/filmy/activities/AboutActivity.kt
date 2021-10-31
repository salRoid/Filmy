package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import tech.salroid.filmy.R
import android.widget.TextView
import android.os.Bundle
import android.preference.PreferenceManager
import butterknife.ButterKnife
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class AboutActivity : AppCompatActivity() {
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.logo)
    var logo: TextView? = null
    private var nightMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)
        setContentView(R.layout.activity_developers)

        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = ""
        }
        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        logo!!.typeface = typeface

        if (nightMode) allThemeLogic()

        Glide.with(this).load(getString(R.string.profile_webianks)).into((findViewById<View>(R.id.profile_webianks) as ImageView))
        Glide.with(this).load(getString(R.string.profile_salroid)).into((findViewById<View>(R.id.profile_salroid) as ImageView))
        Glide.with(this).load(getString(R.string.banner_webianks)).into((findViewById<View>(R.id.banner_webianks) as ImageView))
        Glide.with(this).load(getString(R.string.banner_salroid)).into((findViewById<View>(R.id.banner_salroid) as ImageView))
    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendEmail(view: View) {
        when (view.id) {
            R.id.email_webianks -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto: webianks@gmail.com")
                startActivity(Intent.createChooser(emailIntent, "Send feedback"))
            }
            R.id.email_salroid -> {
                val emailIntent2 = Intent(Intent.ACTION_SENDTO)
                emailIntent2.data = Uri.parse("mailto: gupta.sajal631@gmail.com")
                startActivity(Intent.createChooser(emailIntent2, "Send feedback"))
            }
        }
    }

    fun redirectGithub(view: View) {
        when (view.id) {
            R.id.github_webianks -> {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.git_webianks)))
            }
            R.id.github_salroid -> {
                val builder1 = CustomTabsIntent.Builder()
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent1 = builder1.build()
                customTabsIntent1.launchUrl(this, Uri.parse(getString(R.string.git_salroid)))
            }
        }
    }

    private fun viewIntent(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun redirectWebsite(view: View) {
        when (view.id) {
            R.id.website_webianks -> {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.website_webianks)))
            }
            R.id.website_salroid -> {
                val builder1 = CustomTabsIntent.Builder()
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent1 = builder1.build()
                customTabsIntent1.launchUrl(this, Uri.parse(getString(R.string.website_salroid)))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }
}