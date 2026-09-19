package org.shadownova.vollzanbel.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice(assignableTypes = [CharacterController::class])
class CharacterErrors {
    @ExceptionHandler(ResponseStatusException::class)
    fun status(error: ResponseStatusException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(error.statusCode).body(mapOf("message" to (error.reason ?: "Character request failed")))
}
