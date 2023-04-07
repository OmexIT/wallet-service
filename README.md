# Wallet Service

The Wallet Service is a Spring Boot application that provides RESTful API endpoints for managing wallets and transactions.

his service allows customers to deposit, withdraw, place bets, win bets, and receive bonuses.

First, we have a TransactionType enumeration which represents the different types of transactions: **DEPOSIT**, **WITHDRAWAL**, **BET_PLACED**, **BET_WON**, and **BONUS**, defined in `com.logispin.wallet.models.TransactionType`. Transactions processing strategies have been implemented base on these TransactionType(s). We have several implementations of the `com.logispin.wallet.services.transaction.TransactionStrategy` interface, like `com.logispin.wallet.services.transaction.BetPlacedStrategy`, `com.logispin.wallet.services.transaction.BetWonStrategy`, `com.logispin.wallet.services.transaction.BonusStrategy`, `com.logispin.wallet.services.transaction.DepositStrategy`, and `com.logispin.wallet.services.transaction.WithdrawalStrategy`. Each strategy has an execute method that processes the specific transaction type and a getSupportedTransactionType method that returns the corresponding TransactionType.

Finally, the `com.logispin.wallet.services.transaction.WalletStrategiesRegistry` class is responsible for mapping TransactionType values to their corresponding TransactionStrategy implementations. It has a `com.logispin.wallet.services.transaction.WalletStrategiesRegistry#resolve(final TransactionType type)` method that takes a TransactionType and returns the appropriate strategy. Resolved strategy is then executed in `com.logispin.wallet.services.DefaultWalletService#processTransaction(UUID walletId, TransactionType type, BigDecimal amount)`

In summary, this wallet service provides a flexible and extensible architecture for handling various transaction types, while ensuring proper encapsulation and separation of concerns.

## Prerequisites

- JDK 17
- Maven

## Getting Started

1. Clone the repository:

```git clone <REPOSITORY_URL>```

2. Change into the project directory:

```cd wallet-service```

3. Build the project:
   
```mvn clean install```

4. Run the application:

```mvn spring-boot:run```


The application will start and listen on port 8080.

## Testing the API

### Using Postman

1. Download and install Postman from the official website: https://www.postman.com/downloads/

2. Open Postman and create a new collection to organize your API requests.

3. Create a new request for each API endpoint provided by the `TransactionController` and `WalletController`. Configure the request method, URL, headers, and body as needed.

4. Send the requests and analyze the responses to ensure that the Wallet Service API behaves as expected.

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



