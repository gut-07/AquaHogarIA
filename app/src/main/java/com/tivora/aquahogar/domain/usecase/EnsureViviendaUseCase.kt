package com.tivora.aquahogar.domain.usecase

import com.tivora.aquahogar.domain.model.Vivienda
import com.tivora.aquahogar.domain.repository.ConsumoRepository
import javax.inject.Inject

class EnsureViviendaUseCase @Inject constructor(
    private val repository: ConsumoRepository
) {
    suspend operator fun invoke(): Vivienda {
        repository.getVivienda()?.let { return it }
        val seed = Vivienda(
            nombre = "Mi hogar",
            numeroHabitantes = 4,
            metaMensualLitros = 15_000.0
        )
        val id = repository.upsertVivienda(seed)
        return seed.copy(id = id)
    }
}
