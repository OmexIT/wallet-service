package com.logispin.wallet.api;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.services.TransactionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping("/{transactionId}")
  public ResponseEntity<EntityModel<TransactionDto>> getTransactionById(
      @PathVariable UUID transactionId) {
    TransactionDto transaction = transactionService.getTransactionById(transactionId);

    if (transaction == null) {
      return ResponseEntity.notFound().build();
    }

    Link selfLink =
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TransactionController.class)
                    .getTransactionById(transaction.id()))
            .withSelfRel();
    EntityModel<TransactionDto> transactionResource = EntityModel.of(transaction, selfLink);

    return ResponseEntity.ok(transactionResource);
  }
}
