package jp.techacademy.ryosuke.aono.apiapp

interface FragmentCallback {
    fun onAddFavolite(shop: Shop)

    fun onClickItem(url: String)
    // なんでDelteだけid？
    fun onDeleteFavolite(id:String)
}