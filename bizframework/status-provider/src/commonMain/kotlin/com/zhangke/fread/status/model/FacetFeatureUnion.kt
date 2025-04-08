package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class Facet(
    val byteStart: Long,
    val byteEnd: Long,
    val features: List<FacetFeatureUnion>,
)

@Serializable
sealed interface FacetFeatureUnion {

    @Serializable
    data class Mention(val did: String) : FacetFeatureUnion

    @Serializable
    data class Tag(val tag: String) : FacetFeatureUnion

    @Serializable
    data class Link(val uri: String) : FacetFeatureUnion
}
