package defpackage.mimetronome

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.chibatching.kotpref.bulk
import defpackage.mimetronome.base.BaseActivity
import defpackage.mimetronome.base.BaseAdapter
import defpackage.mimetronome.extension.isRunning
import defpackage.mimetronome.extension.setTextSelection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.bluetoothManager
import org.jetbrains.anko.newTask
import org.jetbrains.anko.sdk21.listeners.textChangedListener

@SuppressLint("SetTextI18n")
class MiActivity : BaseActivity(), BaseAdapter.Listener<BluetoothDevice> {

    private val preferences by lazy { Preferences(applicationContext) }

    private val adapter = MiAdapter(this)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (rg_units.getChildAt(preferences.units - 1) as RadioButton).isChecked = true
        (rg_vibration.getChildAt(preferences.vibration - 1) as RadioButton).isChecked = true
        rg_units.setOnCheckedChangeListener { _, checkedId ->
            stopService()
            preferences.units = checkedId
            updateDimensions()
        }
        rg_vibration.setOnCheckedChangeListener { _, checkedId ->
            stopService()
            preferences.vibration = checkedId - 2
        }
        et_distance.setTextSelection(preferences.distance.toString())
        et_distance.textChangedListener {
            afterTextChanged {
                stopService()
                preferences.distance = it.toString().toFloatOrNull() ?: 0f
            }
        }
        et_pace.setTextSelection(preferences.pace.toString())
        et_pace.textChangedListener {
            afterTextChanged {
                stopService()
                preferences.pace = it.toString().toFloatOrNull() ?: 0f
            }
        }
        rv_devices.adapter = adapter
        tv_help.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage("com.xiaomi.hm.health")
            if (intent != null) {
                startActivity(intent.newTask())
            } else {
                showMessage("MiFit app is not installed")
            }
        }
        btn_go.setOnClickListener {
            if (!activityManager.isRunning<MiService>()) {
                startService()
            } else {
                stopService()
            }
        }
        updateDimensions()
        if (activityManager.isRunning<MiService>()) {
            btn_go.text = "Stop"
        }
        launch {
            while (true) {
                adapter.items.clear()
                val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
                if (devices.isNotEmpty()) {
                    adapter.items.addAll(devices)
                    tv_help.isVisible = false
                } else {
                    tv_help.isVisible = true
                }
                adapter.notifyDataSetChanged()
                delay(3000)
            }
        }
    }

    override fun onItemClick(position: Int, item: BluetoothDevice) {
        stopService()
        preferences.bulk {
            miMac = item.address
            miName = item.name
        }
    }

    private fun startService() {
        when {
            preferences.distance <= 0 -> {
                showMessage("Please fill marker distance value")
                return
            }
            preferences.pace <= 0 -> {
                showMessage("Please fill pace value")
                return
            }
            miMac.isNullOrBlank() -> {
                showMessage("Please select a band")
                return
            }
        }
        ServiceRunnable(applicationContext).run()
        btn_go.text = "Stop"
    }

    private fun stopService() {
        miMac = null
        MiService.stop(applicationContext)
        btn_go.text = "Start"
        adapter.notifyDataSetChanged()
    }

    private fun updateDimensions() {
        when (preferences.units) {
            1 -> {
                til_distance.suffixText = "meters"
                til_pace.suffixText = "Km/h"
            }
            else -> {
                til_distance.suffixText = "yards"
                til_pace.suffixText = "Mi/h"
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            if (!bluetoothManager.adapter.isEnabled) {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1)
            }
        }
    }
}