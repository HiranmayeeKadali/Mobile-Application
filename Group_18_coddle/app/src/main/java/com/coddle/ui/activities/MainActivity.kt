package com.coddle.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.coddle.R
import com.coddle.databinding.ActivityMainBinding
import com.coddle.model.Report
import com.coddle.ui.viewModels.MainViewModel
import com.coddle.util.AppSharedPreference.Companion.getImage
import com.coddle.util.AppSharedPreference.Companion.getPetBreed
import com.coddle.util.AppSharedPreference.Companion.getPetDob
import com.coddle.util.AppSharedPreference.Companion.getPetName
import com.coddle.util.AppSharedPreference.Companion.getUserId
import com.coddle.util.AppUtil.Companion.shortToast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setUI()


        permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            if (it){
                shortToast("Permission granted")
            }else{
                shortToast("Permission denied")
            }
        }

        binding.imgMenu.setOnClickListener {
            val popupMenu = PopupMenu(this,it)
            popupMenu.inflate(R.menu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {menu->
                when(menu.itemId){
                    R.id.settings->{
                        startActivity(Intent(this,SettingsActivity::class.java))
                    }
                    R.id.blog->{
                        startActivity(Intent(this,BlogActivity::class.java))
                    }
                    R.id.report->{
//                        val view = View.inflate(this,R.layout.report_layout,null)
//                        val edtTitle = view.findViewById<EditText>(R.id.edtTitle)
//                        val edtDesc = view.findViewById<EditText>(R.id.edtDescription)
//                        val btnReport = view.findViewById<Button>(R.id.btnReport)
//                        val alertDialog = AlertDialog.Builder(this).apply {
//                            setView(view)
//                        }.create()
//
//                        btnReport.setOnClickListener click@{
//                            if (edtTitle.text.isEmpty() || edtTitle.text.isBlank()){
//                               edtTitle.error = "Enter title"
//                               return@click
//                            }
//                            if (edtDesc.text.isEmpty() || edtDesc.text.isBlank()){
//                                edtTitle.error = "Enter description"
//                                return@click
//                            }
//                            val report = Report()
//                            report.userId = getUserId()
//                            report.description = edtDesc.text.toString()
//                            report.title = edtTitle.text.toString()
//
//                            alertDialog.dismiss()
//                            viewModel.report(report)
//                            shortToast("Reported")
//                        }
//
//                        alertDialog.show()

                        startActivity(Intent().apply {
                            action = Intent.ACTION_SENDTO
                            data = Uri.parse("mailto:Hiranmayeekadali22@gmail.com")
                            putExtra(Intent.EXTRA_EMAIL,"Hiranmayeekadali22@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT,"Report bug")
                        })

                    }
                    R.id.help->{
                        startActivity(Intent().apply {
                            action = Intent.ACTION_SENDTO
                            data = Uri.parse("mailto:Hiranmayeekadali22@gmail.com")
                            putExtra(Intent.EXTRA_EMAIL,"Hiranmayeekadali22@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT,"Suggest ideas")
                        })
                    }
                }
                true
            }
        }

        binding.btnGroom.setOnClickListener {
            startActivity(Intent(this,GroomingActivity::class.java))
        }

        binding.btnMedical.setOnClickListener {
            startActivity(Intent(this,MedicalActivity::class.java))
        }

        binding.btnWalking.setOnClickListener {
            startActivity(Intent(this,WalkingActivity::class.java))
        }

        binding.btnFood.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this,NearbyPetFoodStoreActivity::class.java))
            }else{
                permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.btnClinic.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this,NearbyPetClinicActivity::class.java))
            }else{
                permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    private fun setUI() {
        Glide.with(applicationContext)
            .load(getImage())
            .placeholder(R.drawable.animal)
            .error(R.drawable.animal)
            .into(binding.imgPet)

        binding.txtName.text = getPetName()
        binding.txtBreed.text = getPetBreed()
        binding.txtDob.text = getPetDob()

    }
}