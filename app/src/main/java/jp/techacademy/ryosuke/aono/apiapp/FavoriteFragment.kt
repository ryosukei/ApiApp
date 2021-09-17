package jp.techacademy.ryosuke.aono.apiapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment:Fragment() {
    private val favoriteAdapter by lazy {FavoriteAdapter(requireContext())}
    private var fragmentCallback: FragmentCallback? = null

    // FragmentがActivityにアタッチされたとき
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentCallback){
            fragmentCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter.apply{
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }

        recyclerView.apply{
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        swipeRefreshLayout.setOnRefreshListener{
            updateData()
        }
        updateData()
    }
    fun updateData(){
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }
}