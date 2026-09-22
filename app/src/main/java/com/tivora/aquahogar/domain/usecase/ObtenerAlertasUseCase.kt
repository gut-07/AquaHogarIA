package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.domain.model.AlertaConsumo
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObtenerAlertasUseCase @Inject constructor(
    private val ensureVivienda: EnsureViviendaUseCase,
    private val repository: ConsumoRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AlertaConsumo>> = flow {
        emit(ensureVivienda())
    }.flatMapLatest { vivienda ->
        repository.observeAlertas(vivienda.id)
    }
}
