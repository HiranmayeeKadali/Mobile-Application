package com.coddle.ui.activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coddle.databinding.ActivityStartWalkingBinding
import com.coddle.model.Walking
import com.coddle.repository.WalkRepository
import com.coddle.ui.viewModels.StartWalkingProvider
import com.coddle.ui.viewModels.StartWalkingViewModel
import java.math.RoundingMode
import kotlin.math.round
import kotlin.math.sqrt

class StartWalkingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartWalkingBinding
    private var sensorManager: SensorManager? = null
    private var sensorEventListener: SensorEventListener? = null
    private var walking = Walking()
    private var steps = 0
    private var magnitudePrevious = 0
    private lateinit var viewModel: StartWalkingViewModel
    private lateinit var provider: StartWalkingProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartWalkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = StartWalkingProvider(WalkRepository(application))
        viewModel = ViewModelProvider(this, provider)[StartWalkingViewModel::class.java]


        setUI()

        binding.btnStart.setOnClickListener {
            if (binding.btnStart.text.toString().equals("start", true))
                startSensor()
            else
                stopSensor()
        }


    }

    private fun stopSensor() {
        sensorManager?.unregisterListener(sensorEventListener)
        "Start".also { binding.btnStart.text = it }
    }

    private fun setUI() {
        walking = intent.getBundleExtra("bundle")!!.getParcelable<Walking>("walking") as Walking

        steps = walking.steps.toInt()
        "Steps : $steps".also { binding.txtSteps.text = it }
        "Distance : ${walking.distance}".also { binding.txtDistance.text = it }

    }

    private fun startSensor() {
        "Stop".also { binding.btnStart.text = it }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent?) {
                if (sensorEvent != null) {
                    val xAcceleration = sensorEvent.values[0]
                    val yAcceleration = sensorEvent.values[1]
                    val zAcceleration = sensorEvent.values[2]

                    val magnitude =
                        sqrt((xAcceleration * yAcceleration + yAcceleration * yAcceleration + zAcceleration * zAcceleration).toDouble())

                    val magnitudeDelta = magnitude - magnitudePrevious
                    magnitudePrevious = magnitude.toInt()
                    if (magnitudeDelta > 6) {
                        ++steps
                        walking.steps = steps.toString()
                    }
                    calculateDistance(steps)
                }

                "Steps : $steps".also { binding.txtSteps.text = it }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }
        }
        sensorManager?.registerListener(
            sensorEventListener,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )

    }

    private fun calculateDistance(steps: Int) {
        var distance = steps/100.0
        distance *= 000.1
        distance = distance.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
        walking.distance = distance.toString()
        viewModel.updateWalking(walking)
        "Distance : $distance".also { binding.txtDistance.text = it }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(sensorEventListener)
    }

}