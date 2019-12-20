package org.wit.hillfortapp.views.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R


open class MainView : AppCompatActivity(), AnkoLogger {

    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var viewPager: ViewPager
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_main)
        layoutInflater.inflate(R.layout.activity_main, content_frame)

        app = application as MainApp

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (intent.hasExtra("user")) {
            val user: FirebaseUser = intent.extras?.getParcelable("user")!!
            mainActivityUsernameText.text = user.email!!.split('@')[0]
        }

        bottomNavBar = findViewById(R.id.bottom_navigation)

        viewPager = findViewById<View>(R.id.content_frame) as ViewPager
        viewPager.adapter = CustomPagerAdapter(this)

        bottomNavBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    item.isChecked = true
                    viewPager.setCurrentItem(0, true)
                }
                R.id.navigation_hillforts -> {
                    item.isChecked = true
                    viewPager.setCurrentItem(1, true)
                }
                R.id.navigation_map -> {
                    item.isChecked = true
                    viewPager.setCurrentItem(2, true)
                }
                R.id.navigation_account -> {
                    item.isChecked = true
                    viewPager.setCurrentItem(3, true)
                }
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                mDrawerLayout.openDrawer(GravityCompat.START)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
        return true
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
    }
}