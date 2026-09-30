# Authentication comparison

All rows describe Spring Security **7.0.2**, the version managed by this project's Spring Boot 4.0.1 parent.

| Mechanism | Spring configuration | Important filter | Important provider | State | Test | Relevant source |
|---|---|---|---|---|---|---|
| Form username/password | `formLogin` | `UsernamePasswordAuthenticationFilter` | `DaoAuthenticationProvider` | Stateful session | Browser `/login` | `SessionSecurityConfiguration` |
| Session cookie | `SessionCreationPolicy.IF_REQUIRED` | `SecurityContextHolderFilter` | Provider used at login | `SecurityContext` in `HttpSession`; `JSESSIONID` identifies it | Login, reuse cookie | `SessionSecurityConfiguration` |
| HTTP Basic | `httpBasic` | `BasicAuthenticationFilter` | `DaoAuthenticationProvider` | Stateless here | `curl -u user:password .../profile` | `BasicSecurityConfiguration` |
| Custom JWT | custom filter + provider | `JwtAuthenticationFilter` | custom `JwtAuthenticationProvider` | Stateless | POST login, use Bearer token | `authdemo/jwt/*`, `JwtSecurityConfiguration` |
| JWT resource server | `oauth2ResourceServer(jwt)` | `BearerTokenAuthenticationFilter` | Spring's `JwtAuthenticationProvider` | Stateless | Keycloak access token | `ResourceServerSecurityConfiguration` |
| Opaque bearer token | `oauth2ResourceServer(opaqueToken)` | `BearerTokenAuthenticationFilter` | `OpaqueTokenAuthenticationProvider` | Stateless locally; server introspection | IdP-issued opaque token | `OpaqueTokenSecurityConfiguration` |
| OAuth 2.0/OIDC login | `oauth2Login` | `OAuth2AuthorizationRequestRedirectFilter`, `OAuth2LoginAuthenticationFilter` | `OidcAuthorizationCodeAuthenticationProvider` | Stateful app session | Browser `/oauth2/authorization/keycloak` | `OidcSecurityConfiguration` |
| SAML 2.0 login | `saml2Login` | `Saml2WebSsoAuthenticationRequestFilter`, `Saml2WebSsoAuthenticationFilter` | `OpenSaml5AuthenticationProvider` | Stateful app session | Browser `/saml2/authenticate/test-idp` | `SamlSecurityConfiguration` |
| LDAP password | `LdapAuthenticationProvider` + form login | `UsernamePasswordAuthenticationFilter` | `LdapAuthenticationProvider` | Stateful app session; identities in LDAP | `ldapuser / password` | `LdapSecurityConfiguration` |
| X.509/mTLS | `x509` + TLS client auth | `X509AuthenticationFilter` | `PreAuthenticatedAuthenticationProvider` | Request certificate; no password | `curl --cert ... --key ... https://...` | `MtlsSecurityConfiguration` |
| Remember me | `rememberMe` | `RememberMeAuthenticationFilter` | `RememberMeAuthenticationProvider` | Long-lived signed cookie | Check remember-me on form login | `SessionSecurityConfiguration` |
| API key | custom header filter | `ApiKeyAuthenticationFilter` | custom `ApiKeyAuthenticationProvider` | Stateless | `X-API-KEY: demo-key` | `authdemo/apikey/*` |

## What each mechanism means

### Form login, session, and remember-me

The client sends an HTML form containing username/password. `DaoAuthenticationProvider` loads `UserDetails` through `UserDetailsService` and uses BCrypt through `PasswordEncoder`. On success, Spring saves the resulting `Authentication` in a `SecurityContext` backed by the HTTP session. Subsequent requests normally send only `JSESSIONID`. This is convenient for browser applications, logout, and server-side revocation; it costs server memory and needs CSRF protection. Remember-me uses a longer-lived signed cookie to recreate a limited authentication after the session expires. Treat it as a persistent credential: use HTTPS, rotate keys, bound its lifetime, and offer revocation.

### HTTP Basic

Every request sends `Authorization: Basic base64(username:password)`. The server validates through the same DAO provider. It is simple and widely supported, but Base64 is encoding—not encryption—so production use requires HTTPS. Re-sending the password and awkward browser logout make it a poor fit for rich browser apps.

### Custom JWT

