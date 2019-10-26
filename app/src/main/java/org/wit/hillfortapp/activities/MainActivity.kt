package org.wit.hillfortapp.activities
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
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.UserModel

open class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var app: MainApp

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

        mainActivityUsernameText.text = app.activeUser.username
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
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
                R.id.nav_hillforts -> {
                    startActivity(Intent(this@MainActivity, HillfortListActivity::class.java))
                }
                R.id.nav_add -> {
                    startActivity(Intent(this@MainActivity, HillfortActivity::class.java))
                }

                R.id.nav_account -> {
                    startActivity(Intent(this@MainActivity, AccountActivity::class.java))
                }

                R.id.nav_stats -> {
                    startActivity(Intent(this@MainActivity, StatsActivity::class.java))
                }

                R.id.nav_logout -> {
                    app.activeUser = UserModel()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
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