package jp.techacademy.ryosuke.aono.apiapp

interface FragmentCallback {

    fun onAddFavolite(shop: Shop)

    fun onClickItem(shop: Shop)
    fun onClickItem(favoriteShop: FavoriteShop)
    // なんでDelteだけid？
    fun onDeleteFavorite(id:String)
}