package defpackage.mimetronome.extension

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor

inline fun <reified T> Context.getComponent() = ComponentName(applicationContext, T::class.java)

inline fun <reified T> Context.activityCallback(action: T.() -> Unit) {
    getActivity()?.let {
        if (it is T && !it.isFinishing) {
            action(it)
        }
    }
}

tailrec fun Context?.getActivity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.getActivity()
}

fun Context.areGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

inline fun <reified T : Activity> Context.pendingActivityFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getActivity(applicationContext, requestCode, intentFor<T>(*params), flags)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, intentFor<T>(*params), flags)

fun Context.pendingReceiverFor(
    action: String,
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, Intent(action), flags)

inline fun <reified T : BroadcastReceiver> Context.cancelAlarm(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
) {
    alarmManager.cancel(pendingReceiverFor<T>(requestCode, flags))
}