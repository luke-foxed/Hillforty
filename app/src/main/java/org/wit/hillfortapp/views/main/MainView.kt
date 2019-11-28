package org.wit.hillfortapp.views.main
import org.wit.hillfortapp.views.hillfort.HillfortView
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel
import org.wit.hillfortapp.views.about.AboutView
import org.wit.hillfortapp.views.account.AccountView
import org.wit.hillfortapp.views.hillfortlist.HillfortListView
import org.wit.hillfortapp.views.login.LoginView
import org.wit.hillfortapp.views.map.HillfortMapsView
import org.wit.hillfortapp.views.stats.StatsView

open class MainView : AppCompatActivity(), AnkoLogger {

    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_main)
        layoutInflater.inflate(R.layout.activity_main, content_frame)

        app = application as MainApp

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        if (intent.hasExtra("user")) {
            val user: UserModel = intent.extras?.getParcelable("user")!!
            mainActivityUsernameText.text = user.username
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // remove icon default color
        navigationView.itemIconTintList = null


        // credit: https://tutorial.eyehunts.com/android/android-navigation-drawer-example-kotlin/
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()

            when (menuItem.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this@MainView, MainView::class.java))
                }
                R.id.nav_hillforts -> {
                    startActivity(Intent(this@MainView, HillfortListView::class.java))
                }

                R.id.nav_hillfort_maps -> {
                    startActivity(Intent(this@MainView, HillfortMapsView::class.java))
                }

                R.id.nav_add -> {
                    startActivity(Intent(this@MainView, HillfortView::class.java))
                }

                R.id.nav_account -> {
                    startActivity(Intent(this@MainView, AccountView::class.java))
                }

                R.id.nav_stats -> {
                    startActivity(Intent(this@MainView, StatsView::class.java))
                }

                R.id.nav_about -> {
                    startActivity(Intent(this@MainView, AboutView::class.java))
                }

                R.id.nav_logout -> {
                    app.activeUser = UserModel()
                    startActivity(Intent(this@MainView, LoginView::class.java))
                }
            }

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}