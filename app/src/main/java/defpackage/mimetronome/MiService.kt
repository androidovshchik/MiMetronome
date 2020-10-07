package defpackage.mimetronome

import android.app.Service
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.golda.app.vibrometronome.AlertNotification
import com.golda.app.vibrometronome.ForceConnection
import defpackage.mimetronome.extension.*
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import timber.log.Timber
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Расчет темпа
 * V = скорость (Km/h или Mi/h)
 * D = Расстояние между маркерамм (m или yd)
 * S = секунды на маркер (частота вибрации)
 * Расчет для метрической системы: D в метрах, V в Km/h
 * S = (3.6 x D) / V
 * Расчет для имперской системы: D в ярдах, V в Mi/h
 * S = (2.045 x D) / V
 */
/*
 S рассчитывается до 4 цифр после точки, последнюю цифру опустить, не округлять. 2.4567 = 2.456.
 Для коротких дистанций точность не сильно важна, но, если D = 10 и полная дистанция равна 42.2K (марафон),
 то это начинает быть важным.
 */
@Suppress("MemberVisibilityCanBePrivate")
class MiService : Service(), CoroutineScope {

    private val job = SupervisorJob()

    private var bleGatt: BluetoothGatt? = null

    private val preferences by lazy { Preferences(applicationContext) }

    private val gattClientCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Timber.d("onConnectionStateChange status=${status.gatt} newState=${newState.profile}")
            runBlocking {
                withContext(Dispatchers.Main) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            gatt?.discoverServices()
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            gatt?.connect()
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Timber.d("onServicesDiscovered status=${status.gatt}")
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Timber.d("onCharacteristicRead status=${status.gatt} characteristic=${characteristic?.print()}")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Timber.d("onCharacteristicWrite status=${status.gatt} characteristic=${characteristic?.print()}")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            Timber.d("onCharacteristicChanged characteristic=${characteristic?.print()}")
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val device = preferences.miName
        startForeground(
            1, NotificationCompat.Builder(applicationContext, "service")
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle("Running with $device")
                .setContentIntent(pendingActivityFor<MiActivity>())
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setSound(null)
                .build()
        )
        Timber.d("Starting foreground service")
        val duration = when (preferences.vibration) {
            1 -> 500
            2 -> 1000
            else -> 1500
        }
        val k = when (preferences.units) {
            1 -> 3.6f
            else -> 2.045f
        }
        val d = preferences.distance
        val v = preferences.pace
        val s = k * d / v
        val step = (s * 1000).roundToLong()
        Timber.d("${s.roundToInt()} = $k * $d / $v")
        startConnect()
        launch {
            while (true) {
                delay(step)
                startVibrate(duration)
            }
        }
        launch {
            while (true) {
                delay(10_000)
                forceConnect()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun startConnect() {
        val miMac = preferences.miMac
        bleGatt = bluetoothManager.adapter.getRemoteDevice(miMac)
            .connectGatt(applicationContext, false, gattClientCallback)
    }

    fun forceConnect() {
        bleGatt?.run {
            val characteristic1 = getService(ForceConnection.service)
                ?.getCharacteristic(ForceConnection.alertCharacteristic)
            if (characteristic1 != null) {
                characteristic1.value = "0101e207010f132c000c".toByteArrayAsHex()
                writeCharacteristic(characteristic1)
            }
            val characteristic2 = getService(ForceConnection.service)
                ?.getCharacteristic(ForceConnection.alertCharacteristic)
            if (characteristic2 != null) {
                characteristic2.value = "0102e1070c0f0637360c".toByteArrayAsHex()
                writeCharacteristic(characteristic2)
            }
        }
    }

    fun startVibrate(millis: Int) {
        bleGatt?.run {
            val characteristic = getService(AlertNotification.service)
                ?.getCharacteristic(AlertNotification.alertCharacteristic)
            if (characteristic != null) {
                val array = ByteBuffer.allocate(4).putInt(millis).array()
                characteristic.value = byteArrayOf(-1, array[3], array[2], array[1], array[0], 1)
                writeCharacteristic(characteristic)
            }
        }
    }

    @Suppress("unused")
    fun stopVibrate() {
        bleGatt?.run {
            val characteristic = getService(AlertNotification.service)
                ?.getCharacteristic(AlertNotification.alertCharacteristic)
            if (characteristic != null) {
                characteristic.value = byteArrayOf(0)
                writeCharacteristic(characteristic)
            }
        }
    }

    fun stopConnect() {
        bleGatt?.disconnect()
        bleGatt?.close()
    }

    override fun onDestroy() {
        job.cancelChildren()
        stopConnect()
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }

    companion object {

        fun start(context: Context, vararg params: Pair<String, Any?>) {
            with(context) {
                if (!activityManager.isRunning<MiService>()) {
                    ContextCompat.startForegroundService(context, intentFor<MiService>(*params))
                } else {
                    startService<MiService>(*params)
                }
            }
        }

        @Suppress("unused")
        fun stop(context: Context) {
            with(context) {
                stopService<MiService>()
            }
        }
    }
}

class ServiceRunnable(context: Context) : Runnable {

    private val reference = WeakReference(context)

    private val handler = Handler(Looper.getMainLooper())

    override fun run() {
        try {
            reference.get()?.let {
                MiService.start(it)
            }
        } catch (e: SecurityException) {
            Timber.e(e)
            handler.postDelayed(this, 2000)
        }
    }
}