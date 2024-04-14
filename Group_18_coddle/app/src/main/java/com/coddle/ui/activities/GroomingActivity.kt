package com.coddle.ui.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coddle.R
import com.coddle.adapters.GroomingAdapter
import com.coddle.databinding.ActivityGroomingBinding
import com.coddle.model.Grooming
import com.coddle.repository.GroomRepository
import com.coddle.ui.viewModels.GroomProvider
import com.coddle.ui.viewModels.GroomingViewModel
import com.coddle.util.AppUtil.Companion.dismissProgress
import com.coddle.util.AppUtil.Companion.getNotification
import com.coddle.util.AppUtil.Companion.scheduleNotification
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource
import java.util.*

class GroomingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroomingBinding
    private lateinit var viewModel: GroomingViewModel
    private lateinit var provider: GroomProvider
    private val calendar by lazy { Calendar.getInstance() }
    private val groomingAdapter by lazy { GroomingAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = GroomProvider(GroomRepository(application))
        viewModel = ViewModelProvider(this, provider)[GroomingViewModel::class.java]



        viewModel.addGrooming.observe(this, {
            dismissProgress()
            shortToast("Added")
            scheduleNotification(getNotification("Grooming alert"), (calendar.timeInMillis - (calendar.timeInMillis % 60000)))
            onBackPressed()
        })

        binding.imgAdd.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val view = View.inflate(this, R.layout.add_grooming_layout, null)
            val txtDate = view.findViewById<TextView>(R.id.txtDob)
            val edtDesc = view.findViewById<EditText>(R.id.edtDescription)
            val btnAdd = view.findViewById<Button>(R.id.btnAdd)

            txtDate.setOnClickListener {
                DatePickerDialog(this,
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
                if (edtDesc.text.toString().isBlank() || edtDesc.text.toString().isEmpty()) {
                    edtDesc.error = "Invalid"
                    return@click
                }
                val grooming = Grooming()
                grooming.date = calendar.timeInMillis.toString()
                grooming.description = edtDesc.text.toString()

                alertDialog.cancel()
                showProgress("Adding...")
                viewModel.addGrooming(grooming)
            }

            alertDialog.apply {
                setView(view)
            }.show()

        }

        viewModel.getGrooming.observe(this,{
            when(it.state){
                is Resource.ResourceState.LOADING->{}
                is Resource.ResourceState.SUCCESS->{
                    binding.progressCircular.visibility = INVISIBLE
                    binding.rvGrooming.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@GroomingActivity)
                        adapter = groomingAdapter
                    }
                    groomingAdapter.setData(it.data!!)
                }
                is Resource.ResourceState.ERROR->{
                    binding.progressCircular.visibility = INVISIBLE
                    shortToast(it.message.toString())
                }
            }
        })

        viewModel.getGrooming()


        binding.imgBack.setOnClickListener { onBackPressed() }


    }
}