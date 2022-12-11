package hu.bme.aut.android.virgcserpmonitor.fragments.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentWaterLevelGraphBinding
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class WaterLevelGraphFragment : Fragment() {
    private lateinit var binding: FragmentWaterLevelGraphBinding
    private val entries: MutableList<Entry> = ArrayList()
    private var pos: Float = 0.0f
    val lock = ReentrantLock()

    init {
        DataAccess.OnDataAdded.add { state, newPos ->
            run {
                lock.withLock {
                    entries.add(Entry(pos, state.waterLevel.toFloat(), state.time.toString()))
                    entries.sortWith { a, b -> a.x.compareTo(b.x) }
                }
                pos--
            }
        }

        DataAccess.OnRefres.add {
            entries.clear()
            pos = 0.0f
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
        binding = FragmentWaterLevelGraphBinding.inflate(inflater, container, false)

        val d = Description()
        d.text = ""
        binding.chartWaterLevel.description = d
        binding.tvDate.text = ""

        binding.chartWaterLevel.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {

            override fun onNothingSelected() {
                binding.tvDate.text = ""
            }

            override fun onValueSelected(e: Entry, h: Highlight) {
                val date = e.data as String
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
        val dataset: LineDataSet
        lock.withLock {
            dataset = LineDataSet(entries, getString(R.string.water_level_with_scale))
        }
        binding.chartWaterLevel.data = LineData(dataset)
        binding.chartWaterLevel.invalidate() // refresh
    }

    private fun initEntries() {
        StatesDataHolder.states.forEach { state ->
            lock.withLock {
                entries.add(Entry(pos, state.waterLevel.toFloat(), state.time.toString()))
                entries.sortWith { a, b -> a.x.compareTo(b.x) }
            }
            pos--
        }
    }
}