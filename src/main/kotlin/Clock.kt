import java.time.ZoneId
import java.time.ZonedDateTime

interface Clock {
    fun now(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC-3"))
}

object RealClock : Clock
