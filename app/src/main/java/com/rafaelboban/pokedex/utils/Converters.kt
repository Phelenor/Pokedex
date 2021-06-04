package com.rafaelboban.pokedex.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.rafaelboban.pokedex.model.*
import java.util.*


class Converters {

    @TypeConverter
    fun listToJson(value: List<*>?) = Gson().toJson(value)

    @TypeConverter
    fun abilityInfoJsonToList(value: String) = Gson().fromJson(value, Array<AbilityInfo>::class.java).toList()

    @TypeConverter
    fun statInfoJsonToList(value: String) = Gson().fromJson(value, Array<StatInfo>::class.java).toList()

    @TypeConverter
    fun typeInfoJsonToList(value: String) = Gson().fromJson(value, Array<TypeInfo>::class.java).toList()

    @TypeConverter
    fun nameJsonToList(value: String) = Gson().fromJson(value, Array<Name>::class.java).toList()

    @TypeConverter
    fun generaJsonToList(value: String) = Gson().fromJson(value, Array<Genera>::class.java).toList()

    @TypeConverter
    fun flavorJsonToList(value: String) = Gson().fromJson(value, Array<FlavorTextEntry>::class.java).toList()

}