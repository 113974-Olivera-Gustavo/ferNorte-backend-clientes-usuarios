package com.example.micro_b.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        when(userDetails.getUsername()).thenReturn("testUser");
    }
    
    @Test
    public void testGetClaim() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("key1", "value1");
        extraClaims.put("key2", "value2");

        String token = jwtService.getToken(extraClaims, userDetails);

        String subjectClaim = jwtService.getClaim(token, Claims::getSubject);
        String key1Claim = jwtService.getClaim(token, claims -> claims.get("key1", String.class));
        String key2Claim = jwtService.getClaim(token, claims -> claims.get("key2", String.class));

        assertEquals("testUser", subjectClaim);
        assertEquals("value1", key1Claim);
        assertEquals("value2", key2Claim);
    }

    @Test
    public void testGetTokenWhenGivenUserDetailsAndMapThenReturnToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(Claims.AUDIENCE, "testAudience");

        String token = jwtService.getToken(extraClaims, userDetails);

        assertNotNull(token);
    }

    @Test
    public void testGetTokenWhenGivenUserDetailsAndEmptyMapThenReturnToken() {
        Map<String, Object> extraClaims = new HashMap<>();

        String token = jwtService.getToken(extraClaims, userDetails);

        assertNotNull(token);
    }

    @Test
    public void testIsTokenValidWhenGivenValidTokenAndUserDetailsThenReturnTrue() {
        String token = jwtService.getToken(new HashMap<>(), userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void testGetUsernameFromTokenWhenGivenValidTokenThenReturnUsername() {
        String token = jwtService.getToken(new HashMap<>(), userDetails);

        String username = jwtService.getUsernameFromToken(token);

        assertEquals("testUser", username);
    }
}