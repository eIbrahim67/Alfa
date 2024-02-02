package com.eibrahim.alfa.declaredClasses

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class FormatNumber {
    companion object {
        fun format(number: Long): String {
            val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T')
            val numValue: Double = number.toDouble()
            val value = floor(log10(numValue)).toInt()
            val base = value / 3

            return if (value < 3) {
                "$number"
            } else {
                val result = numValue / 10.0.pow(base * 3.toDouble())
                String.format("%.1f%s", result, suffix[base])
            }
        }
    }
}
