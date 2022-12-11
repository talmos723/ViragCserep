package hu.bme.aut.android.virgcserpmonitor.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.virgcserpmonitor.CserepMain
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.State
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.StateViewBinding
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class StateAdapter : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {
    val UIlock = ReentrantLock()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.state_view, parent, false)
        return StateViewHolder(view)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        val item = StatesDataHolder[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = StatesDataHolder.states.size

    fun removeState(position: Int) {
        notifyItemRemoved(position)
        //Log.i("asdasd", "removeAllStates: $position")
        if (position < this.itemCount) {
            notifyItemRangeChanged(position, this.itemCount - position)
        }
    }

    fun removeAllStates() {
        this.notifyItemRangeRemoved(0, this.itemCount)
    }

    inner class StateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = StateViewBinding.bind(itemView)
        var item: State? = null

        init {
            StatesDataHolder.OnChangeOfTempScale.add {
                fahrenheit -> run {
                    binding.tvStateTemperature.text =
                        if (item!!.fahrenheit)
                            CserepMain.instance.getString(R.string.temperature_parametrized_F, item!!.temperature)
                        else
                            CserepMain.instance.getString(R.string.temperature_parametrized_C, item!!.temperature)
                }
            }
        }

        fun bind(newState: State?) {
            item = newState
            binding.tvStateTimeStamp.text = CserepMain.instance.getString(R.string.time_parametrized, item!!.time.toString())
            binding.tvStateTemperature.text =
                if (item!!.fahrenheit)
                    CserepMain.instance.getString(R.string.temperature_parametrized_F, item!!.temperature)
                else
                    CserepMain.instance.getString(R.string.temperature_parametrized_C, item!!.temperature)
            binding.tvStateHumidity.text = CserepMain.instance.getString(R.string.humidity_parametrized, item!!.humidity)
            binding.tvStateWaterLevel.text = CserepMain.instance.getString(R.string.waterLevel_parametrized, item!!.waterLevel)
            binding.tvStateLight.text = CserepMain.instance.getString(R.string.light_parametrized, item!!.light)
            binding.tvStateSoilMoisture.text = CserepMain.instance.getString(R.string.soilMoisture_parametrized, item!!.soilMoisture)
            binding.tvStateRain.text =
                if (item!!.rain != 0)
                    CserepMain.instance.getString(R.string.raining)
                else
                    CserepMain.instance.getString(R.string.not_raining)
            binding.ivDayLightSymbol.setImageResource(item!!.getDayLightSymbolResource())
        }
    }
}