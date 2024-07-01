package io.github.thegbguy.date

import io.github.thegbguy.date.NepaliDateUtils.isEngDateInConversionRange
import io.github.thegbguy.date.NepaliDateUtils.isNepDateInConversionRange
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/**
 * todo simplify converters
 */

/**
 * convert nepali date into english date
 *
 * @param nepYY `int` year of nepali date [1970-2090]
 * @param nepMM `int` month of nepali date [1-12]
 * @param nepDD `int` day of a nepali date [1-32]
 * @return [LocalDate] object with the converted value from nepali to english
 */
internal fun convertToAd(
    nepYY: Int,
    nepMM: Int,
    nepDD: Int
): LocalDate {
    return if (isNepDateInConversionRange(nepYY, nepMM, nepDD)
    ) {
        val startingEngYear = 1913
        val startingEngMonth = 4
        val startingEngDay = 13
        DayOfWeek.SUNDAY
        // 1970/1/1 is Sunday, using 1 for Sunday directly since DayOfWeek uses 1 for Monday
        val startingDayOfWeek = 1  // based on www.ashesh.com.np/nepali-date-converter
        val startingNepYear = 1970
        val startingNepMonth = 1
        val startingNepDay = 1
        var engMM: Int
        var engDD: Int
        var totalNepDaysCount: Long = 0

        //*count total no of days in nepali year from our starting range*//
        for (i in startingNepYear until nepYY) {
            for (j in 1..12) {
                totalNepDaysCount += NepaliDateUtils.daysInMonthMap[i]?.getOrNull(j) ?: 0
            }
        }
        /*count total days in terms of month*/
        for (j in startingNepMonth until nepMM) {
            totalNepDaysCount += NepaliDateUtils.daysInMonthMap[nepYY]?.getOrNull(j) ?: 0
        }
        /*count total days in terms of date*/
        totalNepDaysCount += (nepDD - startingNepDay).toLong()
        val daysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val daysInMonthOfLeapYear = intArrayOf(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var engYY: Int = startingEngYear
        engMM = startingEngMonth
        engDD = startingEngDay
        var endDayOfMonth: Int
        var dayOfWeek = startingDayOfWeek
        while (totalNepDaysCount != 0L) {
            endDayOfMonth = if (isEngLeapYear(engYY)) {
                daysInMonthOfLeapYear[engMM]
            } else {
                daysInMonth[engMM]
            }
            engDD++
            dayOfWeek++
            if (engDD > endDayOfMonth) {
                engMM++
                engDD = 1
                if (engMM > 12) {
                    engYY++
                    engMM = 1
                }
            }
            if (dayOfWeek > 7) {
                dayOfWeek = 1
            }
            totalNepDaysCount--
        }
        LocalDate(engYY, engMM - 1, engDD)
    } else throw IllegalArgumentException("Out of Range: Date is out of range to Convert")
}


/**
 * convert english date into nepali date
 *
 * @param engYY `int` year of nepali date [1944-2033]
 * @param engMM `int` month of nepali date [1-12]
 * @param engDD `int` day of a nepali date [1-31]
 * @return return nepali date as a [NepaliDate] object converted from english to nepali
 */
internal fun convertToBs(
    engYY: Int,
    engMM: Int,
    engDD: Int
): NepaliDate {
    return if (isEngDateInConversionRange(engYY, engMM, engDD)
    ) {
        val startingEngYear = 1913
        val startingEngMonth = 4
        val startingEngDay = 13
        val startingDayOfWeek = 1 // 1913/4/13 is a Sunday
        val startingNepYear = 1970
        val startingNepMonth = 1
        val startingNepDay = 1
        var dayOfWeek = startingDayOfWeek


        /*calculate the days between two english date*/
        val base = LocalDate(startingEngYear, startingEngMonth, startingEngDay) // June 20th, 2010
        val newDate = LocalDate(engYY, engMM, engDD) // July 24th
        var totalEngDaysCount = base.daysUntil(newDate)
        var nepYY: Int = startingNepYear
        var nepMM = startingNepMonth
        var nepDD = startingNepDay
        while (totalEngDaysCount != 0) {
            val daysInMonth: Int =
                NepaliDateUtils.daysInMonthMap[nepYY]?.get(nepMM) ?: 0
            nepDD++
            if (nepDD > daysInMonth) {
                nepMM++
                nepDD = 1
            }
            if (nepMM > 12) {
                nepYY++
                nepMM = 1
            }
            dayOfWeek++
            if (dayOfWeek > 7) {
                dayOfWeek = 1
            }
            totalEngDaysCount--
        }
        NepaliDate(nepYY, nepMM, nepDD, dayOfWeek)
    } else throw IllegalArgumentException("Out of Range: Date is out of range to Convert")
}

private fun isEngLeapYear(year: Int): Boolean {
    val current = LocalDate(year, 1, 1)
    val nextYear = LocalDate(year + 1, 1, 1)
    return current.daysUntil(nextYear) > 365
}