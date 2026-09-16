package org.shadownova.vollzanbel.dto

data class HitPoints(val current: Int, val max: Int, val temp: Int = 0)

data class HitDice(
    val expression: String = "",
    val spent: Int = 0,
)

data class DeathSaves(
    val successes: Int = 0,
    val failures: Int = 0,
)

data class CombatState(
    val initiative: Int = 0,
    val actionUsed: Boolean = false,
    val bonusActionUsed: Boolean = false,
    val reactionUsed: Boolean = false,
    val movementUsed: Int = 0,
    val action: String = "",
    val bonusAction: String = "",
    val reaction: String = "",
    val notes: String = "",
    val events: List<String> = emptyList(),
    val battles: List<BattleRecord> = emptyList(),
)

data class BattleRecord(
    val id: Int,
    val name: String,
    val initiative: Int,
    val turns: List<BattleTurn> = emptyList(),
    val ended: Boolean = false,
)

data class BattleTurn(
    val number: Int,
    val action: String = "",
    val bonusAction: String = "",
    val reaction: String = "",
    val movement: Int = 0,
    val notes: String = "",
    val events: List<String> = emptyList(),
    val hp: Int = 0,
    val tempHp: Int = 0,
)

data class CharacterEffect(
    val id: String,
    val name: String,
    val source: String = "",
    val notes: String = "",
    val concentration: Boolean = false,
    val roundsRemaining: Int? = null,
    val endsOn: ResourceReset = ResourceReset.MANUAL,
)

data class TrackedResource(
    val id: String = "",
    val name: String = "",
    val current: Int = 0,
    val maximum: Int = 0,
    val reset: ResourceReset = ResourceReset.MANUAL,
    val source: String = "",
    val notes: String = "",
)

enum class ResourceReset { MANUAL, SHORT_REST, LONG_REST }
