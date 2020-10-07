package com.golda.app.vibrometronome

import java.util.*

object AlertMyNotification {
    val alertCharacteristic: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val service: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
}

object AlertNotification {
    val alertCharacteristic: UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb")
    val service: UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb")
}

object AlertNotificationAndMessage {
    val alertCharacteristic: UUID = UUID.fromString("00002a46-0000-1000-8000-00805f9b34fb")
    val service: UUID = UUID.fromString("00001811-0000-1000-8000-00805f9b34fb")
}

object Basic {
    val batteryCharacteristic: UUID = UUID.fromString("00000006-0000-3512-2118-0009af100700")
    val buttonAlertCharacteristic: UUID = UUID.fromString("00000010-0000-3512-2118-0009af100700")
    val service: UUID = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb")
}

object ForceConnection {
    val alertCharacteristic: UUID = UUID.fromString("00000004-0000-3512-2118-0009af100700")
    val service: UUID = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb")
}

object HeartRate {
    val controlCharacteristic: UUID = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb")
    val descriptor: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val measurementCharacteristic: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val service: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
}