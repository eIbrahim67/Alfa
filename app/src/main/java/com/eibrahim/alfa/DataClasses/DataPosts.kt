package com.eibrahim.alfa.DataClasses

data class DataPosts (

    var postText : String? = null,
    var imageUrl : String? = null,
    var time : Long? = null,
    var userId: UserId? = null,
    var likes : List<String>? = emptyList(),
    var dislikes : List<String>? = emptyList(),
    var postId : String? = null,
    var comments : List<String>? = emptyList()
    )


