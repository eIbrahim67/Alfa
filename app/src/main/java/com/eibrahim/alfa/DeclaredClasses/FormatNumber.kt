package com.eibrahim.alfa.DeclaredClasses

class FormatNumber {
    companion object {
        fun format(number: Long): String {
            val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T')
            val numValue: Double = number.toDouble()
            val value = Math.floor(Math.log10(numValue)).toInt()
            val base = value / 3

            return if (value < 3) {
                "$number"
            } else {
                val result = numValue / Math.pow(10.0, base * 3.toDouble())
                String.format("%.1f%s", result, suffix[base])
            }
        }
    }
}
