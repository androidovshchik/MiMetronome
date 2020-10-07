package defpackage.mimetronome.extension

import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_SECONDARY
import splitties.bitflags.hasFlag

fun BluetoothGattService.print(printCharacteristics: Boolean = true): String {
    return if (printCharacteristics) printWithCharacteristics() else printWithoutCharacteristics()
}

fun BluetoothGattService.printWithoutCharacteristics(): String = """UUID: $uuid
instance ID: $instanceId
type: $typeString
characteristics count: ${characteristics.count()}
included services count: ${includedServices?.count()}
"""

fun BluetoothGattService.printWithCharacteristics(): String = """UUID: $uuid
instance ID: $instanceId
type: $typeString
characteristics: {
${characteristics.joinToString { it.print() }.prependIndent()}
}
included services count: ${includedServices?.count()}
"""

fun BluetoothGattCharacteristic.print(): String = """UUID: $uuid
instance ID: $instanceId
permissions: $permissionsString
writeType: $writeTypeString
properties: $propertiesString
value: $value
stringValue: ${getStringValue(0)}
"""

fun BluetoothGattDescriptor.print(): String = """UUID: $uuid
permissions: $permissions
value: $value
characteristic: ${characteristic?.print()}
"""

private val BluetoothGattService.typeString: String
    get() = when (type) {
        SERVICE_TYPE_PRIMARY -> "PRIMARY"
        SERVICE_TYPE_SECONDARY -> "SECONDARY"
        else -> "UNKNOWN"
    }

private val BluetoothGattCharacteristic.writeTypeString: String
    get() = when (writeType) {
        WRITE_TYPE_DEFAULT -> "DEFAULT"
        WRITE_TYPE_NO_RESPONSE -> "NO_RESPONSE"
        WRITE_TYPE_SIGNED -> "SIGNED"
        else -> "UNKNOWN"
    }

private val BluetoothGattCharacteristic.propertiesString: String
    get() = propertiesString(properties)

private val BluetoothGattCharacteristic.permissionsString: String
    get() {
        return "$permissions"
        //return permissionsString(permissions)
    }

@Suppress("DEPRECATION")
@Deprecated("Doesn't seem to work")
private val BluetoothGattDescriptor.permissionsString: String
    get() = permissionsString(permissions)

@Deprecated("Doesn't seem to work")
private fun permissionsString(permissions: Int): String = StringBuilder().apply {
    if (permissions.hasFlag(PERMISSION_READ)) append("READ, ")
    if (permissions.hasFlag(PERMISSION_READ_ENCRYPTED)) append("READ_ENCRYPTED, ")
    if (permissions.hasFlag(PERMISSION_READ_ENCRYPTED_MITM)) append("READ_ENCRYPTED_MITM, ")
    if (permissions.hasFlag(PERMISSION_WRITE)) append("WRITE, ")
    if (permissions.hasFlag(PERMISSION_WRITE_ENCRYPTED)) append("WRITE_ENCRYPTED, ")
    if (permissions.hasFlag(PERMISSION_WRITE_ENCRYPTED_MITM)) append("WRITE_ENCRYPTED_MITM, ")
    if (permissions.hasFlag(PERMISSION_WRITE_SIGNED)) append("WRITE_SIGNED, ")
    if (permissions.hasFlag(PERMISSION_WRITE_SIGNED_MITM)) append("WRITE_SIGNED_MITM, ")
}.toString()

private fun propertiesString(properties: Int): String = StringBuilder().apply {
    if (properties.hasFlag(PROPERTY_READ)) append("READ, ")
    if (properties.hasFlag(PROPERTY_WRITE)) append("WRITE, ")
    if (properties.hasFlag(PROPERTY_WRITE_NO_RESPONSE)) append("WRITE_NO_RESPONSE, ")
    if (properties.hasFlag(PROPERTY_SIGNED_WRITE)) append("SIGNED_WRITE, ")
    if (properties.hasFlag(PROPERTY_INDICATE)) append("INDICATE, ")
    if (properties.hasFlag(PROPERTY_NOTIFY)) append("NOTIFY, ")
    if (properties.hasFlag(PROPERTY_BROADCAST)) append("BROADCAST, ")
    if (properties.hasFlag(PROPERTY_EXTENDED_PROPS)) append("EXTENDED_PROPS, ")
}.toString()

val Int.gatt: String
    get() = when (this) {
        BluetoothGatt.GATT_SUCCESS -> "GATT_SUCCESS"
        BluetoothGatt.GATT_READ_NOT_PERMITTED -> "GATT_READ_NOT_PERMITTED"
        BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> "GATT_WRITE_NOT_PERMITTED"
        BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> "GATT_INSUFFICIENT_AUTHENTICATION"
        BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED -> "GATT_REQUEST_NOT_SUPPORTED"
        BluetoothGatt.GATT_INVALID_OFFSET -> "GATT_INVALID_OFFSET"
        BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> "GATT_INVALID_ATTRIBUTE_LENGTH"
        BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION -> "GATT_INSUFFICIENT_ENCRYPTION"
        BluetoothGatt.GATT_CONNECTION_CONGESTED -> "GATT_CONNECTION_CONGESTED"
        BluetoothGatt.GATT_FAILURE -> "GATT_FAILURE"
        else -> "UNKNOWN=$this"
    }

val Int.profile: String
    get() = when (this) {
        BluetoothProfile.STATE_DISCONNECTED -> "STATE_DISCONNECTED"
        BluetoothProfile.STATE_CONNECTING -> "STATE_CONNECTING"
        BluetoothProfile.STATE_CONNECTED -> "STATE_CONNECTED"
        BluetoothProfile.STATE_DISCONNECTING -> "STATE_DISCONNECTING"
        BluetoothGatt.GATT_FAILURE -> "GATT_FAILURE"
        else -> "UNKNOWN=$this"
    }