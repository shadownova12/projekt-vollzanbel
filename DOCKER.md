# Run Vollzanbel with Docker

Docker Compose builds and runs only the Spring Boot API. It connects to the PostgreSQL database configured in `.env`; the database is managed outside this Compose stack.

## Configure and start

The configured direct Supabase hostname is IPv6-only, and this Docker network has no IPv6 route. In the Supabase dashboard, open **Connect** and select **Session pooler**. Use that connection's host with port `5432`, database name, and password in `.env`. The session pooler username is `postgres.<project-ref>`. For `DB_URL`, use JDBC form, for example `jdbc:postgresql://<pooler-host>:5432/postgres?sslmode=require`.

Then, from this directory, run:

```sh
docker compose up --build -d
```

The API is available at `http://localhost:8080/vollzanbel-api`. For example:

```sh
curl http://localhost:8080/vollzanbel-api/spells
```

Useful commands:

```sh
docker compose logs -f app
docker compose ps
docker compose down
```

`docker compose down` stops and removes the API container. Use `docker compose up --build -d` again to rebuild after code changes.

`.env` is ignored by Git and excluded from the image build context. `.env.example` lists the fields. If you use a PostgreSQL server running directly on this Mac instead of Supabase, change `localhost` in its URL to `host.docker.internal` so the container can reach the Mac host.

`SPELL_ADMIN_TOKEN` protects `POST /vollzanbel-api/spells`. Keep it server-side; do not put it in the client app. If it is blank, catalog writes remain disabled. Port 8080 is published on the host's network interfaces so other devices on the LAN can reach the API. A host firewall or router may still block inbound connections. For a public server, put a reverse proxy with HTTPS in front of the API.
