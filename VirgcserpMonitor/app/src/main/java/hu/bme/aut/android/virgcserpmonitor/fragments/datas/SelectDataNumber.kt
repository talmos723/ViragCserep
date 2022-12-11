package hu.bme.aut.android.virgcserpmonitor.fragments.datas

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.NetworkConstVals
import hu.bme.aut.android.virgcserpmonitor.databinding.SelectDataNumberBinding
import java.util.*

class SelectDataNumber : Fragment() {
    private lateinit var binding: SelectDataNumberBinding

    val c = Calendar.getInstance()
    var fromDate =
        DateData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    var toDate = DateData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    var fromTime = TimeData(c.get(Calendar.HOUR), c.get(Calendar.MINUTE))
    var toTime = TimeData(c.get(Calendar.HOUR), c.get(Calendar.MINUTE))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectDataNumberBinding.inflate(inflater, container, false)
        binding.etNumber.setText(NetworkConstVals.numberOfDatasPerRequest.toString())

        binding.numberCheck.setOnClickListener {
            binding.etNumber.isEnabled = binding.numberCheck.isChecked
        }

        binding.fromCheck.setOnClickListener {
            fromEnabledChecker()
            fromDateValidator(binding.fromDate)
            binding.fromTime?.let { fromTimeValidator(it) }
        }

        binding.toCheck.setOnClickListener {
            toEnabledChecker()
            toDateValidator(binding.toDate)
            binding.toTime?.let { toTimeValidator(it) }
        }

        binding.fromSetNow.setOnClickListener {
            if (binding.toCheck.isChecked) binding.toSetNow.callOnClick()
            fromDate =
                DateData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            fromTime = TimeData(c.get(Calendar.HOUR), c.get(Calendar.MINUTE))
            setFromDateTime()
        }

        binding.toSetNow.setOnClickListener {
            toDate =
                DateData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            toTime = TimeData(c.get(Calendar.HOUR), c.get(Calendar.MINUTE))
            setToDateTime()
        }

        if (NetworkConstVals.fromValue != null) {
            binding.fromCheck.isChecked = true
            fromDate.init(NetworkConstVals.fromValue!!.split("_")[0])
            fromTime.init(NetworkConstVals.fromValue!!.split("_")[1])
        }
        if (NetworkConstVals.toValue != null) {
            binding.toCheck.isChecked = true
            toDate.init(NetworkConstVals.toValue!!.split("_")[0])
            toTime.init(NetworkConstVals.toValue!!.split("_")[1])
        }

        binding.fromDate.setOnDateChangedListener { datePicker, _, _, _ ->
            fromDateValidator(
                datePicker
            )
        }

        binding.fromTime?.setIs24HourView(true)
        binding.fromTime?.setOnTimeChangedListener { timePicker: TimePicker, _, _ ->
            fromTimeValidator(
                timePicker
            )
        }


        binding.toDate.setOnDateChangedListener { datePicker, _, _, _ -> toDateValidator(datePicker) }

        binding.toTime?.setIs24HourView(true)
        binding.toTime?.setOnTimeChangedListener { timePicker: TimePicker, _, _ ->
            toTimeValidator(
                timePicker
            )
        }

        setFromDateTime()
        fromEnabledChecker()
        setToDateTime()
        toEnabledChecker()

        binding.saveButton.setOnClickListener {
            if (binding.numberCheck.isChecked)
                NetworkConstVals.numberOfDatasPerRequest = binding.etNumber.text.toString().toInt()
            else
                NetworkConstVals.numberOfDatasPerRequest = 0
            if (binding.fromCheck.isChecked)
                NetworkConstVals.fromValue = "${fromDate}_${fromTime}"
            else
                NetworkConstVals.fromValue = null
            if (binding.toCheck.isChecked)
                NetworkConstVals.toValue = "${toDate}_${toTime}"
            else
                NetworkConstVals.toValue = null
            DataAccess.refresh()
            navBack()
        }
        binding.cancelButton.setOnClickListener {
            navBack()
        }

