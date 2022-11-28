package com.example.mobile.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mobile.R
import com.example.mobile.databinding.FragmentLoginBinding
import com.example.mobile.domain.model.LogInForm
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var navigator: NavController
    private val viewModel: LoginViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = Navigation.findNavController(view)
        registerLoginBtn()
        registerSignUpBtn()
    }

    private fun registerSignUpBtn(){
        binding.btnLoginRegister.setOnClickListener {
            navigator.navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun registerLoginBtn(){
        binding.btnLoginLogin.setOnClickListener {

            if(binding.tieLoginId.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(binding.tieLoginPassword.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val id = binding.tieLoginId.text.toString()
                val password = binding.tieLoginPassword.text.toString()
//                viewModel.loginSubmission(LogInForm(id,password))
//                checkLoginRequest()
                navigator.navigate(R.id.action_loginFragment_to_homeFragment)
            }

        }
    }


    private fun checkLoginRequest(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.logInFlag.collect{
                if(it){
                    navigator.navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

}