import com.natpryce.hamkrest.Matcher
import org.http4k.core.Method
import org.http4k.hamkrest.hasBody

fun Request() = org.http4k.core.Request(Method.GET, "")

val hasEmptyBody = hasBody(Matcher(String::isEmpty))