# Wallet Service

The Wallet Service is a Spring Boot application that provides RESTful API endpoints for managing wallets and transactions.

his service allows customers to deposit, withdraw, place bets, win bets, and receive bonuses.

First, we have a TransactionType enumeration which represents the different types of transactions: **DEPOSIT**, **WITHDRAWAL**, **BET_PLACED**, **BET_WON**, and **BONUS**, defined in `com.logispin.wallet.models.TransactionType`. Transactions processing strategies have been implemented base on these TransactionType(s). We have several implementations of the `com.logispin.wallet.services.transaction.TransactionStrategy` interface, like `com.logispin.wallet.services.transaction.BetPlacedStrategy`, `com.logispin.wallet.services.transaction.BetWonStrategy`, `com.logispin.wallet.services.transaction.BonusStrategy`, `com.logispin.wallet.services.transaction.DepositStrategy`, and `com.logispin.wallet.services.transaction.WithdrawalStrategy`. Each strategy has an execute method that processes the specific transaction type and a getSupportedTransactionType method that returns the corresponding TransactionType.

The `com.logispin.wallet.services.transaction.WalletStrategiesRegistry` class is responsible for mapping TransactionType values to their corresponding TransactionStrategy implementations. It has a `com.logispin.wallet.services.transaction.WalletStrategiesRegistry#resolve(final TransactionType type)` method that takes a TransactionType and returns the appropriate strategy. Resolved strategy is then executed in `com.logispin.wallet.services.DefaultWalletService#processTransaction(UUID walletId, TransactionType type, BigDecimal amount)`. In summary, this wallet service provides a flexible and extensible architecture for handling various transaction types, while ensuring proper encapsulation and separation of concerns.

Race condition caused by concurrent update of wallet balance has been addressed by including hibernate `@version` annotation, and retry on `ObjectOptimisticLockingFailureException`. It ensures that the version of the wallet is updated on every write operation, and if there's a concurrent update, an ObjectOptimisticLockingFailureException will be thrown. By using optimistic locking with a retry mechanism, you can minimize the chances of race conditions and handle them appropriately when they occur. See `com.logispin.wallet.services.WalletService`. The `@Retryable` annotation is applied to the processTransaction method, and it specifies that the method should be retried when an `ObjectOptimisticLockingFailureException` is thrown. The maximum number of attempts is set to 3, with a 50-millisecond delay between retries.

The `@Recover` annotation is applied to the `handleOptimisticLockingFailure` method, which is called when all retries have been exhausted. In this case, an `IllegalStateException` is thrown to indicate that updating the wallet balance was unsuccessful after several retries.

## Prerequisites

- JDK 17
- Maven

## Getting Started

1. Clone the repository:

```git clone git@github.com:OmexIT/wallet-service.git```

2. Change into the project directory:

```cd wallet-service```

3. Build the project:
   
```mvn clean install```

4. Run the application:

```mvn spring-boot:run```


The application will start and listen on port 8080.

## Testing the API

### Using Postman

- Download and install Postman from the official website: https://www.postman.com/downloads/

- Import collection 

- Send the requests and analyze the responses to ensure that the Wallet Service API behaves as expected.

#### Below is the API documentation for the wallet service.
 **1. Create Wallet**

Endpoint: POST `/wallets`

URL: `localhost:8080/wallets`

Auth: Basic Auth (User/Password: user/12345)

Request Body:
```json
{
  "customerId":"e923b649-3611-45be-8f62-c03dd5c0e992"
}
```

