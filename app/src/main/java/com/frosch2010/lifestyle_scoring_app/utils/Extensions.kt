package com.frosch2010.lifestyle_scoring_app.utils

import java.text.Normalizer

/**
 * Regex removing combining diacritical marks after NFD normalization (accent stripping).
 */
private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

/**
 * Strips diacritics by decomposing Unicode and dropping combining mark code points.
 *
 * Used before fuzzy card matching so OCR noise like accents does not prevent dictionary hits.
 *
 * @receiver Raw user or OCR text that may contain accented Latin characters.
 * @return Lowercasing-neutral base string with accents removed; length may shrink relative to input.
 */
fun String.normalize(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}
