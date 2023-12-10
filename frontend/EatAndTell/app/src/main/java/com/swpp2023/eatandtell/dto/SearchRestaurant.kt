package com.swpp2023.eatandtell.dto
data class GetSearchedRestResponse(
    val data : List<SearchedRestDTO>
)

//data class GetSearchedRestRequest(
//    val query : String,
//    val x : String?,
//    val y : String?,
//)

//우리 서버 restaurant 말고 kakao api에서만 사용

data class SearchedRestDTO(
    val id : Int,
    val place_name : String,
    val road_address_name : String,
    val category_name : String,
    val x : String,
    val y : String,
)