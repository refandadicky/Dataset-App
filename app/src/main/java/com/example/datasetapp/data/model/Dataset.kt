package com.example.datasetapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Dataset(
    var imgUrl: String,
) : Parcelable
