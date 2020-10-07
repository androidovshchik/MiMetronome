package defpackage.mimetronome

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName = "${context.packageName}_preferences"

    var units by intPref(1, "units")

    var vibration by intPref(1, "vibration")

    var distance by floatPref(0f, "distance")

    var pace by floatPref(0f, "pace")

    var miMac by nullableStringPref(null, "mi_mac")

    var miName by nullableStringPref(null, "mi_name")
}