        return binding.root
    }

    fun fromEnabledChecker() {
        binding.fromDate.isEnabled = binding.fromCheck.isChecked
        binding.fromTime?.isEnabled = binding.fromCheck.isChecked
        binding.fromSetNow.isEnabled = binding.fromCheck.isChecked
    }

    fun toEnabledChecker() {
        binding.toDate.isEnabled = binding.toCheck.isChecked
        binding.toTime?.isEnabled = binding.toCheck.isChecked
        binding.toSetNow.isEnabled = binding.toCheck.isChecked
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setFromDateTime() {
        binding.fromDate.updateDate(fromDate.year, fromDate.month, fromDate.day)
        binding.fromTime?.hour = fromTime.hour
        binding.fromTime?.minute = fromTime.min
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setToDateTime() {
        binding.toDate.updateDate(toDate.year, toDate.month, toDate.day)
        binding.toTime?.hour = toTime.hour
        binding.toTime?.minute = toTime.min
    }

    fun navBack() {
        findNavController().navigate(
            findNavController().previousBackStackEntry?.destination?.id
                ?: R.id.action_selectDataNumber_to_mainFragment
        )
    }

    fun fromDateValidator(datePicker: DatePicker) {
        fromDate.year = datePicker.year
        fromDate.month = datePicker.month
        fromDate.day = datePicker.dayOfMonth
        if (binding.toCheck.isChecked && fromDate > toDate) {
            fromDate = toDate.copy()
            datePicker.updateDate(fromDate.year, fromDate.month, fromDate.day)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun fromTimeValidator(timePicker: TimePicker) {
        fromTime.hour = timePicker.hour
        fromTime.min = timePicker.minute
        if (binding.toCheck.isChecked && fromDate == toDate && fromTime > toTime) {
            fromTime = toTime.copy()
            timePicker.hour = fromTime.hour
            timePicker.minute = fromTime.min
        }
    }

    fun toDateValidator(datePicker: DatePicker) {
        toDate.year = datePicker.year
        toDate.month = datePicker.month
        toDate.day = datePicker.dayOfMonth
        if (binding.fromCheck.isChecked && fromDate > toDate) {
            toDate = fromDate.copy()
            datePicker.updateDate(toDate.year, toDate.month, toDate.day)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toTimeValidator(timePicker: TimePicker) {
        toTime.hour = timePicker.hour
        toTime.min = timePicker.minute
        if (binding.fromCheck.isChecked && fromDate == toDate && fromTime > toTime) {
            toTime = fromTime.copy()
            timePicker.hour = toTime.hour
            timePicker.minute = toTime.min
        }
    }

    data class DateData(
        var year: Int,
        var month: Int,
        var day: Int
    ) {
        operator fun compareTo(other: DateData): Int {
            var res = year.compareTo(other.year)
            if (res == 0) {
                res = month.compareTo(other.month)
                if (res == 0) {
                    res = day.compareTo(other.day)
                }
            }
            return res
        }

        fun init(str: String) {
            val strArr = str.split("-")
            year = strArr[0].toInt()
            month = strArr[1].toInt() - 1
            day = strArr[2].toInt()
        }

        override fun toString(): String {
            return String.format("%02d-%02d-%02d", year, month + 1, day)
        }
    }

    data class TimeData(
        var hour: Int,
        var min: Int
    ) {
        operator fun compareTo(other: TimeData): Int {
            var res = hour.compareTo(other.hour)
            if (res == 0) {
                res = min.compareTo(other.min)
            }
            return res
        }

        fun init(str: String) {
            val strArr = str.split(":")
            hour = strArr[0].toInt()
            min = strArr[1].toInt()
        }

        override fun toString(): String {
            return String.format("%02d:%02d", hour, min)
        }
    }
}