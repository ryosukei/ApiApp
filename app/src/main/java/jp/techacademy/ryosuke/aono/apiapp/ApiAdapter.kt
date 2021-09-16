package jp.techacademy.ryosuke.aono.apiapp

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

// 実際のListView
class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Shop>()
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null
    var onClickAddFavorite: ((Shop) -> Unit)? = null

    fun refresh(list: List<Shop>){
        items.apply{
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }
    // ApiItemViewHolder(リストのviewを担当するHolder)を返す
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApiItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
    }
    // リストビューの箱を作る
    class ApiItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    // ApiItemViewHolderに中身をいれるメソッドを呼び出す
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if( holder is ApiItemViewHolder){
           updateApiItemViewHolder(holder, position)
       }
    }
    // ApiItemViewHolderに中身をいれるメソッド
    private fun updateApiItemViewHolder(holder: ApiItemViewHolder,position: Int){
        val data = items[position]
        val isFavorite = FavoriteShop.findBy(data.id) != null
        holder.apply {
            rootView.apply {
                // 偶数番目と奇数番目で背景色を変更させる
                setBackgroundColor(ContextCompat.getColor(context,
                    if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray))
            }
            // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
            nameTextView.text = data.name
            // Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
            Picasso.get().load(data.logoImage).into(imageView)
            // 白抜きの星マークの画像を指定
            favoriteImageView.apply{
                setImageResource(if (isFavorite)R.drawable.ic_star else R.drawable.ic_star_border)
                setOnClickListener{
                    if(isFavorite){
                        onClickDeleteFavorite?.invoke(data)
                    }else{
                        onClickAddFavorite?.invoke(data)
                    }
                    notifyItemChanged(position)
                }
            }

        }
    }
}