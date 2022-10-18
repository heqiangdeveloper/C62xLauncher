package com.chinatsp.vehicle.controller.semantic

import android.os.Parcel
import android.os.Parcelable

data class Slots(val serial: String) : Parcelable {
    var artist: String = ""
    var song: String = ""
    var album: String = ""
    var moreArtist: String = ""
    var band: String = ""
    var gender: String = ""
//    var source: Any? = null
    var sourceType: String = ""
    var genre: String = ""
    var area: String = ""
    var lang: String = ""
    var version: String = ""
    var season: String = ""
    var episode: String = ""
    var mediaSource: String = ""
    var insType: String = ""
    var waveband: String = ""
    var code: String = ""
    var program: String = ""
    var name: String = ""
    var nameOrig: String = ""
    var presenter: String = ""
    var tags: String = ""
    var series: String = ""
    var location: String = ""
    var category: String = ""
    var channel: String = ""
    var airflowDirection: String = ""
    var temperature: Any? = ""
    var temperatureGear: String = ""
    var fanSpeed: Any? = ""
    var direction: String = ""
    var mode: String = ""

    var nameValue: Any? = ""
    var color: String = ""

    var text: String = ""
    var operation: String = ""

    constructor(parcel: Parcel): this("") {
        artist = parcel.readString().toString()
        song = parcel.readString().toString()
        album = parcel.readString().toString()
        moreArtist = parcel.readString().toString()
        band = parcel.readString().toString()
        gender = parcel.readString().toString()
//        source = parcel.readString().toString()
        sourceType = parcel.readString().toString()
        genre = parcel.readString().toString()
        area = parcel.readString().toString()
        lang = parcel.readString().toString()
        version = parcel.readString().toString()
        season = parcel.readString().toString()
        episode = parcel.readString().toString()
        mediaSource = parcel.readString().toString()
        insType = parcel.readString().toString()
        waveband = parcel.readString().toString()
        code = parcel.readString().toString()
        program = parcel.readString().toString()
        name = parcel.readString().toString()
        nameOrig = parcel.readString().toString()
        presenter = parcel.readString().toString()
        tags = parcel.readString().toString()
        series = parcel.readString().toString()
        location = parcel.readString().toString()
        category = parcel.readString().toString()
        channel = parcel.readString().toString()
        airflowDirection = parcel.readString().toString()
        temperature = parcel.readString().toString()
        temperatureGear = parcel.readString().toString()
        fanSpeed = parcel.readString().toString()
        direction = parcel.readString().toString()
        mode = parcel.readString().toString()
        nameValue = parcel.readString().toString()
        color = parcel.readString().toString()
        text = parcel.readString().toString()
        operation = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(artist)
        parcel.writeString(song)
        parcel.writeString(album)
        parcel.writeString(moreArtist)
        parcel.writeString(band)
        parcel.writeString(gender)
//        parcel.writeString(source)
        parcel.writeString(sourceType)
        parcel.writeString(genre)
        parcel.writeString(area)
        parcel.writeString(lang)
        parcel.writeString(version)
        parcel.writeString(season)
        parcel.writeString(episode)
        parcel.writeString(mediaSource)
        parcel.writeString(insType)
        parcel.writeString(waveband)
        parcel.writeString(code)
        parcel.writeString(program)
        parcel.writeString(name)
        parcel.writeString(nameOrig)
        parcel.writeString(presenter)
        parcel.writeString(tags)
        parcel.writeString(series)
        parcel.writeString(location)
        parcel.writeString(category)
        parcel.writeString(channel)
        parcel.writeString(airflowDirection)
        parcel.writeString(temperature?.toString() ?: "")
        parcel.writeString(temperatureGear)
        parcel.writeString(fanSpeed?.toString() ?: "")
        parcel.writeString(direction)
        parcel.writeString(mode)
        parcel.writeString(nameValue?.toString() ?: "")
        parcel.writeString(color)
        parcel.writeString(text)
        parcel.writeString(operation)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Slots(version='$version', name='$name', insType='$insType', temperature='$temperature', fanSpeed='$fanSpeed', mode='$mode')"
    }

    companion object CREATOR : Parcelable.Creator<Slots> {
        override fun createFromParcel(parcel: Parcel): Slots {
            return Slots(parcel)
        }

        override fun newArray(size: Int): Array<Slots?> {
            return arrayOfNulls(size)
        }
    }



}