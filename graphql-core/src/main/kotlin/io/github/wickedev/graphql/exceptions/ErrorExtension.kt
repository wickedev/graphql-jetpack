package io.github.wickedev.graphql.exceptions

import java.io.PrintWriter
import java.io.StringWriter

@Suppress("unused")
class ErrorExtension(
    exception: Exception
) {
    companion object {
        private fun stackTraceToList(exception: Exception): List<String> {
            val sw = StringWriter()
            val writer = PrintWriter(sw)
            try {
                exception.printStackTrace(writer)
            } catch (e: Error) {
                e.printStackTrace()
            } finally {
                writer.close()
                sw.close()
            }

            return sw.toString().split("\n")
        }
    }

    val message: String? = exception.message

    val localizedMessage: String? = exception.localizedMessage

    val cause = exception.cause

    val stackTrace: List<String> = stackTraceToList(exception).take(10).map { it.replace("\t", "  ") }
}