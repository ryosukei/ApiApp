package jp.techacademy.ryosuke.aono.apiapp

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

// 取得したjsonデータをApiResponseへ変換する
class ApiFragment: Fragment()  {
    private val apiAdapter by lazy {ApiAdapter(requireContext())}
    private val handler = android.os.Handler(Looper.getMainLooper())

    private var fragmentCallback : FragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_api, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiAdapter.apply{
            onClickAddFavorite = {
                fragmentCallback?.onDeleteFavolite(it.id)
            }
            onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            // Itemをクリックしたとき
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        recyclerView.apply{
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateView(){
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updateData(){
        val url = StringBuilder()
            .append(getString(R.string.base_url))
            .append("?key=").append(getString(R.string.api_key))
            .append("&start=").append(1)
            .append("&count=").append(COUNT)
            .append("&keyword=").append(getString(R.string.api_keyword))
            .append("&format=json")
            .toString()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handler.post {
                    updateRecyclerView(listOf())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var list = listOf<Shop>()
                response.body?.string()?.also{
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post{
                    updateRecyclerView(list)
                }
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>){
        apiAdapter.refresh(list)
        swipeRefreshLayout.isRefreshing = false
    }
    companion object {
        private const val COUNT = 20 // 1回のAPIで取得する件数
    }
}