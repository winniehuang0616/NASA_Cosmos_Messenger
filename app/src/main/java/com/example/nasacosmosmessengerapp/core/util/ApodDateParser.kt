package com.example.nasacosmosmessengerapp.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

sealed interface ApodDateParseResult {
    data object NotDateLike : ApodDateParseResult
    data object InvalidDate : ApodDateParseResult
    data class OutOfRange(
        val minDate: String,
        val maxDate: String
    ) : ApodDateParseResult
    data class Valid(val canonicalDate: String) : ApodDateParseResult
}

private const val MIN_APOD_DATE = "1995-06-16"
private const val CANONICAL_PATTERN = "yyyy-MM-dd"

fun parseApodDateInput(input: String): ApodDateParseResult {
    val candidate = extractDateCandidate(input.trim()) ?: return ApodDateParseResult.NotDateLike

    val parsed = parseCanonicalDate(candidate) ?: return ApodDateParseResult.InvalidDate
    val minDate = parseCanonicalDate(MIN_APOD_DATE) ?: return ApodDateParseResult.InvalidDate
    val maxDate = utcTodayDate()

    if (parsed.before(minDate) || parsed.after(maxDate)) {
        return ApodDateParseResult.OutOfRange(
            minDate = MIN_APOD_DATE,
            maxDate = formatCanonical(maxDate)
        )
    }

    return ApodDateParseResult.Valid(canonicalDate = formatCanonical(parsed))
}

private fun extractDateCandidate(text: String): String? {
    val separated = Regex("(?<!\\d)(\\d{4})\\s*([-~/.|]|\\s)+\\s*(\\d{1,2})\\s*([-~/.|]|\\s)+\\s*(\\d{1,2})(?!\\d)")
        .find(text)
    if (separated != null) {
        val year = separated.groupValues[1].toIntOrNull() ?: return null
        val month = separated.groupValues[3].toIntOrNull() ?: return null
        val day = separated.groupValues[5].toIntOrNull() ?: return null
        return toCanonical(year, month, day)
    }

    val compact = Regex("(?<!\\d)(\\d{4})(\\d{2})(\\d{2})(?!\\d)").find(text)
    if (compact != null) {
        val year = compact.groupValues[1].toIntOrNull() ?: return null
        val month = compact.groupValues[2].toIntOrNull() ?: return null
        val day = compact.groupValues[3].toIntOrNull() ?: return null
        return toCanonical(year, month, day)
    }

    return null
}

private fun toCanonical(year: Int, month: Int, day: Int): String =
    String.format(Locale.US, "%04d-%02d-%02d", year, month, day)

private fun parseCanonicalDate(raw: String): Date? {
    val parser = SimpleDateFormat(CANONICAL_PATTERN, Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val parsed = try {
        parser.parse(raw)
    } catch (_: Exception) {
        null
    } ?: return null

    return if (parser.format(parsed) == raw) parsed else null
}

private fun utcTodayDate(): Date {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return utc.time
}

private fun formatCanonical(date: Date): String {
    val formatter = SimpleDateFormat(CANONICAL_PATTERN, Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(date)
}
