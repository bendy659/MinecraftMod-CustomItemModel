package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.Serializable

@Serializable
sealed class IPredicateData {
    abstract val name: String
}

@Serializable
data class PredicateDataInt(
    override val name: String,
    val value: Int
) : IPredicateData()

@Serializable
data class PredicateDataFloat(
    override val name: String,
    val value: Float
) : IPredicateData()

@Serializable
data class PredicateDataString(
    override val name: String,
    val value: String
) : IPredicateData()