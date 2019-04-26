import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.and
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasHeader
import org.http4k.hamkrest.hasStatus

fun Request() = Request(Method.GET, "")

val WarmUpRequest = Request().header(WARMUP_HEADER, "true")

val hasEmptyBody = hasBody(Matcher(String::isEmpty))

val isFromWarmUp = hasEmptyBody and hasStatus(OK) and hasHeader(WARMUP_HEADER, "true")
