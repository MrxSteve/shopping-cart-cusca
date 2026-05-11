package com.prueba.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${jwt.secret:your-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${jwt.public-key-path:config/public.key}")
    private String publicKeyPath;

    @Value("${jwt.private-key-path:config/private.key}")
    private String privateKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtTokenProvider() {
    }

    @PostConstruct
    public void init() {
        try {
            loadOrGenerateKeys();
        } catch (Exception e) {
            log.error("Error initializing JWT token provider", e);
            throw new RuntimeException("Failed to initialize JWT token provider", e);
        }
    }

    private void loadOrGenerateKeys() throws Exception {
        Path privKeyPath = Paths.get(privateKeyPath);
        Path pubKeyPath = Paths.get(publicKeyPath);

        if (Files.exists(privKeyPath) && Files.exists(pubKeyPath)) {
            log.info("Loading existing RSA keys from files");
            loadKeysFromFiles(privKeyPath, pubKeyPath);
        } else {
            log.info("Generating new RSA key pair");
            generateAndSaveKeys();
        }
    }

    private void loadKeysFromFiles(Path privKeyPath, Path pubKeyPath) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] privKeyBytes = Files.readAllBytes(privKeyPath);
        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));

        byte[] pubKeyBytes = Files.readAllBytes(pubKeyPath);
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyBytes));

        log.info("RSA keys loaded successfully from files");
    }

    private void generateAndSaveKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Save keys to files
        Path privKeyPath = Paths.get(privateKeyPath);
        Path pubKeyPath = Paths.get(publicKeyPath);

        Files.createDirectories(privKeyPath.getParent());
        Files.write(privKeyPath, privateKey.getEncoded());
        Files.write(pubKeyPath, publicKey.getEncoded());

        log.info("RSA key pair generated and saved successfully");
    }

    public String generateToken(UserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername(), userDetails.getAuthorities());
    }

    public String generateTokenFromUsername(String username, java.util.Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
