package jp.techacademy.ryosuke.aono.apiapp

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private var page = 0

    private var isLoading = false

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
                fragmentCallback?.onAddFavolite(it)
            }
            onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        Log.d("recyclerView","recyclerView")
        recyclerView.apply{
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy == 0){
                        return
                    }
                    val totalCount = apiAdapter.itemCount
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                    if (!isLoading && lastVisibleItem >= totalCount - 6) { // 読み込み中でない、かつ、現在のスクロール位置から下に5件見えていないアイテムがある
                        updateData(true)
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    fun updateView(){
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updateData(isAdd: Boolean = false){
        if (isLoading) {
            return
        } else {
            isLoading = true
        }
        if (isAdd) {
            page ++
        } else {
            page = 0
        }
        val start = page * COUNT + 1
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
                    updateRecyclerView(listOf(), isAdd)
                }
                isLoading = false
            }

            override fun onResponse(call: Call, response: Response) {
                var list = listOf<Shop>()
                response.body?.string()?.also{
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post{
                    updateRecyclerView(list,isAdd)
                }
                isLoading = false
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>, isAdd: Boolean){
        if (isAdd) {
            apiAdapter.add(list)
        } else {
            apiAdapter.refresh(list)
        }
        swipeRefreshLayout.isRefreshing = false
    }
    companion object {
        private const val COUNT = 20 // 1回のAPIで取得する件数
    }
}