package org.shadownova.vollzanbel.repository

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Embeddable
data class CharacterSpellId(
    @Column(name = "character_id")
    val characterId: Long,

    @Column(name = "spell_index")
    val spellIndex: String
)

@Entity
@Table(name = "character_spells")
data class CharacterSpell(
    @EmbeddedId
    val id: CharacterSpellId,

    @Column(name = "is_learned")
    val isLearned: Boolean,

    @Column(name = "is_prepared")
    val isPrepared: Boolean,

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant? = null,

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: Instant? = null
)

@Repository
interface CharacterSpellRepository : JpaRepository<CharacterSpell, CharacterSpellId> {
    // Find all CharacterSpell rows for a given character id. We query the embedded id
    // property using the standard JPA property path.
    @Query("SELECT c FROM CharacterSpell c WHERE c.id.characterId = :charId")
    fun findByCharacterId(charId: Long): List<CharacterSpell>

}
