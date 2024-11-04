package com.example.datasetapp.di

import com.example.datasetapp.data.repository.DatasetRepository
import com.example.datasetapp.data.source.network.service.ApiConfig
import com.example.datasetapp.data.source.network.service.DatasetApiService
import com.example.datasetapp.view.VerifikasiDataViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object AppModules {
    val networkModule =
        module {
            single<DatasetApiService> { ApiConfig.invoke() }
        }

    private val repository =
        module {
            single { DatasetRepository(get()) }
        }

    private val viewModelModule =
        module {
            viewModelOf(::VerifikasiDataViewModel)
        }


    val modules =
        listOf(
            networkModule,
            repository,
            viewModelModule
        )
}