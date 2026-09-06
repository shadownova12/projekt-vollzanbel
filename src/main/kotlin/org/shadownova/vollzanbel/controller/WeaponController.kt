package org.shadownova.vollzanbel.controller

import org.shadownova.vollzanbel.repository.Weapon
import org.shadownova.vollzanbel.service.WeaponService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/weapons")
class WeaponController(private val service: WeaponService) {
    @GetMapping
    fun getAll(): List<Weapon> = service.getAllWeapons()

    @GetMapping("/{index}")
    fun getOne(@PathVariable index: String): ResponseEntity<Weapon> =
        service.findWeapon(index)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
