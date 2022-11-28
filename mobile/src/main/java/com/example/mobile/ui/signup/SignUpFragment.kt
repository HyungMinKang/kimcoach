package com.example.mobile.ui.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.DisplayNameSources.EMAIL
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mobile.R
import com.example.mobile.databinding.FragmentSignUpBinding
import com.example.mobile.domain.model.SignUpForm
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navigator: NavController
    private val viewModel: SignUpViewModel by inject()
    private var idFlag = false
    private var passwordFlag = false
    private var passwordConfirmFlag = false
    private var nameFlag = false
    private var birthDateFlag = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = Navigation.findNavController(view)
        registerSignUpBtn()
        registerUserInputValidation()

    }

    private fun registerUserInputValidation() {
        registerPasswordInputValidation()
        registerPasswordConfirmInputValidation()
        registerNameInputValidation()
        registerBirthDateInputValidation()
    }

    private fun registerSignUpBtn() {
        binding.btnSignUpComplete.setOnClickListener {
            navigator.navigate(R.id.action_signUpFragment_to_loginFragment)
//            viewModel.requestSignUp(makeSignUpForm())
//            checkSignUpRequest()
        }
    }

    private fun checkSignUpRequest(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.signUpFlag.collect{
                if(it){
                    navigator.navigate(R.id.action_signUpFragment_to_loginFragment)
                }
            }
        }
    }

    private fun makeSignUpForm(): SignUpForm {
        val id = binding.tieSignId.text.toString()
        val password = binding.tieSignPassword.text.toString()
        val name = binding.tieSignName.text.toString()
        val birthDate = LocalDate.parse(binding.tieSignBirthdate.text.toString(), DateTimeFormatter.ISO_DATE)
        return SignUpForm(id, password, name, birthDate)
    }

    private fun registerBirthDateInputValidation() {
        binding.tieSignBirthdate.setOnClickListener {
            showDateDialog()
        }
    }

    private fun registerPasswordInputValidation() {
        binding.tieSignPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(input: Editable?) {
                input?.let {
                    if (input.isEmpty()) {
                        binding.tieSignPassword.error =
                            getString(R.string.signup_error_password_validation_empty_input)
                        passwordFlag = false
                    } else if (input.length < 8) {
                        binding.tieSignPassword.error =
                            getString(R.string.signup_error_password_validation_min_length)
                        passwordFlag = false
                    } else if (!Pattern.matches("^[a-zA-Z0-9]*.{8,20}$", it)) {
                        binding.tieSignPassword.error =
                            getString(R.string.singup_error_password_validation_special_char)
                        passwordFlag = false
                    } else {
                        binding.tieSignPassword.error = null
                        passwordFlag = true
                    }
                    checkInputValidation()
                }
            }
        })
    }

    private fun registerPasswordConfirmInputValidation() {
        binding.tieSignPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(input: Editable?) {
                input?.let {
                    if (input.toString() != binding.tieSignPassword.text.toString()) {
                        binding.tieSignPasswordConfirm.error =
                            getString(R.string.signup_error_password_confirm_validation)
                        passwordConfirmFlag = false
                    } else {
                        binding.tieSignPasswordConfirm.error = null
                        passwordConfirmFlag = true
                    }
                    checkInputValidation()
                }
            }
        })
    }

    private fun registerIDInputValidation() {
        binding.tieSignId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(input: Editable?) {
                input?.let {
                    if (input.isEmpty()) {
                        binding.tieSignId.error =
                            getString(R.string.signup_error_id_validation_empty_input)
                        idFlag = false
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                        binding.tieSignId.error =
                            getString(R.string.signup_error_id_validation_email_form)
                        idFlag = false
                    } else {
                        binding.tieSignId.error = null
                        idFlag = true
                    }
                    checkInputValidation()
                }
            }
        })
    }

    private fun registerNameInputValidation() {
        binding.tieSignName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(input: Editable?) {
                input?.let {
                    if (input.isEmpty()) {
                        binding.tieSignName.error = getString(R.string.signup_error_name_validation)
                        nameFlag = false
                    } else {
                        binding.tieSignName.error = null
                        nameFlag = true
                    }
                    checkInputValidation()
                }
            }

        })
    }

    private fun showDateDialog() {
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()
        maxDate.set(2009, 11, 31)
        minDate.set(1923, 0, 1)
        val maxYear = maxDate.get(Calendar.YEAR)
        val maxMonth = maxDate.get(Calendar.MONTH)
        val maxDay = maxDate.get(Calendar.DAY_OF_MONTH)
        val dialog =
            DatePickerDialog(requireContext(), birthDateSetListener, maxYear, maxMonth, maxDay)
        dialog.datePicker.minDate = minDate.timeInMillis
        dialog.datePicker.maxDate = maxDate.timeInMillis
        dialog.show()
    }


    private val birthDateSetListener =
        DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
            val dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            binding.tieSignBirthdate.setText(dateString)
            birthDateFlag = true
            checkInputValidation()
        }

    private fun checkInputValidation() {
        if (idFlag && passwordFlag && passwordConfirmFlag && nameFlag && birthDateFlag) {
            binding.btnSignUpComplete.background =
                requireContext().resources.getDrawable(R.drawable.btn_radius_green)
            binding.btnSignUpComplete.isEnabled = true
        } else {
            binding.btnSignUpComplete.setBackgroundColor(R.color.grey1)
            binding.btnSignUpComplete.isEnabled = false
        }
    }

}