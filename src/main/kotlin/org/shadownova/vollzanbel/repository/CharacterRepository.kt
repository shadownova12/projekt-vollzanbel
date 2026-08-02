package org.shadownova.vollzanbel.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Entity
@Table(name = "characters")
data class CharacterRow(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0,

	@Column(name = "user_id", nullable = false)
	val userId: Long,

	@Column(name = "name", nullable = false)
	val name: String = "",

	@Column(name = "data", nullable = false)
	val compressedData: ByteArray
)

@Repository
interface CharacterRepository : JpaRepository<CharacterRow, Long> {

	@Query(
		"""
		SELECT c.name
		FROM CharacterRow c
		WHERE c.userId = :userId
		ORDER BY c.name
		"""
	)
	fun listNames(@Param("userId") userId: Long): List<String>

	@Query("SELECT c FROM CharacterRow c WHERE c.userId = :userId AND c.name = :name")
	fun findByUserIdAndName(userId: Long, name: String): CharacterRow?

	@Modifying
	@Query(
		"""
		DELETE FROM CharacterRow c
		WHERE c.userId = :userId
		AND c.name = :name
		"""
	)
	fun deleteByUserIdAndName(@Param("userId") userId: Long, @Param("name") name: String): Int
}
