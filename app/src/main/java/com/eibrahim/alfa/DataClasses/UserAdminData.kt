package com.eibrahim.alfa.DataClasses

data class UserAdminData(

    var followers : Long? = null,
    var following : Long? = null,
    var gender : String? = null,
    var imageUrl : String? = null,
    var imageBackUrl : String? = null,
    var name : String? = null,
    var noPosts : Long? = null,
    var points : Long? = null,
    var specialist : String? = null,
    var userName : String? = null,
    var webSite : String? = null,
    var whatsApp : String? = null,
    var postsBookmarks : List<String>? = emptyList(),
    var postsId : List<String>? = emptyList()

    )
