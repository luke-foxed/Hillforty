package org.wit.hillfortapp.views.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.account.AccountView
import org.wit.hillfortapp.views.hillfortlist.HillfortListView
import org.wit.hillfortapp.views.map.HillfortMapsView


open class MainView : AppCompatActivity(), AnkoLogger {

    lateinit var bottomNavBar: BottomNavigationView
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
        bottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        bottomNavBar.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_home -> {
//                    startActivity(
//                        Intent(this@MainView, MainView::class.java),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//
//                }
//                R.id.navigation_hillforts -> {
//
//                    startActivity(
//                        Intent(this@MainView, HillfortListView::class.java),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//                }
//                R.id.navigation_map -> {
//                    it.isChecked = true
////                    viewPager.setCurrentItem(2, true)
//                    startActivity(
//                        Intent(this@MainView, HillfortMapsView::class.java),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//                }
//                R.id.navigation_account -> {
////                    viewPager.setCurrentItem(3, true)
//                    startActivity(
//                        Intent(this@MainView, AccountView::class.java),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//                }
//            }
//            true
//        }
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(
                        Intent(this, MainView::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                    bottomNavBar.menu.findItem(R.id.navigation_home).isChecked = true

                }
                R.id.navigation_hillforts -> {
                    startActivity(
                        Intent(this, HillfortListView::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                    //bottomNavBar.menu.findItem(R.id.navigation_hillforts).isChecked = true
                }
                R.id.navigation_map -> {
                    startActivity(
                        Intent(this, HillfortMapsView::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                }
                R.id.navigation_account -> {
                    startActivity(
                        Intent(this, AccountView::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                }
        }
        true
    }

    open fun setNavigationBarItem() {}

}