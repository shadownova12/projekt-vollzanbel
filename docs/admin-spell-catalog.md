# Admin spell catalog writes

`POST /vollzanbel-api/spells` adds a spell to the shared catalog. The catalog is visible to every user, so the endpoint is guarded by a server-side admin token. `X-User-Id` does not grant write access.

Before starting Vollzanbel, configure a strong secret in the server environment:

```sh
export SPELL_ADMIN_TOKEN='use-a-long-random-secret'
```

Keep this token on the server or in an admin-only shell. Do not put it in the Zoltraak client or any browser code. If the token is unset, the endpoint fails closed with `503 Service Unavailable`.

Example request:

```sh
curl -X POST 'http://localhost:8080/vollzanbel-api/spells' \
  -H "X-Admin-Token: $SPELL_ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{
    "index": "pulse-wave",
    "name": "Pulse Wave",
    "level": 3,
    "school": "Evocation",
    "source": "Explorer\u0027s Guide to Wildemount",
    "description": "Spell description text.",
    "higherLevel": "",
    "range": "Self (30-foot cone)",
    "components": "V, S",
    "material": "",
    "ritual": false,
    "duration": "Instantaneous",
    "concentration": false,
    "castingTime": "1 action",
    "url": "https://dnd5e.wikidot.com/spell:pulse-wave"
  }'
```

The `index` must be a lowercase, hyphenated canonical spell ID. The endpoint responds with `201 Created` when inserted, `400 Bad Request` for invalid fields, `403 Forbidden` for a missing or incorrect token, `409 Conflict` if that spell ID already exists, and `503 Service Unavailable` when no admin token is configured.
