package com.logispin.wallet.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuditorAwareImplTest {

  @Mock private Authentication authentication;

  private AuditorAwareImpl auditorAwareImpl;

  @BeforeEach
  void setUp() {
    auditorAwareImpl = new AuditorAwareImpl();
  }

  @Test
  void getCurrentAuditor_whenAuthenticated_returnsUsername() {
    var expectedUsername = "testUser";
    when(authentication.getName()).thenReturn(expectedUsername);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Optional<String> result = auditorAwareImpl.getCurrentAuditor();
    assertEquals(Optional.of(expectedUsername), result);
  }

  @Test
  void getCurrentAuditor_whenNotAuthenticated_throwsIllegalStateException() {
    SecurityContextHolder.clearContext();

    assertThrows(IllegalStateException.class, () -> auditorAwareImpl.getCurrentAuditor());
  }
}
