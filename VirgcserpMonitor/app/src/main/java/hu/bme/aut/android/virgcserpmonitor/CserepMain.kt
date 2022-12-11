package hu.bme.aut.android.virgcserpmonitor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.NetworkConstVals
import hu.bme.aut.android.virgcserpmonitor.data.Preferences
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.CserepMainBinding
import hu.bme.aut.android.virgcserpmonitor.fragments.datas.SelectDataNumber

class CserepMain : AppCompatActivity() {
    private lateinit var binding: CserepMainBinding

    companion object {
        lateinit var instance: CserepMain private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CserepMainBinding.inflate(layoutInflater)

        instance = this

        Preferences.initPreferences(this)
        DataAccess.refresh()

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (StatesDataHolder.fahrenheit)
            menu.findItem(R.id.menu_fahrenheit).isChecked = true
        else
            menu.findItem(R.id.menu_celsius).isChecked = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.iSelectDataNum -> {
                try {
                    findNavController(R.id.vpMain).navigate(R.id.action_mainFragment_to_selectDataNumber)
                } catch (e: Exception) {
                    try {
                        findNavController(R.id.vpGraphs).navigate(R.id.action_graphsFragment_to_selectDataNumber)
                    } catch (e: Exception) {
                        throw e
                    }
                }
                true
            }
            R.id.iRefresh -> {
                DataAccess.refresh()
                true
            }
            R.id.menu_celsius -> {
                item.isChecked = true
                StatesDataHolder.fahrenheit = false
                true
            }
            R.id.menu_fahrenheit -> {
                item.isChecked = true
                StatesDataHolder.fahrenheit = true
                true
            }
            R.id.iIp -> {
                val et = EditText(this)
                et.setText(NetworkConstVals.ip)
                val regexip: Regex = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\$".toRegex()
                AlertDialog.Builder(this)
                    .setTitle(R.string.select_ip)
                    .setView(et)
                    .setPositiveButton(R.string.save) { dialogInterface, i ->
                        if (et.text.matches(regexip)) {
                            NetworkConstVals.ip = et.text.toString()
                            DataAccess.refresh()
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}