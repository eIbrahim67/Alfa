package com.eibrahim.alfa.dataClasses

data class ReadDataPosts (

    var postText : String? = null,
    var imageUrl : String? = null,
    var time : Long? = null,
    var userId: UserId? = null,
    var postId : String? = null,
    var isBookmarked: Boolean? = false,
    var isLoved: Boolean? = false,
    var noLikes : Long = 0,
    var comments : List<String>? = emptyList()
    )


