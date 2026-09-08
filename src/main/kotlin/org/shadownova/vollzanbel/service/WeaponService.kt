package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.repository.Weapon
import org.shadownova.vollzanbel.repository.WeaponRepository
import org.springframework.stereotype.Service

@Service
class WeaponService(private val repository: WeaponRepository) {
    fun getAllWeapons(): List<Weapon> = repository.findAllByOrderByNameAsc()
    fun findWeapon(index: String): Weapon? = repository.findById(index).orElse(null)
}
