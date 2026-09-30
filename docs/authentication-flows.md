# Authentication request flows

The invariant is: request enters a `SecurityFilterChain`; an authentication filter creates an unauthenticated token; `AuthenticationManager` selects an `AuthenticationProvider`; successful validation returns authenticated `Authentication`; Spring places it in `SecurityContext`; authorization runs before the controller.

## Username/password

```mermaid
sequenceDiagram
    participant B as Browser
    participant F as UsernamePasswordAuthenticationFilter
    participant M as AuthenticationManager
    participant P as DaoAuthenticationProvider
    participant U as UserDetailsService + BCrypt
    B->>F: POST /login username + password + CSRF
    F->>M: UsernamePasswordAuthenticationToken (unauthenticated)
    M->>P: authenticate
    P->>U: load user and compare password hash
    U-->>P: UserDetails
    P-->>F: authenticated token
    F->>F: save SecurityContext
    F-->>B: session cookie / redirect
```

## Session

```mermaid
sequenceDiagram
    participant B as Browser
    participant C as JSESSIONID cookie
    participant H as SecurityContextHolderFilter
    participant R as HttpSessionSecurityContextRepository
    participant A as AuthorizationFilter
    participant X as Controller
    B->>C: subsequent request
    C->>H: JSESSIONID
    H->>R: load SecurityContext from HttpSession
    R-->>H: Authentication
    H->>A: continue chain
    A->>X: authority accepted
    X-->>B: response
```

## HTTP Basic

```mermaid
sequenceDiagram
    participant C as Client
    participant F as BasicAuthenticationFilter
    participant M as AuthenticationManager
    participant P as DaoAuthenticationProvider
    participant X as Controller
    C->>F: Authorization: Basic base64(user:password)
    F->>M: username/password token
    M->>P: authenticate
    P-->>F: authenticated token
    F->>F: populate SecurityContext for request
    F->>X: authorized request
```

## Custom JWT

```mermaid
sequenceDiagram
    participant C as Client
    participant L as JwtLoginController
    participant D as DaoAuthenticationProvider
    participant J as JwtService
    participant F as JwtAuthenticationFilter
    participant P as Custom JwtAuthenticationProvider
    C->>L: POST /login credentials
    L->>D: authenticate through AuthenticationManager
    D-->>L: Authentication
    L->>J: sign issuer/subject/roles/expiry
    J-->>C: compact JWT
    C->>F: Authorization: Bearer JWT
    F->>P: unauthenticated JwtAuthenticationToken
    P->>J: verify signature, issuer, expiration
    P-->>F: authenticated token
    F->>F: populate SecurityContext
```

## OAuth 2.0 / OIDC login and SSO

```mermaid
sequenceDiagram
    participant B as Browser
    participant A as Spring Boot client
    participant I as Keycloak IdP
    B->>A: GET protected resource
    A-->>B: redirect to authorization endpoint
    B->>I: authorization request
    I->>B: authenticate/consent (or reuse SSO session)
    I-->>B: redirect with authorization code
    B->>A: callback code
    A->>I: back-channel code exchange
    I-->>A: ID token + access token
    A->>A: OidcAuthorizationCodeAuthenticationProvider validates login
    A-->>B: application session
```

## OAuth 2.0 JWT resource server

```mermaid
sequenceDiagram
    participant C as Client
    participant I as Authorization Server
    participant F as BearerTokenAuthenticationFilter
    participant P as JwtAuthenticationProvider
    participant K as Issuer JWK Set
    C->>I: obtain access token
    I-->>C: signed JWT
    C->>F: Authorization: Bearer JWT
    F->>P: bearer authentication token
    P->>K: obtain/cache public signing keys
    P-->>F: JwtAuthenticationToken
    F->>F: populate SecurityContext
```

## SAML 2.0

```mermaid
sequenceDiagram
    participant B as Browser
    participant S as Spring Boot SP
    participant I as SAML IdP
    B->>S: protected resource
    S-->>B: redirect with SAML AuthnRequest
    B->>I: AuthnRequest
    I->>B: authenticate
    I-->>B: signed SAML Response
    B->>S: POST response to ACS
    S->>S: OpenSaml5AuthenticationProvider validates assertion
    S-->>B: session + protected resource
```

## LDAP

```mermaid
sequenceDiagram
    participant B as Browser
    participant F as UsernamePasswordAuthenticationFilter
    participant P as LdapAuthenticationProvider
    participant L as OpenLDAP
    B->>F: username/password form
    F->>P: authenticate
    P->>L: bind as user DN
    L-->>P: bind accepted
    P->>L: search groups
    L-->>P: USER / ADMIN groups
    P-->>F: Authentication with authorities
    F->>F: save SecurityContext in session
```

## mTLS / X.509

```mermaid
sequenceDiagram
    participant C as Client
    participant T as TLS container
    participant F as X509AuthenticationFilter
    participant P as PreAuthenticatedAuthenticationProvider
    participant U as UserDetailsService
    C->>T: TLS handshake + client certificate
    T->>T: validate certificate chain against trust store
    T->>F: request with verified X.509 certificate
    F->>P: certificate principal (CN)
    P->>U: load mapped user
    U-->>P: authorities
    P-->>F: authenticated pre-authentication token
```

## Custom API key

```mermaid
sequenceDiagram
    participant C as Client
    participant F as ApiKeyAuthenticationFilter
    participant M as ProviderManager
    participant P as ApiKeyAuthenticationProvider
    participant X as Controller
    C->>F: X-API-KEY header
    F->>M: unauthenticated custom token
    M->>P: authenticate
    P->>P: constant-time configured-key comparison
    P-->>F: authenticated custom token
    F->>F: populate SecurityContext
    F->>X: authorized request
```
