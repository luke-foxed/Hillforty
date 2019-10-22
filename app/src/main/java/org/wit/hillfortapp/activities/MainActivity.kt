package org.wit.hillfortapp.activities
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.wit.hillfortapp.R

open class MainActivity : AppCompatActivity() {

    lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    // Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@MainActivity, HillfortListActivity::class.java))
                }
                R.id.nav_wallet -> {
//                   Toast.makeText(this, "Wallet", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@MainActivity, TestActivity::class.java))
                }
                R.id.nav_offer -> {
                    Toast.makeText(this, "Offer", Toast.LENGTH_LONG).show()
                }
                R.id.nav_setting -> {
                    Toast.makeText(this, "Setting", Toast.LENGTH_LONG).show()
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }
    }

    //appbar - toolbar button click
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