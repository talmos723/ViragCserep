package hu.bme.aut.android.virgcserpmonitor.fragments.graphs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class GraphsPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = when (position.mod(6)) {
        0 -> TemperatureGraphFragment()
        1 -> HumidityGraphFragment()
        2 -> LightGraphFragment()
        3 -> SoilMoistureGraphFragment()
        4 -> RainGraphFragment()
        5 -> WaterLevelGraphFragment()
        else -> TemperatureGraphFragment()
    }

    companion object {
        const val NUM_PAGES = Int.MAX_VALUE
    }
}
