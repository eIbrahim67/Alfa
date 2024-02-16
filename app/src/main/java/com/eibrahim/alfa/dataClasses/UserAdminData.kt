package com.eibrahim.alfa.dataClasses

data class UserAdminData(

    var followers :  List<String>? = emptyList(),
    var following : List<String>? = emptyList(),

    var gender : String? = null,
    var imageUrl : String? = null,

    var name : String? = null,
    var specialist : String? = null,

    var userName : String? = null,
    var webSite : String? = null,

    var whatsApp : String? = null,
    var about : String? = null,

    var postsBookmarks : List<String>? = emptyList(),
    var postsId : List<String>? = emptyList()

    )
