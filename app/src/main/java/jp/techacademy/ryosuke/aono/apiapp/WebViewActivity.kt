package jp.techacademy.ryosuke.aono.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        var id = intent.getStringExtra(KEY_ID).toString()
        var name = intent.getStringExtra(KEY_NAME).toString()
        var imageUrl = intent.getStringExtra(KEY_IMAGEURL).toString()
        var url = intent.getStringExtra(KEY_URL).toString()

        val shop = FavoriteShop()
        shop.id = id
        shop.name = name
        shop.imageUrl = imageUrl
        shop.url = url
        var isFavorite = FavoriteShop.findBy(id) != null
        webView.loadUrl(url)
        changeText(isFavorite)

        webViewBtn.setOnClickListener { view ->
            if (isFavorite) {
                Log.d("isFab", "onClickDeleteFavorite")
                FavoriteShop.delete(id)
            } else {
                Log.d("isFab", "onClickAddFavorite")
                FavoriteShop.insert(shop)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("呼ばれた","aaa")
        Log.d("KEYCODE_BACK",KeyEvent.KEYCODE_BACK.toString())
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("呼ばれた","きめた")
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val KEY_ID = "key_id"
        private const val KEY_IMAGEURL = "key_imageUrl"
        private const val KEY_NAME = "key_name"
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, data: Shop) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(KEY_ID, data.id)
            intent.putExtra(KEY_IMAGEURL, data.logoImage)
            intent.putExtra(KEY_NAME, data.name)
            intent.putExtra(KEY_URL, if (data.couponUrls.sp.isNotEmpty()) data.couponUrls.sp else data.couponUrls.pc)
            activity.startActivity(intent)
        }
        fun start(activity: Activity, data: FavoriteShop) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(KEY_ID, data.id)
            intent.putExtra(KEY_IMAGEURL, data.imageUrl)
            intent.putExtra(KEY_NAME, data.name)
            intent.putExtra(KEY_URL, data.url)
            activity.startActivity(intent)
        }
    }
}