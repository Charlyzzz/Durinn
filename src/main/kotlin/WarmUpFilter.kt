import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

const val WARMUP_HEADER = "x-warm-up"

object WarmUpFilter : Filter by WarmUpFilter({ })

fun WarmUpFilter(onWarmUpEvent: () -> Unit) = Filter { next ->
    {
        if (it.header(WARMUP_HEADER) == "true") {
            onWarmUpEvent()
            Response(OK).header(WARMUP_HEADER, "true")
        } else {
            next(it)
        }
    }
}
