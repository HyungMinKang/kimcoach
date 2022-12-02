package com.example.mobile.ui.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mobile.R
import com.example.mobile.databinding.FragmentRegisterMatchBinding
import com.example.mobile.domain.model.SpinnerType
import java.util.*


class RegisterMatchFragment : Fragment() {

    private lateinit var binding: FragmentRegisterMatchBinding
    private lateinit var regionSpinner: Spinner
    private lateinit var stadiumSpinner: Spinner
    private lateinit var navigator: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register_match, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = Navigation.findNavController(view)
        regionSpinner = binding.spinnerRegisterMatchRegion
        stadiumSpinner = binding.spinnerRegisterMatchStadium
        binding.tieRegisterMatchDate.setOnClickListener {
            showDateDialog()
        }
        setSpinner(listOf("지역선택", "인천", "의정부", "수원", "안양"), SpinnerType.REGION)
        registerMatch()

    }

    private fun registerMatch() {
        binding.btnRegisterMatch.setOnClickListener {
            navigator.navigate(R.id.action_registerMatchFragment_to_navigation_home)
        }
    }

    private fun loadRegionStadium(region: String) {
        when (region) {
            "인천" -> {
                val list =
                    listOf("경기장선택 ", "인천 더배스트 풋볼파크", "케이지풋살아레나 부평점", "현우 풋살 스타디움", "스토래지풋살 인천남동점")
                setSpinner(list, SpinnerType.STADIUM)
            }

            "의정부" -> {
                val list = listOf("경기장선택 ", "도봉산호원풋살장", "허니비 풋살파크", "의정부스카이풋살장", "자일 풋살장")
                setSpinner(list, SpinnerType.STADIUM)
            }
            "수원" -> {
                val list = listOf("경기장선택 ", "수원 월드컵 경기장", "에스빌드 풋살파크", "누누풋살장", "HK풋살파크")
                setSpinner(list, SpinnerType.STADIUM)

            }
            "안양" -> {
                val list = listOf("경기장선택 ", "비플렉스풋볼스타디움", "SNFC 야외풋살장", "안양Master풋살파크")
                setSpinner(list, SpinnerType.STADIUM)
            }
        }


    }

    private fun setSpinner(list: List<String>, type: SpinnerType) {
        val spinner = when (type) {
            SpinnerType.REGION -> binding.spinnerRegisterMatchRegion
            else -> binding.spinnerRegisterMatchStadium
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spiinner_menu, list)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> (view as TextView).setTextColor(Color.GRAY)
                    else -> {
                        (view as TextView).setTextColor(Color.BLACK)
                        checkAllInput()
                    }
                }

                when (type) {
                    SpinnerType.REGION -> {
                        if (position != 0) {
                            loadRegionStadium(list[position])
                        }

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun showDateDialog() {
        val minDate = Calendar.getInstance()
        minDate.set(2022, 11, 29)
        val minYear = minDate.get(Calendar.YEAR)
        val minMonth = minDate.get(Calendar.MONTH)
        val minDay = minDate.get(Calendar.DAY_OF_MONTH)
        val dialog =
            DatePickerDialog(requireContext(), matchDateSetListener, minYear, minMonth, minDay)
        dialog.datePicker.minDate = minDate.timeInMillis
        dialog.show()
    }


    private val matchDateSetListener =
        DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
            val dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            binding.tieRegisterMatchDate.setText(dateString)
            checkAllInput()
        }

    private fun checkAllInput() {
        binding.btnRegisterMatch.isEnabled =
            (binding.tieRegisterMatchDate.text?.isEmpty() == false && !binding.spinnerRegisterMatchRegion.isEmpty() && !binding.spinnerRegisterMatchStadium.isEmpty())
        if (binding.btnRegisterMatch.isEnabled) {
            binding.btnRegisterMatch.setBackgroundResource(R.drawable.btn_radius_green)
        }
    }
}