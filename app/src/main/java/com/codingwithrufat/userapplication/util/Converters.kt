package com.codingwithrufat.userapplication.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * api badges items such as gold, silver and bronze is concated by helping this method
 * Like that --> Silver: 86
 */
fun convertBadgeParametersToString(badge_name: String, badge_count: Int): String{
    return "$badge_name: $badge_count"
}

// is converted integer time format to UTC time format
fun Int.convertUTCToDateFormat(): String {
    val date = Date(1000L * this)
    val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(date)
}