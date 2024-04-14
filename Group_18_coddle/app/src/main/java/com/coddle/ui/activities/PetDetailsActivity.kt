package com.coddle.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.coddle.R
import com.coddle.databinding.ActivityPetDetailsBinding
import com.coddle.model.User
import com.coddle.repository.UserRepository
import com.coddle.ui.viewModels.PetDetailsProvider
import com.coddle.ui.viewModels.PetDetailsViewModel
import com.coddle.util.AppSharedPreference.Companion.login
import com.coddle.util.AppUtil.Companion.dismissProgress
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource
import java.util.*

class PetDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPetDetailsBinding
    private lateinit var calendar: Calendar
    private var year = 0
    private var day = 0
    private var month = 0
    private var user = User()
    private var password = ""
    private lateinit var viewModel: PetDetailsViewModel
    private lateinit var provider: PetDetailsProvider
    private val genderList = arrayListOf("male","female")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)+1

        "$day/$month/$year".also { binding.txtDob.text = it }

        provider = PetDetailsProvider(UserRepository())
        viewModel = ViewModelProvider(this,provider)[PetDetailsViewModel::class.java]

        setupSpinner()

        val bundle = intent.getBundleExtra("bundle")!!
        user = bundle.getParcelable<User>("user") as User
        password = bundle.getString("password").toString()

        user.dob = binding.txtDob.text.toString()

        binding.txtDob.setOnClickListener {
            DatePickerDialog(this,
                { _, year, month, day ->
                    this.year = year
                    this.month = month+1
                    this.day = day
                    "$day/${this.month}/$year".also { binding.txtDob.text = it }
                    user.dob = binding.txtDob.text.toString()
                }, year,month,day
            ).show()
        }

        viewModel.validate.observe(this,{
            when(it.state){
                is Resource.ResourceState.LOADING->{}
                is Resource.ResourceState.SUCCESS->{
                    showProgress("Loading....")
                    viewModel.addUser(user,password)
                }
                is Resource.ResourceState.ERROR->{
                    when(it.data!!){
                        0->{
                            binding.edtName.error = it.message
                        }
                        1->{
                            binding.edtType.error = it.message
                        }
                        2->{
                            binding.edtBreed.error = it.message
                        }
                    }
                }
            }
        })

        viewModel.addUser.observe(this,{
            when(it.state){
                is Resource.ResourceState.LOADING->{}
                is Resource.ResourceState.SUCCESS->{
                    dismissProgress()
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
                    startActivity(Intent(this,MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                is Resource.ResourceState.ERROR->{
                    dismissProgress()
                    shortToast(it.message.toString())
                }
            }
        })

        binding.btnSave.setOnClickListener {
            user.petName = binding.edtName.text.toString()
            user.type = binding.edtType.text.toString()
            user.breed = binding.edtBreed.text.toString()

            viewModel.validateInputs(
                user.petName,
                user.type,
                user.breed
            )

        }

    }

    private fun setupSpinner() {
        user.gender = genderList[0]
        binding.spinnerGender.apply {
            adapter = ArrayAdapter(this@PetDetailsActivity, R.layout.support_simple_spinner_dropdown_item,genderList)
            onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    user.gender = genderList[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }
}