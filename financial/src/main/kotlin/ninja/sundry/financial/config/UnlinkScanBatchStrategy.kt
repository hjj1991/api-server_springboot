package ninja.sundry.financial.config

import org.springframework.data.redis.cache.BatchStrategy
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.ScanOptions

class UnlinkScanBatchStrategy(
    private val batchSize: Int,
) : BatchStrategy {
    override fun cleanCache(
        connection: RedisConnection,
        name: String,
        pattern: ByteArray,
    ): Long =
        connection.keyCommands().scan(ScanOptions.scanOptions().count(this.batchSize.toLong()).match(pattern).build()).asSequence()
            .chunked(this.batchSize)
            .map { keys ->
                connection.keyCommands().unlink(*keys.toTypedArray())
                keys.size.toLong()
            }.sum()
}
