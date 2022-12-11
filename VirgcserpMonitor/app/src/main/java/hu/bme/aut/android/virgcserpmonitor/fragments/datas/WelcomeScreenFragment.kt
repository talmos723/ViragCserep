package hu.bme.aut.android.virgcserpmonitor.fragments.datas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.virgcserpmonitor.CserepMain
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentWelcomeScreenBinding
import kotlin.math.abs

class WelcomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeScreenBinding

    init {
        DataAccess.OnDataLoaded.add {
            activity?.runOnUiThread {
                resetData()
            }
        }

        StatesDataHolder.OnChangeOfTempScale.add { fahrenheit ->
            run {
                binding.tvTemperature.text =
                    if (fahrenheit)
                        getString(
                            R.string.only_temperature_parametrized_F,
                            StatesDataHolder[0].temperature
                        )
                    else
                        getString(
                            R.string.only_temperature_parametrized_C,
                            StatesDataHolder[0].temperature
                        )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentWelcomeScreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetData()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_graphsFragment)
        }

        binding.thermometer.setImageResource(R.drawable.ic_thermometer)
    }

    private fun resetData() {
        if (StatesDataHolder.getSize() > 0) {
            binding.tvTemperature.text =
                if (StatesDataHolder.fahrenheit)
                    getString(
                        R.string.only_temperature_parametrized_F,
                        StatesDataHolder[0].temperature
                    )
                else
                    getString(
                        R.string.only_temperature_parametrized_C,
                        StatesDataHolder[0].temperature
                    )

            //Százalékosan beállítjuk a segédvonalat a hőmérsékletnek, arányosan a min és max hőmérséklethez. A hőmérő, amihez igazítjuk az oldal 66%-át foglalja el, így ehhez arányosítunk
            binding.temperatureLevelLine.setGuidelinePercent(
                1f - 0.66f * (
                        if (StatesDataHolder.fahrenheit) (StatesDataHolder[0].temperature - MIN_TEMPERATURE_F) / abs(
                            MAX_TEMPERATURE_F - MIN_TEMPERATURE_F
                        )
                        else (StatesDataHolder[0].temperature - MIN_TEMPERATURE_C) / abs(
                            MAX_TEMPERATURE_C - MIN_TEMPERATURE_C
                        )
                        )
            )

            binding.ivDayLightSymbol.setImageResource(StatesDataHolder[0].getDayLightSymbolResource())
        }
    }

    companion object {
        var MAX_TEMPERATURE_C = 50f
        var MIN_TEMPERATURE_C = -30f
        var MAX_TEMPERATURE_F = 122f
        var MIN_TEMPERATURE_F = -22f
    }
}