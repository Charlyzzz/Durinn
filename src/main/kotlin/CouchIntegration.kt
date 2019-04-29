import com.google.gson.annotations.SerializedName

data class TrusteeByDeviceViewResult(
    @SerializedName("total_rows")
    val totalRows: Int,
    val offset: Int,
    val rows: List<TrusteeByDevice>
)

data class TrusteeByDevice(
    val id: String,
    @SerializedName("key")
    val deviceId: String,
    @SerializedName("value")
    val name: String
)

/**
 * Builds first query param without escaping ":" character
 */
fun quotingQuery(name: String, value: String): String {
    return "?" + """$name="$value"""".replace("\"", "%22")
}
