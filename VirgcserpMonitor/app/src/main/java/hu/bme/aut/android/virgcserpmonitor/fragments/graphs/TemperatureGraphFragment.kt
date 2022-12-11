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
import hu.bme.aut.android.virgcserpmonitor.CserepMain
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentTemperatureGraphBinding
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock


class TemperatureGraphFragment : Fragment() {
    private lateinit var binding: FragmentTemperatureGraphBinding
    private val entries: MutableList<Entry> = ArrayList()
    private var pos: Float = 0.0f
    val lock = ReentrantLock()

    init {
        DataAccess.OnDataAdded.add { state, newPos ->
            run {
                lock.withLock {
                    entries.add(Entry(pos, state.temperature.toFloat(), state.time.toString()))
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

        StatesDataHolder.OnChangeOfTempScale.add { fahrenheit ->
            run {
                entries.clear()
                pos = 0.0f
                initEntries()
                loadData()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemperatureGraphBinding.inflate(inflater, container, false)

        val d = Description()
        d.text = ""
        binding.chartTemperature.description = d
        binding.tvDate.text = ""

        binding.chartTemperature.setOnChartValueSelectedListener(object :
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
            dataset = LineDataSet(
                entries,
                if (StatesDataHolder.fahrenheit)
                    CserepMain.instance.getString(R.string.temperature_with_scale_F)
                else
                    CserepMain.instance.getString(R.string.temperature_with_scale_C)
            )
        }
        binding.chartTemperature.data = LineData(dataset)
        binding.chartTemperature.invalidate() // refresh
    }

    private fun initEntries() {
        StatesDataHolder.states.forEach { state ->
            lock.withLock {
                entries.add(Entry(pos, state.temperature.toFloat(), state.time.toString()))
                entries.sortWith { a, b -> a.x.compareTo(b.x) }
            }
            pos--
        }
    }
}