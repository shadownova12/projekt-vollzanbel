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

Character setup now includes `backgroundId`, `backgroundUrl`, `backgroundFeature`,
`toolProficiencies`, `otherProficiencies`, `setupReviewed`, and
`appliedSetupGrants`; the profile also stores `ideals`, `bonds`, and `flaws`.
All have backward-compatible defaults. Initiative and passive Perception are
derived from scores and proficiency rather than stored redundantly.

Create trims the lookup name, rejects existing names case-insensitively per owner,
and has a database uniqueness constraint on `(user_id, name)`. Before deploying
to an existing database, check duplicate names and resolve them deliberately;
schema updates must not delete or merge characters automatically. Character names
remain stable lookup keys. Sheet display text is not a rename operation.
The API validates numeric bounds, required identity, duplicate skill/save entries,
spell slots, currency, and inventory. Failures return a readable `message`.

The client writes each pending snapshot and its last confirmed snapshot to an
owner-scoped local draft before debouncing network writes. JVM drafts are atomic
JSON files; browser drafts use localStorage. A successful response removes only
the acknowledged draft (newer edits remain). Restart restores pending edits and
resumes saving when their character is opened. Confirmed sheets remain on the
server; this is draft recovery, not a complete offline character library.
Save now/Retry now wakes the queue; browser and desktop close guards warn about
unconfirmed edits. Rest, avatar, and level-up changes use the same queue.

Setup grant previews use the 2014 reference API and apply fixed class/species
grants, plus the Folk Hero seed's fixed grants. Ability increases require an
explicit opt-in; a persisted source marker prevents applying the same grant
twice. Choice-dependent proficiencies, gear, features, and spells remain editable
and are reviewed manually. Changing identity retains earlier grants for explicit
review. New character forms remain open on failed creation.

Tests use an isolated H2 database in PostgreSQL mode and disable remote startup
sync with `catalog.sync.enabled=false`. Production retains its PostgreSQL config.
`CharacterDatabaseTest` covers HTTP create/update/read/delete, ownership, duplicate
names, validation, compressed storage, and the new fields through real JPA.

Verification: API `mvn test`; client `./gradlew :shared:jvmTest
:shared:compileKotlinJs :shared:compileKotlinWasmJs`. The same fixture exercises
both serializers, including `race`, Boolean `is*` properties, enum values, nested
inventory charges, battle history, spell slots, and pending level-up drafts.
