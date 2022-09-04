package com.hjj.apiserver.config

import com.p6spy.engine.common.ConnectionInformation
import com.p6spy.engine.event.JdbcEventListener
import com.p6spy.engine.spy.P6SpyOptions
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.sql.SQLException

@Component
class P6spySqlFormatConfiguration : JdbcEventListener(), MessageFormattingStrategy {

    override fun onAfterGetConnection(connectionInformation: ConnectionInformation?, e: SQLException?) {
        P6SpyOptions.getActiveInstance().logMessageFormat = javaClass.name
    }

    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String,
        url: String?
    ): String {

        val sb = StringBuilder()
        sb.append(category).append(" ").append(elapsed).append("ms")
        if (StringUtils.hasText(sql)) {
            sb.append(highlight(formatSql(sql)))
        }
        return sb.toString()
    }

    private fun formatSql(sql: String): String {
        if (isDDL(sql)) {
            return FormatStyle.DDL.formatter.format(sql);
        } else if (isBasic(sql)) {
            return FormatStyle.BASIC.formatter.format(sql);
        }

        return sql
    }

    private fun highlight(sql: String): String? {
        return FormatStyle.HIGHLIGHT.formatter.format(sql)
    }

    private fun isDDL(sql: String): Boolean {
        return sql.startsWith("create") || sql.startsWith("alter") || sql.startsWith("comment")
    }

    private fun isBasic(sql: String): Boolean {
        return sql.startsWith("select") || sql.startsWith("insert") || sql.startsWith("update") || sql.startsWith("delete")
    }

}