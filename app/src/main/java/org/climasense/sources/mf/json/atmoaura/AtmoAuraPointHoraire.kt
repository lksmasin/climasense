package org.climasense.sources.mf.json.atmoaura

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

@Serializable
data class AtmoAuraPointHoraire(
    @SerialName("datetime_echeance") @Serializable(DateSerializer::class) val datetimeEcheance: Date,
    @SerialName("indice_atmo") val indiceAtmo: Int?,
    val concentration: Int?
)
