# Running and testing the lab

## Build

```bash
./mvnw clean test
```

The security tests load real `SecurityFilterChain` beans and use MockMvc through the filters. They cover public/protected resources, valid and invalid credentials, USER and ADMIN access, 401 vs 403, and missing/invalid/expired/valid custom JWTs.

## Session and form login

Run `session`, open `http://localhost:8080/login`, and sign in as `user / password` or `admin / admin123`. Spring generates the login page. Browser developer tools show `POST /login`, the `JSESSIONID` response cookie, that cookie on later requests, and `POST /logout`. The login and logout forms require a CSRF token. Check “Remember me” to exercise the longer-lived cookie.

For curl, first GET the login page and cookie jar, extract the `_csrf` form value, then POST `username`, `password`, and `_csrf`; keep using `-b cookies.txt -c cookies.txt`. Browser testing is simpler for this intentionally CSRF-protected profile.

## HTTP Basic

```bash
curl -i http://localhost:8080/api/auth-demo/public
curl -i http://localhost:8080/api/auth-demo/profile
curl -i -u user:wrong http://localhost:8080/api/auth-demo/profile
curl -i -u user:password http://localhost:8080/api/auth-demo/profile
curl -i -u user:password http://localhost:8080/api/auth-demo/admin
curl -i -u admin:admin123 http://localhost:8080/api/auth-demo/admin
```

Expected: no credentials gives 401, a valid USER on `/admin` gives 403, and ADMIN gives 200. Basic credentials are merely Base64 encoded; use HTTPS outside localhost.

## Custom JWT

```bash
curl -s -X POST http://localhost:8080/api/auth-demo/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

curl -i http://localhost:8080/api/auth-demo/debug \
  -H "Authorization: Bearer REPLACE_WITH_ACCESS_TOKEN"
```

Try a changed character, missing token, or wait past `auth-demo.jwt.ttl`. The response is 401. Set `AUTH_DEMO_JWT_SECRET` to at least 32 random bytes for anything beyond local learning.

## API key/custom header

```bash
curl -i http://localhost:8080/api/auth-demo/profile -H "X-API-KEY: demo-key"
curl -i http://localhost:8080/api/auth-demo/admin -H "X-API-KEY: demo-key"
```

The demo key has only `ROLE_USER`, so the second request is 403. Override it with `AUTH_DEMO_API_KEY`.

## Keycloak: OIDC, SSO, and resource server

```bash
docker compose --profile auth-demo up keycloak
```

Keycloak runs at `http://localhost:8180` (`admin / admin` for local administration). The imported realm is `auth-demo`; browser users are `oidcuser / password` and `oidcadmin / admin123`.

Run the `oidc` profile and browse to `http://localhost:8080/oauth2/authorization/keycloak`. The registered redirect URI is `http://localhost:8080/login/oauth2/code/keycloak`. Sign into another client registered in the same realm to observe SSO: the IdP session can remove the second credential prompt; the applications still create their own sessions.

For resource-server testing, obtain an access token using an appropriate Keycloak test client/flow, then send it as `Authorization: Bearer ...`. This profile validates issuer/signature/claims; it does not issue tokens. Keycloak realm roles are not automatically `ROLE_*` authorities in this minimal configuration; add a `JwtAuthenticationConverter` when role-protected endpoint mapping is the lesson being tested.

The `opaque` profile requires an authorization server that issues reference/opaque access tokens and supports RFC 7662 introspection. Configure `OAUTH_INTROSPECTION_URI`, `OAUTH_INTROSPECTION_CLIENT_ID`, and `OAUTH_INTROSPECTION_CLIENT_SECRET`. Do not call a token “opaque” merely because the client chooses not to decode a JWT.

## LDAP

```bash
docker compose --profile auth-demo up openldap
./mvnw spring-boot:run -Dspring-boot.run.profiles=ldap
```

Use `ldapuser / password` (`ROLE_USER`) or `ldapadmin / admin123` (`ROLE_USER`, `ROLE_ADMIN`) at `/login`. The app binds to `ldap://localhost:1389`, then searches `ou=groups`. Environment variables can override URL, base DN, manager DN, and manager password.

## SAML

Run a real test IdP (Keycloak SAML client, SimpleSAMLphp, Okta developer tenant, or another standards-compliant IdP), set `SAML_IDP_METADATA_URI`, and start `saml`. Import SP metadata from:

```text
http://localhost:8080/saml2/service-provider-metadata/test-idp
```

Set the IdP ACS URL to `http://localhost:8080/login/saml2/sso/test-idp` and SP entity ID to `{baseUrl}/saml2/service-provider-metadata/test-idp`. Start login at `/saml2/authenticate/test-idp`. Exact IdP screens differ, so the project does not ship a fake assertion issuer.

## mTLS

Use a development CA to create a server certificate (`localhost`) and client certificates whose CN is `user` or `admin`. Package the server key/certificate as PKCS#12 and the CA certificate as a PKCS#12 trust store, then set the `MTLS_*` variables referenced in `application-mtls.properties`.

```bash
curl --cacert ca.crt --cert user.crt --key user.key \
  https://localhost:8080/api/auth-demo/debug
```

The TLS layer rejects missing/untrusted client certificates before Spring Security. Never commit generated private keys or keystore passwords.

## Passkeys and MFA

For genuine MFA locally, configure Keycloak realm Authentication with “Browser - Conditional OTP” or WebAuthn/Passwordless and use the `oidc` profile. WebAuthn needs a browser secure context, exact relying-party ID/origin, and credential storage. See `authentication-comparison.md` for why the application intentionally avoids fake OTP/passkey endpoints.
