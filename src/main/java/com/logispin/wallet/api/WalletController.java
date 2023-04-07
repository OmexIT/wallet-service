package com.logispin.wallet.api;

import com.logispin.wallet.dto.CreateWalletDto;
import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.services.TransactionService;
import com.logispin.wallet.services.WalletService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallets")
public class WalletController {

  private final WalletService walletService;
  private final TransactionService transactionService;

  @PostMapping
  public ResponseEntity<EntityModel<WalletDto>> createWallet(@RequestBody CreateWalletDto request) {
    WalletDto createdWallet = walletService.createWallet(request);

    Link selfLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getWalletById(createdWallet.id()))
            .withSelfRel();

    Link transactionsLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getTransactionsByWalletId(createdWallet.id()))
            .withRel("transactions");

    EntityModel<WalletDto> walletResource =
        EntityModel.of(createdWallet, selfLink, transactionsLink);

    return ResponseEntity.created(selfLink.toUri()).body(walletResource);
  }

  @GetMapping("/{walletId}")
  public ResponseEntity<EntityModel<WalletDto>> getWalletById(@PathVariable UUID walletId) {
    WalletDto wallet = walletService.getWalletById(walletId);
    Link selfLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class).getWalletById(walletId))
            .withSelfRel();
    Link transactionsLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getTransactionsByWalletId(walletId))
            .withRel("transactions");

    EntityModel<WalletDto> walletResource = EntityModel.of(wallet, selfLink, transactionsLink);
    return ResponseEntity.ok(walletResource);
  }

  @PutMapping("/{walletId}/transactions")
  public ResponseEntity<EntityModel<WalletDto>> processTransaction(
      @PathVariable UUID walletId,
      @RequestParam TransactionType type,
      @RequestParam BigDecimal amount) {

    WalletDto updatedWallet = walletService.processTransaction(walletId, type, amount);

    Link selfLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getWalletById(updatedWallet.id()))
            .withSelfRel();

    Link transactionsLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getTransactionsByWalletId(updatedWallet.id()))
            .withRel("transactions");

    EntityModel<WalletDto> walletResource =
        EntityModel.of(updatedWallet, selfLink, transactionsLink);

    return ResponseEntity.ok(walletResource);
  }

  @GetMapping("/{walletId}/transactions")
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getTransactionsByWalletId(
      @PathVariable UUID walletId) {
    List<TransactionDto> transactions = transactionService.getTransactionsByWalletId(walletId);

    List<EntityModel<TransactionDto>> transactionResources =
        transactions.stream()
            .map(
                transaction -> {
                  Link selfLink =
                      WebMvcLinkBuilder.linkTo(
                              WebMvcLinkBuilder.methodOn(TransactionController.class)
                                  .getTransactionById(transaction.id()))
                          .withSelfRel();

                  return EntityModel.of(transaction, selfLink);
                })
            .collect(Collectors.toList());

    Link selfLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(WalletController.class)
                    .getTransactionsByWalletId(walletId))
            .withSelfRel();

    CollectionModel<EntityModel<TransactionDto>> transactionCollection =
        CollectionModel.of(transactionResources, selfLink);
    return ResponseEntity.ok(transactionCollection);
  }
}
