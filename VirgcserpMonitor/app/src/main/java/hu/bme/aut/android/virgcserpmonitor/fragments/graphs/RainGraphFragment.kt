package hu.bme.aut.android.virgcserpmonitor.fragments.graphs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentRainGraphBinding
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RainGraphFragment : Fragment() {
    private lateinit var binding: FragmentRainGraphBinding
    private val entries: MutableList<PieEntry> = ArrayList()
    val lock = ReentrantLock()

    init {
        DataAccess.OnDataAdded.add { state, newPos ->
            run {
                lock.withLock {
                    if (state.rain == 1) {
                        entries.add(PieEntry(1f, state.time))
                    }
                }
            }
        }

        DataAccess.OnRefres.add {
            entries.clear()
        }
        DataAccess.OnDataLoaded.add {
            activity?.runOnUiThread {
                loadData()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRainGraphBinding.inflate(inflater, container, false)

        val d = Description()
        d.text = ""
        binding.chartRain.description = d
        binding.tvDate.text = ""

        binding.chartRain.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {

            override fun onNothingSelected() {
                binding.tvDate.text = ""
            }

            override fun onValueSelected(e: Entry, h: Highlight) {
                val date = e.data.toString()
                binding.tvDate.text = date
            }
        })

        if (StatesDataHolder.getSize() == 0)
            DataAccess.loadStateData()
        else if (entries.size == 0) {
            initEntries()
            loadData()
        }

        return binding.root
    }

    private fun loadData() {
        val dataset: PieDataSet
        lock.withLock {
            dataset = PieDataSet(entries, getString(R.string.raining))
        }
        if (entries.size == 0) {
            binding.tvDate.text = getString(R.string.noRainInTimePeriod)
        }
        binding.chartRain.data = PieData(dataset)
        binding.chartRain.invalidate() // refresh
    }

    private fun initEntries() {
        StatesDataHolder.states.forEach { state ->
            lock.withLock {
                if (state.rain == 1) {
                    entries.add(PieEntry(1f, state.time))
                }
            }
        }
    }
}