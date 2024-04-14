package com.coddle.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coddle.databinding.ActivityLoginBinding
import com.coddle.repository.UserRepository
import com.coddle.ui.viewModels.LoginProvider
import com.coddle.ui.viewModels.LoginViewModel
import com.coddle.util.AppSharedPreference.Companion.login
import com.coddle.util.AppUtil.Companion.dismissProgress
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var provider: LoginProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = LoginProvider((UserRepository()))
        viewModel = ViewModelProvider(this, provider)[LoginViewModel::class.java]


        viewModel.validate.observe(this, {
            when (it.state) {
                is Resource.ResourceState.LOADING -> {}
                is Resource.ResourceState.SUCCESS -> {
                    viewModel.login(
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString()
                    )
                }
                is Resource.ResourceState.ERROR -> {
                    when (it.data!!) {
                        0 -> binding.edtEmail.error = it.message
                        1 -> binding.edtPassword.error = it.message
                    }
                }
            }
        })

        viewModel.login.observe(this, {
            when (it.state) {
                is Resource.ResourceState.LOADING -> {
                    showProgress("Loading....")
                }
                is Resource.ResourceState.SUCCESS -> {
                    dismissProgress()
                    val user = it.data!!
                    login(
                        user.email,
                        user.username,
                        user.userId,
                        user.petName,
                        user.breed,
                        user.dob,
                        user.petImage,
                        user.type
                    )
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                is Resource.ResourceState.ERROR -> {
                    dismissProgress()
                    shortToast(it.message!!)
                }
            }
        })

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            viewModel.validateInputs(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString()
            )
        }

    }
}