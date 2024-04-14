package com.coddle.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coddle.databinding.ActivitySettingsBinding
import com.coddle.model.User
import com.coddle.repository.UserRepository
import com.coddle.ui.viewModels.SettingsProvider
import com.coddle.ui.viewModels.SettingsViewModel
import com.coddle.util.AppSharedPreference.Companion.getPetBreed
import com.coddle.util.AppSharedPreference.Companion.getPetDob
import com.coddle.util.AppSharedPreference.Companion.getPetName
import com.coddle.util.AppSharedPreference.Companion.getPetType
import com.coddle.util.AppSharedPreference.Companion.getUserId
import com.coddle.util.AppSharedPreference.Companion.getUsername
import com.coddle.util.AppSharedPreference.Companion.updateUser
import com.coddle.util.AppUtil.Companion.dismissProgress
import com.coddle.util.AppUtil.Companion.shortToast
import com.coddle.util.AppUtil.Companion.showProgress
import com.coddle.util.Resource
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var year = 0
    private var day = 0
    private var month = 0
    private var user = User()
    private lateinit var calendar: Calendar
    private lateinit var viewModel: SettingsViewModel
    private lateinit var provider: SettingsProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = SettingsProvider(UserRepository())
        viewModel = ViewModelProvider(this, provider)[SettingsViewModel::class.java]

        setUI()

        viewModel.validate.observe(this,{
            when(it.state){
                is Resource.ResourceState.LOADING->{}
                is Resource.ResourceState.SUCCESS->{
                    showProgress("Updating...")
                    viewModel.updateUser(user)
                }
                is Resource.ResourceState.ERROR->{
                    when(it.data!!){
                        0->{
                            binding.edtName.error = it.message
                        }
                        1->{
                            binding.edtPetName.error = it.message
                        }
                        2->{
                            binding.edtType.error = it.message
                        }
                        3->{
                            binding.edtBreed.error = it.message
                        }
                    }
                }
            }
        })

        viewModel.update.observe(this, {
            updateUser(
                    user.username,
                    user.petName,
                    user.breed,
                    user.type,
                    user.dob
            )
            shortToast("Updated")
            dismissProgress()
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        })

        binding.btnSave.setOnClickListener {
            user.breed = binding.edtBreed.text.toString()
            user.username = binding.edtName.text.toString()
            user.petName = binding.edtPetName.text.toString()
            user.type = binding.edtType.text.toString()
            user.breed = binding.edtBreed.text.toString()
            user.dob = binding.txtDob.text.toString()

            viewModel.validateInputs(user)
        }

        binding.imgBack.setOnClickListener { onBackPressed() }

    }

    private fun setUI() {

        binding.edtName.setText(getUsername())
        binding.edtPetName.setText(getPetName())
        binding.edtBreed.setText(getPetBreed())
        binding.edtType.setText(getPetType())
        binding.txtDob.text = getPetDob()

        user.breed = binding.edtBreed.text.toString()
        user.username = binding.edtName.text.toString()
        user.petName = binding.edtPetName.text.toString()
        user.type = binding.edtType.text.toString()
        user.breed = binding.edtBreed.text.toString()
        user.userId = getUserId()


        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH) + 1


        binding.txtDob.setOnClickListener {
            DatePickerDialog(this,
                    { _, year, month, day ->
                        this.year = year
                        this.month = month + 1
                        this.day = day
                        "$day/${this.month}/$year".also { binding.txtDob.text = it }
                        user.dob = binding.txtDob.text.toString()
                    }, year, month, day
            ).show()
        }


    }
}