package com.logispin.wallet.api;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.logispin.wallet.config.SecurityConfig;
import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.services.TransactionService;
import com.logispin.wallet.wrappers.WalletTransactionTestWrapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TransactionService transactionService;

  @Test
  @WithMockUser(username = "testuser", authorities = "read")
  void testGetTransactionById() throws Exception {
    UUID transactionId = UUID.randomUUID();

    TransactionDto transactionDto =
        WalletTransactionTestWrapper.buildValid()
            .id(transactionId)
            .transactionType(TransactionType.DEPOSIT)
            .amount(BigDecimal.valueOf(100))
            .build()
            .unwrapTransactionDto();

    when(transactionService.getTransactionById(transactionId)).thenReturn(transactionDto);

    mockMvc
        .perform(get("/transactions/{transactionId}", transactionId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
        .andExpect(jsonPath("$.id").value(transactionId.toString()))
        .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.toString()))
        .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(100).doubleValue()));

    verify(transactionService, times(1)).getTransactionById(transactionId);
    verifyNoMoreInteractions(transactionService);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = "read")
  void testGetTransactionById_notFound() throws Exception {
    UUID transactionId = UUID.randomUUID();

    when(transactionService.getTransactionById(transactionId)).thenReturn(null);

    mockMvc
        .perform(get("/transactions/{transactionId}", transactionId))
        .andExpect(status().isNotFound());

    verify(transactionService, times(1)).getTransactionById(transactionId);
    verifyNoMoreInteractions(transactionService);
  }
}
