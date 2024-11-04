package com.example.datasetapp.data.source.network.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DatasetResponse(

	@field:SerializedName("values")
	val values: Values? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class Values(

	@field:SerializedName("url")
	val url: String? = null
) : Parcelable