The login endpoint authenticates a password through `AuthenticationManager` and `DaoAuthenticationProvider`, then signs a JWT containing issuer, subject, roles, issued-at, and expiration claims. The compact token has a Base64URL header, payload, and cryptographic signature. The custom filter extracts a Bearer token and submits an unauthenticated token object to the custom provider; the provider verifies signature, issuer, and expiration before creating an authenticated object. JWT payloads are readable, so never put secrets in claims. Common mistakes are long expirations, weak/shared keys, skipped issuer/audience checks, logging tokens, and assuming logout revokes an already-issued token.

### JWT resource server and opaque token

A resource server validates tokens but does not issue them. With JWT it verifies locally using the authorization server's advertised keys, which is fast but makes immediate revocation difficult. An opaque token has no client-readable claims; `OpaqueTokenAuthenticationProvider` calls the authorization server's introspection endpoint on requests, enabling centralized status/revocation at the cost of network latency and availability. Keycloak normally emits JWT access tokens; use an IdP/client configured for reference tokens to exercise `opaque` genuinely.

### OAuth 2.0, OIDC, and SSO

OAuth 2.0 is an authorization framework. OIDC adds identity/authentication using an ID token and user-info conventions. In Authorization Code flow the browser is redirected to Keycloak, the application receives a short-lived code at its redirect URI, and its back channel exchanges that code for tokens. The ID token describes the login; the access token authorizes API calls. SSO is the experience, not a protocol: after one IdP login, multiple OIDC or SAML applications can accept the IdP session without asking for credentials again.

### SAML 2.0

This app is a Service Provider (SP). It creates a SAML authentication request and redirects the browser to an Identity Provider (IdP). The IdP posts a signed SAML response containing an assertion to the Assertion Consumer Service (ACS), `/login/saml2/sso/test-idp`. Spring validates issuer, destination, time conditions, and signature before establishing a session. Entity IDs identify parties; metadata exchanges endpoints and signing keys. Never accept unsigned/unvalidated assertions. The profile needs genuine IdP metadata and intentionally has no pass-through mode.

### LDAP

LDAP is a directory protocol rather than an application user table. The demo binds as the presented user using a DN pattern, then searches group entries and maps their `cn` values to authorities. A normal `UserDetailsService` typically loads a password hash and attributes from an application database; LDAP commonly validates through directory bind and centrally managed groups.

### X.509/mTLS

TLS authenticates the server; mutual TLS additionally requires the client to present an X.509 certificate signed by a trusted CA. The servlet container verifies the certificate chain before Spring extracts `CN` and performs pre-authentication lookup. Protect private keys, keep trust stores narrow, and plan certificate issuance, expiry, and revocation. The normal application is not forced into mTLS; only the `mtls` profile enables it.

### API key/custom provider

The client sends `X-API-KEY`. The filter constructs an unauthenticated `ApiKeyAuthenticationToken`; `ProviderManager` selects `ApiKeyAuthenticationProvider`; the provider compares the key in constant time and only then returns an authenticated object. API keys identify software clients, not usually human users. Hash stored keys, scope/rotate/revoke them, require HTTPS, and never put them in URLs or logs.

## WebAuthn/passkeys and MFA/OTP

These are documented but not faked in this lab. A correct passkey implementation needs HTTPS (localhost is a browser exception), relying-party/origin configuration, challenge persistence, credential/public-key storage, signature counters, and browser JavaScript ceremonies. Spring Security 7 has WebAuthn support available through its dedicated module, but this application has no durable user/credential schema to store registrations safely.

MFA is a policy/flow: verify a first factor, place the login in a restricted pending state, verify a short-lived single-use OTP using a real TOTP or delivery provider, then elevate authentication. A hard-coded “OTP” would teach an insecure pass-through, so no fake endpoint is included. Production designs must cover enrollment, recovery codes, replay protection, rate limits, clock skew, and factor reset. Keycloak can enforce real OTP or WebAuthn in its realm authentication flow, letting the `oidc` profile demonstrate MFA without weakening this application.

## 401, 403, roles, CSRF, and CORS

- `401 Unauthorized`: the request lacks valid authentication (missing/invalid credentials).
- `403 Forbidden`: authentication succeeded but lacks the required authority.
- A role is an authority with the conventional `ROLE_` prefix. `hasRole("USER")` checks `ROLE_USER`; `hasAuthority` does no prefixing.
- CSRF exploits automatically attached browser credentials such as cookies. Session profiles keep CSRF enabled. Stateless demo APIs ignore it only where credentials come from explicit Authorization/custom headers.
- CORS controls which browser origins may read/call a server; it neither authenticates nor prevents non-browser clients. No permissive global policy is installed.