Response:
```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 0,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**2. Get Wallet by ID**

Endpoint: GET `/wallets/{walletId}`

URL: `localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c`

Auth: Basic Auth (User/Password: user/12345)

Response:

```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 0.00,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**3. Deposit**
Endpoint: PUT /wallets/{walletId}/transactions?type=DEPOSIT&amount={amount}

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions?type=DEPOSIT&amount=100

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
    "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
    "balance": 100.00,
    "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
    "_links": {
        "self": {
            "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
        },
        "transactions": {
            "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
        }
    }
}
```

**4. Withdraw**
Endpoint: PUT /wallets/{walletId}/transactions?type=WITHDRAWAL&amount={amount}

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions?type=WITHDRAWAL&amount=50

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 50.00,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**5. Place Bet**
Endpoint: PUT /wallets/{walletId}/transactions?type=BET_PLACED&amount={amount}

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions?type=BET_PLACED&amount=50

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 0.00,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**6. Place Bet**
Endpoint: PUT /wallets/{walletId}/transactions?type=BET_WON&amount={amount}

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions?type=BET_WON&amount=150

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 150.00,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**7. Bonus**
Endpoint: PUT /wallets/{walletId}/transactions?type=BONUS&amount={amount}

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions?type=BONUS&amount=150

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "id": "418c3c24-d967-4cac-80f7-53a8e2cf062c",
  "balance": 160.00,
  "customerId": "b9e42efd-81a0-4bfa-bb82-793a3804214d",
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c"
    },
    "transactions": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**8. Get wallet transactions**
Endpoint: GET /wallets/{walletId}/transactions

URL: localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "_embedded": {
    "transactionDtoList": [
      {
        "id": "f033bf10-edd5-42bd-bd7b-d7ba4c8f2861",
        "type": "DEPOSIT",
        "amount": 100.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/transactions/f033bf10-edd5-42bd-bd7b-d7ba4c8f2861"
          }
        }
      },
      {
        "id": "cbcc2503-446a-46b6-b16e-873e0c2b7e67",
        "type": "WITHDRAWAL",
        "amount": 50.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/transactions/cbcc2503-446a-46b6-b16e-873e0c2b7e67"
          }
        }
      },
      {
        "id": "37ae6ada-f15a-42ca-9856-613bddc3a8b4",
        "type": "BET_PLACED",
        "amount": 50.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/transactions/37ae6ada-f15a-42ca-9856-613bddc3a8b4"
          }
        }
      },
      {
        "id": "f2c02498-5172-49a0-924c-7b93c8c226fe",
        "type": "BET_WON",
        "amount": 150.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/transactions/f2c02498-5172-49a0-924c-7b93c8c226fe"
          }
        }
      },
      {
        "id": "d8c733ce-f983-4f75-90bb-bf6538e705fc",
        "type": "BONUS",
        "amount": 10.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/transactions/d8c733ce-f983-4f75-90bb-bf6538e705fc"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/wallets/418c3c24-d967-4cac-80f7-53a8e2cf062c/transactions"
    }
  }
}
```

**7. Get transaction by Id**
Endpoint: GET transactions/{{transactionId}}

URL: localhost:8080/transactions/418c3c24-d967-4cac-80f7-53a8e2cf062c

Auth: Basic Auth (User/Password: user/12345)

Response:
```json
{
  "id": "f033bf10-edd5-42bd-bd7b-d7ba4c8f2861",
  "type": "DEPOSIT",
  "amount": 100.00,
  "_links": {
    "self": {
      "href": "http://localhost:8080/transactions/f033bf10-edd5-42bd-bd7b-d7ba4c8f2861"
    }
  }
}
```

### Running Unit and Integration Tests

Run the tests using the following command in the terminal:

```mvn test```


Maven will execute the tests and display the results in the terminal.

## Test Coverage

To analyze the test coverage in the project, we use JaCoCo, a Java Code Coverage tool.

Run the tests with JaCoCo enabled to generate the coverage report:

```mvn clean test``` or ```mvn clean install```

After the tests have completed, the JaCoCo coverage report can be found in the `target/site/jacoco` directory. Open the index.html file in a web browser to view the report.

The test coverage report will provide insights into the lines, branches, and methods covered by the unit and integration tests, helping you identify areas where additional testing may be required.



