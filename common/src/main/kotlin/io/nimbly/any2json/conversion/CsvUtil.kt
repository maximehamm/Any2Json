package io.nimbly.any2json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.*
import com.fasterxml.jackson.dataformat.csv.CsvSchema

fun looksLikeCsv(csv: String): Boolean {

    val separorCounts = guessSeparator(csv)?.second
    if (separorCounts==null || separorCounts == 0)
        throw Any2PojoException("No CSV separator found !")

    val xmlSep = csv.count { it == '<' }
    if (separorCounts < xmlSep)
        throw Any2PojoException("Looks like XML not CSV !")

    val jsonSep = csv.count { it == '{' }
    if (separorCounts < jsonSep)
        throw Any2PojoException("Looks like JSON not CSV !")

    val yamlStart = csv.trim().substringBefore('\n') == "---"
    if (yamlStart)
        throw Any2PojoException("Looks like YAML not CSV !")

    return true
}

fun csvToMap(csv: String): List<Map<String, Any>> {

    val separator = guessSeparator(csv)?.first ?: ';'

    val lines: List<Map<String, Any>> = try {

        val csvSchema = CsvSchema.builder()
            .setUseHeader(true)
            .setColumnSeparator(separator)
            .build()

        val mappingIterator = CsvMapper()
            .enable(TRIM_SPACES)
            .enable(ALLOW_TRAILING_COMMA)
            .enable(INSERT_NULLS_FOR_MISSING_COLUMNS)
            .enable(SKIP_EMPTY_LINES)
            .enable(EMPTY_STRING_AS_NULL)
            .enable(IGNORE_TRAILING_UNMAPPABLE)
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readerFor(Map::class.java)
            .with(csvSchema)
            .readValues<Map<String, Any>>(csv)

        val lines = mappingIterator.readAll()
        lines

    } catch (e: MismatchedInputException) {
        throw Any2PojoException("Unable to parse CSV. Does it have a header ?")
    }

    if (csv.isNotEmpty() && lines.isEmpty())
        throw Any2PojoException("Unable to parse CSV !")

    return lines
}

private fun guessSeparator(csv: String): Pair<Char, Int>? {
    val separatorsCandidates = listOf(
        ';' to csv.count { it == ';' },
        ',' to csv.count { it == ',' },
        '\t' to csv.count { it == '\t' })

    return separatorsCandidates.maxByOrNull { it.second }
}