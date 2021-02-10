package io.nimbly.any2json.languages

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.ALLOW_TRAILING_COMMA
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.EMPTY_STRING_AS_NULL
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.SKIP_EMPTY_LINES
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.TRIM_SPACES
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import io.nimbly.any2json.util.Any2PojoException

class Csv2Json(actionType: EType) : AnyToJsonBuilder<String, List<Map<String, Any>>>(actionType) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(csv: String): List<Map<String, Any>> {

        val separatorsCandidates = listOf(
            ';' to csv.count { it == ';' },
            ',' to csv.count { it == ',' },
            '\t' to csv.count { it == '\t' })
        val separator = separatorsCandidates.maxByOrNull { it.second }?.first ?: ';'

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
                .disable(SORT_PROPERTIES_ALPHABETICALLY)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .readerFor(Map::class.java)
                .with(csvSchema)
                .readValues<Map<String, Any>>(csv)

            val lines = mappingIterator.readAll()
            lines

        } catch (e: MismatchedInputException) {
            throw Any2PojoException("Unable to parse CSV. Does it have a header ?")
        }

        return lines
    }

    override fun presentation()
        = "from CSV"

    override fun isVisible()
        = actionType == EType.MAIN
}
