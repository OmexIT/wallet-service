package com.logispin.wallet.api;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.logispin.wallet.config.SecurityConfig;
import com.logispin.wallet.dto.CreateWalletDto;
import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.services.TransactionService;
import com.logispin.wallet.services.WalletService;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import com.logispin.wallet.wrappers.WalletTransactionTestWrapper;
import java.math.BigDecimal;
import java.util.Arrays;
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
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = WalletController.class)
@Import(SecurityConfig.class)
class WalletControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private WalletService walletService;
  @MockBean private TransactionService transactionService;
  private static final UUID WALLET_ID = UUID.randomUUID();
  private static final BigDecimal AMOUNT = new BigDecimal("1000.00");
  private static final TransactionType TRANSACTION_TYPE = TransactionType.DEPOSIT;

  @Test
  @WithMockUser(username = "testuser", authorities = "write")
  void createWallet_shouldReturnCreated() throws Exception {
    UUID customerId = UUID.randomUUID();
    WalletTestWrapper walletTestWrapper =
        WalletTestWrapper.buildValid().id(WALLET_ID).customerId(customerId).build();
    CreateWalletDto createWalletDto = new CreateWalletDto(customerId);
    WalletDto createdWalletDto = walletTestWrapper.unwrapWalletDto();
    given(walletService.createWallet(createWalletDto)).willReturn(createdWalletDto);

    ResultActions resultActions =
        mockMvc.perform(
            post("/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createWalletDto)));

    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(WALLET_ID.toString())))
        .andExpect(jsonPath("$.customerId", is(createWalletDto.customerId().toString())));

    verify(walletService).createWallet(createWalletDto);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = "read")
  void getWalletById_shouldReturnOk() throws Exception {
    UUID customerId = UUID.randomUUID();
    WalletTestWrapper walletTestWrapper =
        WalletTestWrapper.buildValid().id(WALLET_ID).customerId(customerId).build();
    WalletDto walletDto = walletTestWrapper.unwrapWalletDto();
    given(walletService.getWalletById(WALLET_ID)).willReturn(walletDto);

    // when
    ResultActions resultActions = mockMvc.perform(get("/wallets/{walletId}", WALLET_ID));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(WALLET_ID.toString())))
        .andExpect(jsonPath("$.customerId", is(walletDto.customerId().toString())));

    verify(walletService).getWalletById(WALLET_ID);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = "read")
  void getWalletById_shouldReturnNotFound_whenWalletNotFound() throws Exception {
    given(walletService.getWalletById(WALLET_ID)).willThrow(new ResourceNotFoundException(""));

    ResultActions resultActions = mockMvc.perform(get("/wallets/{walletId}", WALLET_ID));

    resultActions.andExpect(status().isNotFound());

    verify(walletService).getWalletById(WALLET_ID);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = "write")
  void processTransaction_shouldReturnOk() throws Exception {
    UUID customerId = UUID.randomUUID();
    WalletTestWrapper walletTestWrapper =
        WalletTestWrapper.buildValid().id(WALLET_ID).customerId(customerId).build();
    WalletDto walletDto = walletTestWrapper.unwrapWalletDto();
    given(walletService.processTransaction(WALLET_ID, TRANSACTION_TYPE, AMOUNT))
        .willReturn(walletDto);

    ResultActions resultActions =
        mockMvc.perform(
            put("/wallets/{walletId}/transactions", WALLET_ID)
                .param("type", TRANSACTION_TYPE.toString())
                .param("amount", AMOUNT.toString()));

    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(WALLET_ID.toString())))
        .andExpect(jsonPath("$.customerId", is(walletDto.customerId().toString())));

    verify(walletService).processTransaction(WALLET_ID, TRANSACTION_TYPE, AMOUNT);
  }

  private static String asJsonString(Object obj) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper.writeValueAsString(obj);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = "read")
  void testGetTransactionsByWalletId() throws Exception {
    UUID walletId = UUID.randomUUID();

    TransactionDto transaction1 =
        WalletTransactionTestWrapper.buildValid()
            .transactionType(TransactionType.DEPOSIT)
            .amount(BigDecimal.valueOf(100))
            .build()
            .unwrapTransactionDto();
    TransactionDto transaction2 =
        WalletTransactionTestWrapper.buildValid()
            .transactionType(TransactionType.WITHDRAWAL)
            .amount(BigDecimal.valueOf(50))
            .build()
            .unwrapTransactionDto();
    ;

    when(transactionService.getTransactionsByWalletId(walletId))
        .thenReturn(Arrays.asList(transaction1, transaction2));

    mockMvc
        .perform(get("/wallets/{walletId}/transactions", walletId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[0].id").value(transaction1.id().toString()))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[0].type")
                .value(transaction1.type().toString()))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[0].amount")
                .value(transaction1.amount().doubleValue()))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[1].id").value(transaction2.id().toString()))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[1].type")
                .value(transaction2.type().toString()))
        .andExpect(
            jsonPath("$._embedded.transactionDtoList[1].amount")
                .value(transaction2.amount().doubleValue()));

    verify(transactionService, times(1)).getTransactionsByWalletId(walletId);
    verifyNoMoreInteractions(transactionService);
  }
}
