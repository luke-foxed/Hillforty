package org.wit.hillfortapp.views.more

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.wit.hillfortapp.views.more.fragments.about.AboutFragment
import org.wit.hillfortapp.views.more.fragments.account.AccountFragment
import org.wit.hillfortapp.views.more.fragments.stats.StatsFragment

class ExtraAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AccountFragment()
            1 -> AboutFragment()
            2 -> StatsFragment()
            else -> {
                return AccountFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "First Tab"
            1 -> "Second Tab"
            2 -> "Third, sTab"
            else -> {
                return "Third Tab"
            }
        }
    }
}