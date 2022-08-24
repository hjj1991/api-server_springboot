package com.hjj.apiserver.config

import com.p6spy.engine.common.P6Util
import com.p6spy.engine.logging.Category
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle
import org.springframework.util.StringUtils

class P6spySqlFormatConfiguration: MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?
    ): String {

        val formatSql = formatSql(category, sql)
        return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + P6Util.singleLine(prepared) + formatSql
    }

    private fun formatSql(category: String?, sql: String?): String? {

        if(!StringUtils.hasText(sql)){
            return sql
        }

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.name == category) {
            val tmpsql = sql!!.trim().lowercase()
            var convertSql = if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                FormatStyle.DDL.formatter.format(sql)
            } else {
                FormatStyle.BASIC.formatter.format(sql)
            }
            convertSql = "|\nHeFormatSql(P6Spy sql,Hibernate format):$convertSql"

            return convertSql
        }
        return sql
    }
}