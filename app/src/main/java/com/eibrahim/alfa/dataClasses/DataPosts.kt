package com.eibrahim.alfa.dataClasses

data class DataPosts (

    var postText : String? = null,
    var imageUrl : String? = null,
    var time : Long? = null,
    var userId: UserId? = null,
    var postId : String? = null,
    var comments : List<String>? = emptyList()
    )


