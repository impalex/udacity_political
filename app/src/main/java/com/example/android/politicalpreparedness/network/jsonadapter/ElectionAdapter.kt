package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.*

class ElectionAdapter: JsonAdapter<Division>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Division {
        val ocdDivisionId = reader.nextString()
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
            .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
            .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Division?) {
        value?.let { writer.value(value.id) }
    }
}