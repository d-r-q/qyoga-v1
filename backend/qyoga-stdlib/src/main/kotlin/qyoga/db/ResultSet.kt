package qyoga.db

import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.Time
import java.sql.Types


fun ResultSet.asSequence(): Sequence<Row> {
    return sequence {
        while (this@asSequence.next()) {
            yield(Row.fetch(this@asSequence))
        }
    }
}

@Suppress("UNCHECKED_CAST")
class Row(private val label2idx: Map<String, Int>, private val values: Array<Any?>) {

    operator fun <T : Any?> get(col: String): T {
        val value = values[col2idx(col)]
        return value as? T
            ?: throw IllegalStateException("$col's type is ${value!!::class}")
    }

    fun getString(col: String): String? {
        return values[col2idx(col)] as String?
    }

    fun getLong(col: String): Long? {
        val value = values[col2idx(col)]
        return (value as? BigDecimal)?.toLong() ?: value as Long?
    }

    fun getTime(col: String): Time? {
        val value = values[col2idx(col)]
        return value as Time?
    }

    fun getInteger(col: String): Int? {
        val value = values[col2idx(col)]
        return (value as? Int)?.toInt() ?: value as Int?
    }

    fun getBoolean(col: String): Boolean? {
        val value = values[col2idx(col)]
        return if (value is BigDecimal) {
            value >= BigDecimal(1)
        } else {
            value as Boolean?
        }
    }

    private fun col2idx(col: String): Int {
        return label2idx[col.toLowerCase()]
            ?: throw IllegalStateException("Column $col not found. Columns: ${label2idx.keys}")
    }

    companion object {

        internal fun fetch(resultSet: ResultSet): Row {
            val md = resultSet.metaData
            val columns = md.columnCount
            val res = arrayOfNulls<Any>(columns)
            val label2idx = HashMap<String, Int>()
            for (i in 1..columns) {
                val columnLabel = md.getColumnLabel(i)
                label2idx[columnLabel.toLowerCase()] = i - 1
                val columnType = md.getColumnType(i)
                val value: Any?
                value = when (columnType) {
                    Types.VARCHAR -> resultSet.getString(i)
                    Types.BIGINT, Types.DOUBLE, Types.INTEGER, Types.NUMERIC, Types.REAL -> resultSet.getObject(i)
                    Types.BOOLEAN, Types.BIT -> resultSet.getBoolean(i)
                    Types.TIME -> resultSet.getTime(i)
                    Types.CLOB -> {
                        resultSet.getClob(i)?.getSubString(1, 102400)
                    }
                    else -> resultSet.getObject(i)
                }
                res[i - 1] = value

            }
            return Row(label2idx, res)
        }
    }

}
