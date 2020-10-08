package defpackage.mimetronome

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import defpackage.mimetronome.extension.isOreoPlus
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.jetbrains.anko.notificationManager
import timber.log.Timber

var miMac: String? = null

@Suppress("unused")
class MiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            ACRA.init(this, CoreConfigurationBuilder(applicationContext)
                .setBuildConfigClass(BuildConfig::class.java)
                .setReportFormat(StringFormat.KEY_VALUE_LIST)
                .setEnabled(true).apply {
                    getPluginConfigurationBuilder(MailSenderConfigurationBuilder::class.java)
                        .setMailTo("vladkalyuzhnyu@gmail.com")
                        .setResSubject(R.string.crash_subject)
                        .setReportFileName("logs.txt")
                        .setReportAsFile(true)
                        .setEnabled(true)
                    getPluginConfigurationBuilder(DialogConfigurationBuilder::class.java)
                        .setResTheme(R.style.Theme_MaterialComponents_Dialog)
                        .setResTitle(R.string.crash_title)
                        .setResText(R.string.crash_text)
                        .setResCommentPrompt(R.string.crash_comment)
                        .setEnabled(true)
                })
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