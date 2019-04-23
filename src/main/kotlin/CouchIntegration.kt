import com.fasterxml.jackson.annotation.JsonProperty

data class TrusteeByDeviceViewResult(
    @JsonProperty("total_rows")
    val totalRows: Int,
    val offset: Int,
    val rows: List<TrusteeByDevice>
)

data class TrusteeByDevice(
    val id: String,
    @JsonProperty("key")
    val deviceId: String,
    @JsonProperty("value")
    val name: String
)

/**
 * Builds first query param without escaping ":" character
 */
fun quotingQuery(name: String, value: String): String {
    return "?" + """$name="$value"""".replace("\"", "%22")
}