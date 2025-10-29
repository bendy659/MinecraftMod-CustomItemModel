package ru.benos.custom_item_model.client

import io.netty.handler.logging.LogLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CIMLogger(
    val base: Logger,
    val historySize: Int = 8
) {
    constructor(base: String, historySize: Int = 8): this(LogManager.getLogger(base), historySize)

    data class LogEntry(var message: String, var logLevel: LogLevel, var count: Int = 0)
    private val HISTORY: MutableList<LogEntry> = mutableListOf()

    fun info(message: String) = logging(message, LogLevel.INFO) { base.info(it) }
    fun warn(message: String) = logging(message, LogLevel.WARN) { base.warn(it) }
    fun error(message: String) = logging(message, LogLevel.ERROR) { base.error(it) }
    fun debug(message: String) = logging(message, LogLevel.DEBUG) { base.debug(it) }
    fun fatal(message: String) = logging(message, LogLevel.TRACE) { base.fatal(it) }

    fun logging(message: String, logLevel: LogLevel, logAction: (String) -> Unit) {
        val last = HISTORY.lastOrNull()
        if (last != null && last.message == message) {
            last.count++
        } else {
            last?.let { flushEntry(it, logAction) }

            if (HISTORY.size >= historySize) HISTORY.removeAt(0)
            HISTORY += LogEntry(message, logLevel)
        }
    }

    private fun flushEntry(entry: LogEntry, logAction: (String) -> Unit) =
        if (entry.count > 1)
            logAction("${entry.message} | ${entry.count}")
        else
            logAction(entry.message)


    fun flushAll() {
        HISTORY.forEach { flushEntry(it) { msg -> base.info(msg) } }
        HISTORY.clear()
    }
}