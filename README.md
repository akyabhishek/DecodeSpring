# DecodeSpring authentication lab

This is the original `DecodeSpring` application extended in place with an isolated Spring Security learning lab. Existing `/`, property, external-API, Redis, service, and repository examples remain intact. Demo endpoints live under `/api/auth-demo/**`.

## Project baseline

- Spring Boot: **4.0.1**
- Spring Security: **7.0.2** (managed by Boot)
- Java: **17**
- Build: Maven
- Existing persistence: Redis dependency/configuration; no JPA entities or database-backed user table. The existing `UserRepository` is a teaching stub, so the lab uses an `InMemoryUserDetailsManager` rather than pretending it is a credential store.
- Existing profiles: `default`, `dev`, `test`

The local users are `user / password` (`ROLE_USER`) and `admin / admin123` (`ROLE_USER`, `ROLE_ADMIN`). These are teaching credentials only. Override all demo keys for any non-local use.

## Endpoints

| Endpoint | Rule |
|---|---|
| `GET /api/auth-demo/public` | permit all |
| `GET /api/auth-demo/profile` | authenticated |
| `GET /api/auth-demo/user` | `ROLE_USER` |
| `GET /api/auth-demo/admin` | `ROLE_ADMIN`, plus `@PreAuthorize` |
| `GET /api/auth-demo/debug` | authenticated; returns only safe `Authentication` metadata |
| `POST /api/auth-demo/login` | custom JWT profile only |

The debug response contains the authentication class, principal class, name, and authorities. It never serializes credentials, a raw JWT, secrets, refresh tokens, or private key material.

## Run profiles

Use exactly one authentication profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=session
./mvnw spring-boot:run -Dspring-boot.run.profiles=basic
./mvnw spring-boot:run -Dspring-boot.run.profiles=jwt
./mvnw spring-boot:run -Dspring-boot.run.profiles=api-key
./mvnw spring-boot:run -Dspring-boot.run.profiles=resource-server
./mvnw spring-boot:run -Dspring-boot.run.profiles=opaque
./mvnw spring-boot:run -Dspring-boot.run.profiles=oidc
./mvnw spring-boot:run -Dspring-boot.run.profiles=ldap
./mvnw spring-boot:run -Dspring-boot.run.profiles=saml
./mvnw spring-boot:run -Dspring-boot.run.profiles=mtls
```

`default` is equivalent to the stateful `session` configuration. The external profiles deliberately fail closed when their real IdP, introspection endpoint, LDAP directory, SAML metadata, or TLS material is unavailable.

Start local Keycloak and OpenLDAP with:

```bash
docker compose --profile auth-demo up keycloak openldap
```

See [authentication-comparison](docs/authentication-comparison.md), [request-flow diagrams](docs/authentication-flows.md), and [testing instructions](docs/testing.md).

## Security notes

Authentication answers “who are you?”; authorization answers “what may you do?”. `hasRole("ADMIN")` checks for the authority `ROLE_ADMIN`; `hasAuthority("ROLE_ADMIN")` checks that exact string.

Session/browser profiles retain CSRF protection. Stateless header/bearer profiles ignore CSRF only for the lab API because browsers do not automatically attach those credentials. CORS is not authentication and is not opened globally; add an explicit allowed-origin policy for a real separate frontend.

No real credentials should be committed. Redis and reqres keys that were literals are now read from `REDIS_PASSWORD` and `REQRES_API_KEY`. Rotate the previously committed values if they were live.
