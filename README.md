# TOTP Encrypt/Decrypt Demo

Spring Boot example that exposes two APIs to encrypt and decrypt payloads with `MkTotpUtil`.

## What this project provides

- `POST /totp/encode`: encrypts plaintext and returns Base64 ciphertext.
- `POST /totp/decode`: decodes Base64 ciphertext and returns plaintext.

## Requirements

- Java 17+
- Maven (or Maven Wrapper)
- Native and Java libs in `libs/`:
  - `libMKtotp.so`
  - `mkfr_ezway_v1_6_1.jar`

## Run locally

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

Default local URL:

- `http://localhost:8010`

## API usage example

### 1) Encrypt

```bash
curl -X POST "http://localhost:8010/totp/encode" \
  -H "Content-Type: application/json" \
  -d '{
    "uuid":"demo-uuid-001",
    "random":"123456",
    "data":"hello-totp",
    "length":10
  }'
```

Example response:

```json
{
  "code": "OK",
  "message": "Encoded Data",
  "data": "<base64-ciphertext>"
}
```

### 2) Decrypt

Use the `data` value returned by `/totp/encode`.

```bash
curl -X POST "http://localhost:8010/totp/decode" \
  -H "Content-Type: application/json" \
  -d '{
    "data":"<base64-ciphertext>",
    "length":44
  }'
```

Example response:

```json
{
  "code": "OK",
  "message": "Decrypted",
  "data": "hello-totp"
}
```

## Java example code

### Encrypt example

```java
import com.metsakuur.uface.totp.MkTotpUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TotpEncryptExample {
    public static void main(String[] args) {
        String uuid = "demo-uuid-001";
        String random = "123456";
        String plainText = "hello-totp";

        MkTotpUtil totp = new MkTotpUtil();
        byte[] encrypted = totp.enc(uuid, random, plainText.getBytes(StandardCharsets.UTF_8));
        String cipherBase64 = Base64.getEncoder().encodeToString(encrypted);

        System.out.println("Encrypted(Base64): " + cipherBase64);
    }
}
```

### Decrypt example

```java
import com.metsakuur.uface.totp.MkTotpUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TotpDecryptExample {
    public static void main(String[] args) {
        String cipherBase64 = "<base64-ciphertext>";

        MkTotpUtil totp = new MkTotpUtil();
        byte[] encrypted = Base64.getDecoder().decode(cipherBase64);
        byte[] decrypted = totp.dec(encrypted);
        String plainText = new String(decrypted, StandardCharsets.UTF_8);

        System.out.println("Decrypted: " + plainText);
    }
}
```

## Implementation outline

1. Convert input plaintext to UTF-8 bytes.
2. Encrypt using `MkTotpUtil.enc(uuid, random, source)`.
3. Return ciphertext as Base64 string.
4. For decrypt flow, Base64-decode request data.
5. Decrypt using `MkTotpUtil.dec(source)` and return UTF-8 plaintext.

## Docker

```bash
docker compose up -d --build
docker compose ps
```

Containerized app URL:

- `http://localhost:8011`
