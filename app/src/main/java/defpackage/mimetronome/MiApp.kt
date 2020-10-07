package defpackage.mimetronome

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import defpackage.mimetronome.extension.isOreoPlus
import org.jetbrains.anko.notificationManager
import timber.log.Timber

var miMac: String? = null

@Suppress("unused")
class MiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel("main", "Main", NotificationManager.IMPORTANCE_DEFAULT)
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "service",
                    "Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
                    setSound(null, null)
                }
            )
        }
        AndroidThreeTen.init(this)
    }
}