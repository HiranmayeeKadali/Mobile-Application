package com.coddle.ui.activities

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coddle.R
import com.coddle.adapters.WalkingAdapter
import com.coddle.databinding.ActivityWalkingBinding
import com.coddle.model.Walking
import com.coddle.repository.WalkRepository
import com.coddle.ui.viewModels.WalkProvider
import com.coddle.ui.viewModels.WalkingViewModel
import com.coddle.util.AppUtil
import com.coddle.util.AppUtil.Companion.getNotification
import com.coddle.util.AppUtil.Companion.scheduleNotification
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource
import java.util.*

class WalkingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkingBinding
    private lateinit var viewModel: WalkingViewModel
    private lateinit var provider: WalkProvider
    private val calendar by lazy { Calendar.getInstance() }
    private val walkingAdapter by lazy { WalkingAdapter() }
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = WalkProvider(WalkRepository(application))
        viewModel = ViewModelProvider(this, provider)[WalkingViewModel::class.java]

        permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            if (it){
                shortToast("Permission granted")
            }else{
                shortToast("Permission denied")
            }
        }

        walkingAdapter.setOnclickListener {
           if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
               startActivity(
                   Intent(this, StartWalkingActivity::class.java)
                       .putExtra("bundle", Bundle().apply {
                           putParcelable("walking", it)
                       })
               )
            else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionResultLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
        }

        viewModel.addWalking.observe(this, {
            AppUtil.dismissProgress()
            shortToast("Added")
            scheduleNotification(
                getNotification("Walking alert"),
                (calendar.timeInMillis - (calendar.timeInMillis % 60000))
            )
            onBackPressed()
        })

        binding.imgAdd.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val view = View.inflate(this, R.layout.add_walking_layout, null)
            val txtDate = view.findViewById<TextView>(R.id.txtDob)
            val btnAdd = view.findViewById<Button>(R.id.btnAdd)

            txtDate.setOnClickListener {
                DatePickerDialog(
                    this,
                    { _, year, month, day ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        "$day/${month+1}/$year".also { txtDate.text = it }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            btnAdd.setOnClickListener click@{
                if (txtDate.text.toString() == "Select date") {
                    shortToast("Select data")
                    return@click
                }
                val walking = Walking()
                walking.date = calendar.timeInMillis.toString()

                alertDialog.cancel()
                showProgress("Adding...")
                viewModel.addWalking(walking)
            }

            alertDialog.apply {
                setView(view)
            }.show()

        }

        viewModel.getWalking.observe(this, {
            when (it.state) {
                is Resource.ResourceState.LOADING -> {}
                is Resource.ResourceState.SUCCESS -> {
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.rvWalking.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@WalkingActivity)
                        adapter = walkingAdapter
                    }
                    walkingAdapter.setData(it.data!!)
                }
                is Resource.ResourceState.ERROR -> {
                    binding.progressCircular.visibility = View.INVISIBLE
                    shortToast(it.message.toString())
                }
            }
        })

        viewModel.getWalking()


        binding.imgBack.setOnClickListener { onBackPressed() }


    }
}