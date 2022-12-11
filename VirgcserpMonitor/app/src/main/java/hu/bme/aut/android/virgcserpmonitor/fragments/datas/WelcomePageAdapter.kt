package hu.bme.aut.android.virgcserpmonitor.fragments.datas

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WelcomePageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> WelcomeScreenFragment()
        1 -> CserepAllDataFragment()
        else -> WelcomeScreenFragment()
    }

    companion object {
        const val NUM_PAGES = 2
    }
}
