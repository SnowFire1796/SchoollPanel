package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PersianUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(text: String?): String {
        if (text == null) return ""
        val builder = StringBuilder()
        for (char in text) {
            if (char in '0'..'9') {
                builder.append(persianDigits[char - '0'])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Number): String {
        return toPersianDigits(number.toString())
    }

    fun formatScore(score: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            decimalSeparator = '٫'
        }
        val df = if (score % 1.0 == 0.0) {
            DecimalFormat("#0", symbols)
        } else {
            DecimalFormat("#0.0#", symbols)
        }
        return toPersianDigits(df.format(score))
    }

    fun formatScoreWithEnglishDot(score: Double): String {
        val df = if (score % 1.0 == 0.0) {
            DecimalFormat("#0")
        } else {
            DecimalFormat("#0.0#")
        }
        return toPersianDigits(df.format(score))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return toPersianDigits(sdf.format(Date(timestamp)))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return toPersianDigits(sdf.format(Date(timestamp)))
    }

    fun getPerformanceLabel(score: Double): String {
        return when {
            score >= 18.5 -> "عالی"
            score >= 16.0 -> "خیلی خوب"
            score >= 12.0 -> "خوب"
            score >= 10.0 -> "قابل قبول"
            else -> "نیاز به تلاش"
        }
    }
}
