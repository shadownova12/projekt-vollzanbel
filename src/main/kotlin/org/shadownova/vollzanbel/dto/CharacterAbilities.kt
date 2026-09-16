package org.shadownova.vollzanbel.dto

enum class AbilityScore { STR, DEX, CON, INT, WIS, CHA }

data class CharacterStats(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
)

enum class ProficiencyLevel { NONE, PROFICIENT, EXPERT }

data class SkillProficiency(
    val name: String,
    val ability: AbilityScore,
    val proficiency: ProficiencyLevel = ProficiencyLevel.NONE,
)

data class SavingThrowProficiency(
    val ability: AbilityScore,
    val proficiency: ProficiencyLevel = ProficiencyLevel.NONE,
)

data class ClassFeature(
    val id: String = "",
    val name: String = "",
    val description: String = "",
)

data class SpeciesTrait(
    val id: String = "",
    val name: String = "",
    val description: String = "",
)

data class Feat(
    val id: String = "",
    val name: String = "",
    val description: String = "",
)

data class WeaponMastery(
    val id: String = "",
    val name: String = "",
    val mastery: String = "",
    val description: String = "",
)

data class ArmorTraining(
    val light: Boolean = false,
    val medium: Boolean = false,
    val heavy: Boolean = false,
    val shields: Boolean = false,
)

data class WeaponTraining(
    val simple: Boolean = false,
    val martial: Boolean = false,
    val improvised: Boolean = false,
)
