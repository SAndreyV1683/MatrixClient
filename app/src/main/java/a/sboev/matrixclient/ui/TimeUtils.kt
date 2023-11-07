package a.sboev.matrixclient.ui

import io.terrakok.smalk.service.createDateFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

private val timeFormat = createDateFormat("HH:mm")
private val dayFormat = createDateFormat("EEE")
private val fullFormat = createDateFormat("dd/MM/yy")
private val fullDayFormat = createDateFormat("d MMM yyyy")

fun Instant?.toText(): String {
    val tz = TimeZone.currentSystemDefault()
    val date = this?.toLocalDateTime(tz) ?: return ""
    val now = Clock.System.now().toLocalDateTime(tz)
    return when {
        date.date == now.date -> timeFormat(this)
        date.date.weekOfYear() == now.date.weekOfYear() -> dayFormat(this)
        else -> fullFormat(this)
    }
}

fun Instant.fullDayText() = fullDayFormat(this)
fun Instant.timeText() = timeFormat(this)

private fun LocalDate.weekOfYear(): Int {
    val firstDayOfYear = LocalDate(year, 1, 1)
    val daysFromFirstDay = dayOfYear - firstDayOfYear.dayOfYear
    val firstDayOfYearDayOfWeek = firstDayOfYear.dayOfWeek.isoDayNumber
    val adjustment = when {
        firstDayOfYearDayOfWeek <= 4 -> firstDayOfYearDayOfWeek - 1
        else -> 8 - firstDayOfYearDayOfWeek
    }
    return (daysFromFirstDay + adjustment) / 7 + 1
}