package com.coddle.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coddle.databinding.ActivityRegisterBinding
import com.coddle.model.User
import com.coddle.repository.UserRepository
import com.coddle.ui.viewModels.RegisterProvider
import com.coddle.ui.viewModels.RegisterViewModel
import com.coddle.util.AppUtil.Companion.dismissProgress
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var provider: RegisterProvider
    private var user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = RegisterProvider(application, UserRepository())
        viewModel = ViewModelProvider(this, provider)[RegisterViewModel::class.java]


        viewModel.validate.observe(this, @ExperimentalCoroutinesApi {
            when (it.state) {
                is Resource.ResourceState.SUCCESS -> {
                    viewModel.isEmailExist(binding.edtEmail.text.toString())
                }
                is Resource.ResourceState.ERROR -> {
                    when (it.data) {
                        0 -> binding.edtName.error = it.message
                        1 -> binding.edtEmail.error = it.message
                        2 -> binding.edtPassword.error = it.message
                    }
                }
                is Resource.ResourceState.LOADING -> {}
            }
        })

        viewModel.isEmailExist.observe(this, {
            when (it.state) {
                is Resource.ResourceState.LOADING -> {
                    showProgress("Loading...")
                }
                is Resource.ResourceState.SUCCESS -> {
                    dismissProgress()
                    when (it.data!!) {
                        true -> {
                            shortToast("Email already registered")
                            binding.edtEmail.error = "Email already registered"
                        }
                        false -> {
                            user.email = binding.edtEmail.text.toString()
                            user.username = binding.edtName.text.toString()
                            startActivity(Intent(this, PetDetailsActivity::class.java)
                                .putExtra("bundle", Bundle().apply {
                                    putParcelable("user", user)
                                    putString("password", binding.edtPassword.text.toString())
                                })
                            )
                        }
                    }
                }
                is Resource.ResourceState.ERROR -> {
                    dismissProgress()
                    shortToast(it.message.toString())
                }
            }
        })

        binding.btnSignup.setOnClickListener {
            viewModel.validateInputs(
                binding.edtName.text.toString(),
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString()
            )
        }

    }


}