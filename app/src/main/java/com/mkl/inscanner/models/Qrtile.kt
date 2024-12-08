package com.mkl.inscanner.models

import com.mkl.inscanner.R
import kotlinx.serialization.Serializable

@Serializable
data class Qrtile(
    val text: String,
    val iconRes: Int
)

// List of row items
val QrtileItems = listOf(
    Qrtile("URL", R.drawable.url),
    Qrtile("Text", R.drawable.text),
    Qrtile("Contact", R.drawable.contact),
    Qrtile("Email", R.drawable.email),
    Qrtile("SMS", R.drawable.sms),
    Qrtile("Geo", R.drawable.nrk_geopoint),
    Qrtile("Phone", R.drawable.phone),
    Qrtile("Calendar", R.drawable.calendar),
    Qrtile("WiFi", R.drawable.wifi),
    Qrtile("EAN_8", R.drawable.bar_code),
    Qrtile("EAN_13", R.drawable.bar_code),
    Qrtile("UPC_E", R.drawable.bar_code),
    Qrtile("CODE_39", R.drawable.bar_code),
    Qrtile("CODE_93", R.drawable.bar_code),
    Qrtile("CODE_128", R.drawable.bar_code),
    Qrtile("ITF", R.drawable.bar_code),
    Qrtile("PDF_417", R.drawable.bar_code),
    Qrtile("DATA_MATRIX", R.drawable.bar_code)
)