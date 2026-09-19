package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.Background
import org.shadownova.vollzanbel.repository.BackgroundRepository
import org.springframework.stereotype.Service

@Service
class BackgroundService(private val repository: BackgroundRepository) {
    fun getAllBackgrounds(): List<Background> = repository.findAllByOrderByNameAsc()
    fun findBackground(index: String): Background? = repository.findById(index).orElse(null)
}
