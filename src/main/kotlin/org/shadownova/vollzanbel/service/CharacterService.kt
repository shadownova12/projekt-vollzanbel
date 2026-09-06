package org.shadownova.vollzanbel.service

import org.shadownova.vollzanbel.dto.CharacterSpellRequest
import org.shadownova.vollzanbel.repository.CharacterSpell
import org.shadownova.vollzanbel.repository.CharacterSpellId
import org.shadownova.vollzanbel.repository.CharacterSpellRepository
import org.shadownova.vollzanbel.repository.CharacterRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class CharacterService(
    val characterSpellRepository: CharacterSpellRepository,
    val characterRepository: CharacterRepository,
) {

    /**
     * Delete a character and its character-owned spell selections atomically.
     * Shared spell and equipment-library data is deliberately not touched.
     */
    @Transactional
    fun deleteCharacter(userId: Long, name: String): Boolean {
        val character = characterRepository.findByUserIdAndName(userId, name)
            ?: return false

        characterSpellRepository.deleteByCharacterId(character.id)
        characterRepository.delete(character)
        return true
    }

    fun addSpell(charId: Long, spellRequest: CharacterSpellRequest): CharacterSpell {
        val characterSpell = CharacterSpell(
            CharacterSpellId(charId, spellRequest.spellIndex),
            spellRequest.isLearned,
            spellRequest.isPrepared
        )
        return characterSpellRepository.save(characterSpell)

    }

    fun removeSpell(charId: Long, spellIndex: String) {
        characterSpellRepository.deleteById(CharacterSpellId(charId, spellIndex))
    }

    fun updateSpell(charId: Long, spellRequest: CharacterSpellRequest): CharacterSpell? {
        val id = CharacterSpellId(charId, spellRequest.spellIndex)

        val characterSpell = characterSpellRepository
            .findById(id)
            .orElse(null)
            ?: return null

        return characterSpellRepository.save(
            characterSpell.copy(
                isLearned = spellRequest.isLearned,
                isPrepared = spellRequest.isPrepared
            )
        )
    }

    fun getSpells(charId: Long): List<CharacterSpell> {
        return characterSpellRepository.findByCharacterId(charId)
    }
}
