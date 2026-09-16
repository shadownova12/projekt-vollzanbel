package org.shadownova.vollzanbel.dto

enum class RuleSet { DND_2014, DND_2024 }

enum class LevelUpCategory { GENERAL, SPECIES, CLASS, SUBCLASS, FEATS, SPELLCASTING }

enum class LevelUpStepKind { CHECK, NUMBER, MANUAL }

data class LevelUpStep(
    val id: String,
    val category: LevelUpCategory,
    val title: String,
    val description: String,
    val kind: LevelUpStepKind = LevelUpStepKind.CHECK,
    val required: Boolean = true,
    val completed: Boolean = false,
    val value: String = "",
    val source: String = "",
)

data class PendingLevelUp(
    val fromLevel: Int,
    val toLevel: Int,
    val ruleSet: RuleSet,
    val steps: List<LevelUpStep>,
)
