```
sigil/
├── core/              # Fundamental utils and helpers
│   ├── Base64Url.java
│   ├── Hex.java
│   ├── Bytes.java
│   └── crypto/        # Low-level crypto utils (can be here or top-level)
│       ├── Digest.java
│       ├── Hmac.java
│       ├── Signature.java
│       ├── CipherUtil.java
│       └── KeyUtils.java
│
├── jwa/               # Algorithm registry
│   ├── Algorithm.java
│   ├── HmacAlgorithm.java
│   ├── RsaAlgorithm.java
│   └── EllipticCurveAlgorithm.java
│
├── jwt/               # JSON Web Token abstraction
│   ├── Jwt.java
│   ├── Claims.java
│   ├── JwtParser.java
│   └── JwtValidator.java
│
├── jws/               # Signature handling
│   ├── JwsHeader.java
│   ├── JwsSigner.java
│   └── JwsVerifier.java
│
├── jwe/               # Encryption handling
│   ├── JweHeader.java
│   ├── JweEncrypter.java
│   └── JweDecrypter.java
│
├── jwk/               # Key representations and loaders
│   ├── Jwk.java
│   ├── JwkSet.java
│   ├── JwkParser.java
│   └── KeyConverter.java
│
└── tests/
    └── ... unit tests and fixtures ...

```