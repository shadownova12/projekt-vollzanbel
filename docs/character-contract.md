# Character contract

The API and multiplatform client have matching Kotlin contracts, using Jackson and
kotlinx.serialization respectively. They live in separate Maven/Gradle projects;
changes to wire fields must update both projects and their character-sheet.json
contract fixtures/tests together.

- `CharacterSheet`: editable game state. Nested types cover abilities, combat,
  inventory, spells, narrative, and progression. It has no database ID or owner.
- `PlayerCharacter`: saved record (`id`, `userId`, stable `name` lookup key,
  `avatarId`, and `characterSheet`).
- `CreateCharacterRequest`: `name`, `avatarId`, `characterSheet`. There is no fake ID.
- `UpdateCharacterRequest`: `id`, `avatarId`, `characterSheet`. The path identifies
  the saved character; ID must match. Ownership comes from `X-User-Id` for both writes.
- `CharacterListResponse`: `characters: List<CharacterSummary>`.
- Create/update return `CharacterSummary`; the client fetches the saved record after creation.

The stable record name is the URL lookup key; the sheet name is editable display
text. Updating a sheet does not rename the lookup key. The JSON property `race`
continues to represent Kotlin `species`. Existing enum spellings, integer-keyed
spell slot objects, and optional-field defaults remain unchanged. Unknown legacy
request metadata (`userId`, `name`, or a fake creation `id`) is ignored by request
DTOs, never trusted for ownership. Nested sheet data must conform to typed models.

`CharacterSnapshotCodec` owns GZIP and JSON storage. It reads legacy compressed
sheets directly and writes version 1 `{version, avatarId, characterSheet}` snapshots
in the same database column. Legacy sheets receive the old default avatar;
previously discarded avatar choices cannot be recovered. No database migration is
required. Deploy the API before the client; old API binaries cannot read the new
storage envelope, so rolling back requires converting version 1 snapshots first.

The full sheet snapshot is the editor's persistence source, including spell and
inventory details. The separate `/characters/{id}/spells` endpoints represent
legacy spell selections in a separate table, not the full sheet spellbook.

Verification: API `mvn test`; client `./gradlew :shared:jvmTest
:shared:compileKotlinJs :shared:compileKotlinWasmJs`. The same fixture exercises
both serializers, including `race`, Boolean `is*` properties, enum values, nested
inventory charges, battle history, spell slots, and pending level-up drafts.
