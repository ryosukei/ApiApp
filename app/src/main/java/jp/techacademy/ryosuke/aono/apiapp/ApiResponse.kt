package jp.techacademy.ryosuke.aono.apiapp
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ApiResponse(
    @SerializedName("results")
    var results: Results
)

data class Results(
    @SerializedName("shop")
    var shop: List<Shop>
)

data class Shop(
    @SerializedName("coupon_urls")
    val couponUrls: CouponUrls,
    @SerializedName("id")
    val id: String,
    @SerializedName("logo_image")
    val logoImage: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String
): Serializable

data class CouponUrls(
    @SerializedName("pc")
    var pc: String,
    @SerializedName("sp")
    var sp: String
): Serializable