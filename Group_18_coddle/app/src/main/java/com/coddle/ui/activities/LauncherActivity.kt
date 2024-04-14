package com.coddle.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coddle.databinding.ActivityLauncherBinding
import com.coddle.ui.viewModels.LauncherProvider
import com.coddle.ui.viewModels.LauncherViewModel

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    private lateinit var viewModel: LauncherViewModel
    private lateinit var provider: LauncherProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = LauncherProvider(application)
        viewModel = ViewModelProvider(this, provider)[LauncherViewModel::class.java]

        viewModel.splash()

        viewModel.login.observe(this, {
            if (it)
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, LoginActivity::class.java))
        })


    }
}