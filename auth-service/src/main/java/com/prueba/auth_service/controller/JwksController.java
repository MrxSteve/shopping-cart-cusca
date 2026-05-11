package com.prueba.auth_service.controller;

import com.prueba.auth_service.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/.well-known")
@RequiredArgsConstructor
public class JwksController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, Object>> jwks() {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) jwtTokenProvider.getPublicKey();

        Map<String, Object> jwk = Map.of(
            "kty", "RSA",
            "use", "sig",
            "alg", "RS256",
            "n",   encodeUnsignedBytes(rsaPublicKey.getModulus().toByteArray()),
            "e",   encodeUnsignedBytes(rsaPublicKey.getPublicExponent().toByteArray())
        );

        return ResponseEntity.ok(Map.of("keys", List.of(jwk)));
    }

    /**
     * BigInteger.toByteArray() may include a leading 0x00 sign byte for positive values.
     * JWK spec requires unsigned big-endian encoding without the sign byte.
     */
    private String encodeUnsignedBytes(byte[] bytes) {
        if (bytes.length > 1 && bytes[0] == 0x00) {
            byte[] stripped = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, stripped, 0, stripped.length);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(stripped);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
