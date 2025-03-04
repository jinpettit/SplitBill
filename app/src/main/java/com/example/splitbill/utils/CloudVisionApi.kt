package com.example.splitbill.utils

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CloudVisionApi {
    @POST("v1/images:annotate")
    fun annotateImage(
        @Query("key") apiKey: String,
        @Body request: VisionRequest
    ): Call<VisionResponse>
}

data class VisionRequest(
    @SerializedName("requests") val requests: List<Request>
)

data class Request(
    @SerializedName("image") val image: Image,
    @SerializedName("features") val features: List<Feature>
)

data class Image(
    @SerializedName("content") val content: String
)

data class Feature(
    @SerializedName("type") val type: String = "TEXT_DETECTION"
)

data class VisionResponse(
    @SerializedName("responses") val responses: List<VisionResponseItem>
)

data class VisionResponseItem(
    @SerializedName("textAnnotations") val textAnnotations: List<TextAnnotation>?
)

data class TextAnnotation(
    @SerializedName("description") val description: String
)