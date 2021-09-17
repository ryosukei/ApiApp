package jp.techacademy.ryosuke.aono.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val data = intent.getSerializableExtra(KEY)
        var url: String = ""
        var id: String = ""

        if(data is FavoriteShop){
            url = data.url
            id = data.id
        } else if (data is Shop) {
            url = if (data.couponUrls.sp.isNotEmpty()) {
                data.couponUrls.sp
            } else {
                data.couponUrls.pc
            }
            id = data.id
        }
        var isFavorite = FavoriteShop.findBy(id) != null
        webView.loadUrl(url)
        changeText(isFavorite)

        webViewBtn.setOnClickListener { view ->
            if (isFavorite) {
                Log.d("isFab", "onClickDeleteFavorite")
                FavoriteShop.delete(id)
            } else {
                Log.d("isFab", "onClickAddFavorite")
                if (data is Shop) {
                    FavoriteShop.insert(FavoriteShop().apply {
                        id = data.id
                        name = data.name
                        imageUrl = data.logoImage
                        url =
                            if (data.couponUrls.sp.isNotEmpty()) data.couponUrls.sp else data.couponUrls.pc
                    })
                }
            }
            isFavorite = !isFavorite
            changeText(isFavorite)
        }
    }

    fun changeText(isFavorite: Boolean){
        if(isFavorite){
            webViewBtn.text = "お気に入りから削除"
        }else{
            webViewBtn.text = "お気に入りに追加"
        }
    }

    companion object {
        private const val KEY = "shop_data"
        fun start(activity: Activity, data: Shop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY, data))
        }
        fun start(activity: Activity, data: FavoriteShop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY, data))
        }
    }
}