# Design Document 


Authors: Angela D'Antonio, Gabriele Inzerillo, Ruggero Nocera, Marzio Vallero

Date: 06/06/2021

Version: 1.3


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

Layered architeture whit MVC pattern.

```plantuml
@startuml
package it.polito.ezshop as EzShop
package it.polito.ezshop.data as EZShopData
package it.polito.ezshop.exceptions as EZShopExceptions
package it.polito.ezshop.model as EZShopModel
package it.polito.ezshop.acceptanceTests as AcceptanceTests
package it.polito.ezshop.test as Test
package "ezshop-gui.jar" as GUI
database ezshop.bin as DB

EzShop <-- GUI
EzShop <-- EZShopModel
EZShopData <-- EZShopModel
EZShopExceptions <-- EZShopModel
EZShopData <-- GUI
EZShopData <-- AcceptanceTests
EZShopData <-- Test
EZShopData <--> DB
@enduml
```

# Low level design

### it.polito.ezshop.data
```plantuml
@startuml
package it.polito.ezshop.data {
top to bottom direction

    interface "EZShopInterface" as API{
    void reset()
    -- User Management --
    Integer createUser(String username, String password, String role) 
    boolean deleteUser(Integer id) 
    List<User> getAllUsers() 
    User getUser(Integer id) 
    boolean updateUserRights(Integer id, String role)
    User login(String username, String password) 
    boolean logout()
    -- Product Management --
    Integer createProductType(String description, String productCode, double pricePerUnit, String note)
    boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
    boolean deleteProductType(Integer id) 
    List<ProductType> getAllProductTypes() 
    ProductType getProductTypeByBarCode(String barCode) 
    List<ProductType> getProductTypesByDescription(String description) 
    boolean updateQuantity(Integer productId, int toBeAdded) 
    boolean updatePosition(Integer productId, String newPos) 
    -- Order Management --
    Integer issueOrder(String productCode, int quantity, double pricePerUnit)
    Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
    boolean payOrder(Integer orderId) 
    boolean recordOrderArrival(Integer orderId) 
    boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom)
    List<Order> getAllOrders() 
    -- Customer Management --
    Integer defineCustomer(String customerName) 
    boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) 
    boolean deleteCustomer(Integer id) 
    Customer getCustomer(Integer id) 
    List<Customer> getAllCustomers() 
    String createCard() 
    boolean attachCardToCustomer(String customerCard, Integer customerId) 
    boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) 
    -- Payment Management --
    Integer startSaleTransaction() 
    boolean addProductToSale(Integer transactionId, String productCode, int amount)
    boolean addProductToSaleRFID(Integer transactionId, String RFID)
    boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
    boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) 
    boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) 
    boolean applyDiscountRateToSale(Integer transactionId, double discountRate) 
    int computePointsForSale(Integer transactionId) 
    boolean endSaleTransaction(Integer transactionId) 
    boolean deleteSaleTransaction(Integer transactionId) 
    SaleTransaction getSaleTransaction(Integer transactionId) 
    Integer startReturnTransaction(Integer transactionId) 
    boolean returnProduct(Integer returnId, String productCode, int amount) 
    boolean returnProductRFID(Integer returnId, String RFID)
    boolean endReturnTransaction(Integer returnId, boolean commit) 
    boolean deleteReturnTransaction(Integer returnId) 
    double receiveCashPayment(Integer transactionId, double cash) 
    boolean receiveCreditCardPayment(Integer transactionId, String creditCard) 
    double returnCashPayment(Integer returnId) 
    double returnCreditCardPayment(Integer returnId, String creditCard) 
    boolean recordBalanceUpdate(double toBeAdded) 
    List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) 
    double computeBalance()
    }

    class EZShop {
        double balance
        User loggedUser
        HashMap<Integer, SaleTransactionImpl> openedSaleTransactions
        HashMap<Integer, SaleTransactionImpl> closedSaleTransactions
        HashMap<Integer, SaleTransactionImpl> paidSaleTransactions
        HashMap<Integer, ReturnTransactionImpl> openedReturnTransactions
        HashMap<Integer, ReturnTransactionImpl> closedReturnTransactions
        HashMap<Integer, ReturnTransactionImpl> paidReturnTransactions
        HashMap<Integer, OrderImpl> orders
        HashMap<String, ProductTypeImpl> productTypes
        HashMap<String, ProductTypeImpl> products
        HashMap<Integer, BalanceOperationImpl> balanceOperations
        HashMap<Integer, UserImpl> users
        HashMap<Integer, CustomerImpl> customers
        HashMap<String, CreditCardImpl> creditCards

        +EZShop()
        +EZShop(int)
        -boolean creditCardIsValid(String)
        -boolean barCodeIsValid(String)
        -int RoundUp(int)
    }

    together {

        interface ProductType
        interface SaleTransaction
        interface TicketEntry
        interface User
        interface BalanceOperation
        interface Customer
        interface Order 
    }
}

ProductType -up[hidden]- EZShop 
API <|-- EZShop : <<implements>>
note right of EZShop: Permanent class
@enduml
```

### it.polito.ezshop.model
```plantuml
@startuml
package it.polito.ezshop.model{
    class UserImpl {
        -id: Integer
        -username: String
        -password: String
        -role: String
        {static}serialVersionUID: final long
        {static}PROGRESSIVE_ID: Integer
    }

    class ProductTypeImpl {
        -id : Integer
        -barCode : String
        -productDescription : String
        -pricePerUnit : Double 
        -sellPrice : Float
        -quantity : Integer
        -discountRate : Float
        -note : String
        -location : String
        {static}PROGRESSIVE_ID: Integer
    }

    class OrderImpl {
        -balanceId : Integer
        -productCode : String
        -pricePerUnit : Double
        -quntity : Integer
        -status : String
        -orderId : Integer
        -date : LocalDate
        -balanceOperation : BalanceOprationImpl
        {static}PROGRESSIVE_ID: Integer
    }

    class CustomerImpl {
        - id : Integer
        - customerName : String
        - points : Integer
        - customerCard : Integer
        {static}PROGRESSIVE_ID: Integer
        {static}PROGRESSIVE_CARD_ID: Integer
        {static}serialVersionUID: final long
    }

    class BalanceOperationImpl {
        - balanceId : Integer
        - date: LocalDate
        - money: double
        - type: String
        {static}PROGRESSIVE_ID: Integer
        {static}serialVersionUID: final long
    }

    class ReturnTransactionImpl {
        -saleTransaction : SaleTransactionImpl
        -id : Integer
        -entries : List<TicketEntry>
        -price : double
        -date : LocalDate
        -balanceOperation : BalanceOperationImpl
        {static}PROGRESSIVE_ID: Integer
        {static}serialVersionUID: final long
    }

    class SaleTransactionImpl {
        -ticketNumber : Integer
        -entries : List<TicketEntry>
        -discountRate : double
        -price : double
        -date : LocalDate
        -balanceOperation : BalanceOperationImpl
        {static}PROGRESSIVE_ID: Integer
        {static}serialVersionUID: final long
    }

    class TicketEntryImpl {
        -barCode : String
        -productDescription : String
        -amount : Integer
        -pricePerUnit : double
        -discountRate : double
        {static}serialVersionUID: final long
    }

    class CreditCardImpl {
        -code :String
        -balance:String
        {static}serialVersionUID: final long
    }

    OrderImpl "1" -- "1" BalanceOperationImpl
    ReturnTransactionImpl "1" -- "1" SaleTransactionImpl
    ReturnTransactionImpl "1" -- "1" BalanceOperationImpl
    ReturnTransactionImpl "1" -- "*" TicketEntryImpl
    SaleTransactionImpl "1" -- "1" BalanceOperationImpl
    SaleTransactionImpl "1" -- "*" TicketEntryImpl

}
@enduml
```

### it.polito.ezshop.exceptions
```plantuml
@startuml
package it.polito.ezshop.exceptions {
    class "Exception" as E
    InvalidCreditCardException -down-|> E
    InvalidCustomerCardException -down--|> E
    InvalidCustomerIdException -down-|> E
    InvalidCustomerNameException -down--|> E
    InvalidDiscountRateException -down-|> E

    InvalidLocationException -up-|> E
    InvalidOrderIdException -up--|> E
    InvalidPasswordException -up-|> E
    InvalidPaymentException -up--|> E
    InvalidPricePerUnitException -up-|> E

    InvalidProductCodeException -up-|> E
    InvalidProductDescriptionException -up--|> E
    InvalidProductIdException -up-|> E
    InvalidQuantityException -up--|> E
    InvalidRoleException -up-|> E

    InvalidTransactionIdException -down-|> E
    InvalidUserIdException -down--|> E
    InvalidUsernameException -down-|> E
    InvalidRFIDException -down--|> E
    UnauthorizedException -down-|> E
}
@enduml
```

# Verification traceability matrix

| - | Shop | User | ProductType | Position | OrderState | Order | Customer | LoyaltyCard | BalanceOperation | BOType | ReturnTransaction | SaleTransaction | TransactionState |
| :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: | :--: |
| FR1 | V | V | | | | | | | | | | | |
| FR3 | V | | V | V | | | | | | | | | |
| FR4 | V | | V | V | V | V | | | | | | | |
| FR5 | V | | | | | | V | V | | | | | |
| FR6 | V | | V | | | | | | V | V | V | V | V |
| FR7 | V | | | | | | | | V | | | | |
| FR8 | V | | | | V | V | | | V | V | V | V | V |

# Verification sequence diagrams 

### Scenario 1.1
```plantuml
@startuml
actor User
User -> GUI: Insert description, productCode, pricePerUnit, note
GUI -> EZShop: createProductType()
EZShop -> ProductTypeImpl: new ProductTypeImpl()
EZShop <- ProductTypeImpl: return productType
EZShop -> ProductTypeImpl: productType.getID()
EZShop <- ProductTypeImpl: return productID
GUI <- EZShop: return productID
User <- GUI: successful message
User -> GUI: Insert position of ProductType
GUI -> EZShop: updatePosition()
EZShop -> ProductTypeImpl: productType.setLocation()
EZShop <- ProductTypeImpl: return
GUI <- EZShop: return true
User <- GUI: successful message
@enduml
```

### Scenario 1.2
```plantuml
@startuml
actor User
User -> GUI: Insert barcode
GUI -> EZShop: getProductTypeByBarCode()
GUI <- EZShop: return productType
User <- GUI: successful message
User -> GUI: Insert new position of Product
GUI -> EZShop: updatePosition()
EZShop -> ProductTypeImpl: productType.setLocation()
EZShop <- ProductTypeImpl: return
GUI <- EZShop: return true
User <- GUI: successful message
@enduml
```


### Scenario 2.1
```plantuml
@startuml
actor Admin
Admin -> GUI : Define credentials of new account
Admin -> GUI : Select access rights
Admin -> GUI : Confirms inserted data
GUI -> EZShop : createUser()
EZShop -> UserImpl : new UserImpl()
UserImpl -> EZShop : return user
EZShop -> GUI : return userID
GUI -> Admin : Successfull message
@enduml
```

### Scenario 3.1
```plantuml
@startuml
actor User
User -> GUI: Insert productCode, quantity, pricePerUnit
GUI -> EZShop: issueOrder()
EZShop -> OrderImpl: new OrderImpl()
EZShop <- OrderImpl: return order
EZShop -> OrderImpl: order.getID()
EZShop <- OrderImpl: return orderID
GUI <- EZShop: return orderID
User <- GUI: successful message
@enduml
```

### Scenario 3.2
```plantuml
@startuml
actor User
User -> GUI: Insert orderID
GUI -> EZShop: payOrder()
EZShop -> OrderImpl: order.setStatus()
EZShop <- OrderImpl: return true
GUI <- EZShop: return true
User <- GUI: successful message
@enduml
```

### Scenario 4.1
```plantuml
@startuml
Actor User as U
U -> GUI: Define a new customer
GUI -> EZShop: defineCustomer()
EZShop -> CustomerImpl: new CustomerImpl()
EZShop <- CustomerImpl: return customer
EZShop -> CustomerImpl : customer.getID()
CustomerImpl -> EZShop : return id
GUI <- EZShop: return customerID
U <- GUI: successful message
@enduml
```

### Scenario 4.2
```plantuml
@startuml
Actor User as U
U -> GUI: Define a new card
GUI -> EZShop: createCard()
EZShop -> EZShop: generates card
GUI <- EZShop: return String
U <- GUI: successful message
U -> GUI: Attach card to customer
GUI -> EZShop: attachCardToCustomer()
EZShop -> CustomerImpl: customer.setCustomerCard()
EZShop <- CustomerImpl: return true
GUI <- EZShop: return true
U <- GUI: successful message
@enduml
```

### Scenario 5.1
```plantuml
@startuml
actor User
User -> GUI : Insert username
User -> GUI : Insert password
GUI -> EZShop : login()
EZShop -> GUI : return user
GUI -> User : Successful message
@enduml
```

### Scenario 6.2
```plantuml
@startuml
actor Cashier
Cashier -> GUI: Start a new sale transaction
GUI  -> EZShop: startSaleTransaction()
EZShop -> SaleTransactionImpl: new SaleTransactionImpl()
EZShop <- SaleTransactionImpl: return saleTransaction
GUI <- EZShop: return ID
GUI -> Cashier: Ask for product bar code
GUI -> Cashier: Ask for product units
Cashier -> GUI: Insert product bar code
Cashier -> GUI: Set N units of product P
GUI  -> EZShop: addProductToSale()
EZShop -> TicketEntryImpl: products.get(productCode)
EZShop -> TicketEntryImpl: new TicketEntryImpl()
TicketEntryImpl -> EZShop: return ticketEntry
EZShop -> TicketEntryImpl : ticketEntry.setAmount()
EZShop -> SaleTransactionImpl: saleTransaction.setEntries()
EZShop -> GUI: return true
GUI -> Cashier : Ask for product discount rate 
Cashier -> GUI: Apply product discount rate  
GUI -> EZShop: applyDiscountRateToProduct()
EZShop -> TicketEntryImpl : ticketEntry.getBarCode()
EZShop -> SaleTransactionImpl: saleTransaction.setDiscountRate()
EZShop -> GUI: return true
Cashier -> GUI: End Sale transaction
GUI  -> EZShop: endSaleTransaction()
GUI <- EZShop: return true
Cashier -> GUI: Manage payment (UC7)
Cashier <- GUI: Print sale receipt
@enduml
```


### Scenario 6.4
```plantuml
@startuml
actor Cashier
Cashier -> GUI: Start a new sale transaction
GUI  -> EZShop: startSaleTransaction()
EZShop -> SaleTransactionImpl: new SaleTransactionImpl()
EZShop <- SaleTransactionImpl: return saleTransaction
GUI <- EZShop: return ID
GUI -> Cashier: Ask for product bar code
GUI -> Cashier: Ask for product units
Cashier -> GUI: Insert product bar code
Cashier -> GUI: Set N units of product P
GUI  -> EZShop: addProductToSale()
EZShop -> TicketEntryImpl: new TicketEntryImpl()
TicketEntryImpl -> EZShop: return ticketEntry
EZShop -> ProductTypeImpl: ticketEntry.setAmount()
EZShop -> SaleTransactionImpl: saleTransaction.setEntries()
EZShop -> GUI: return true
Cashier -> GUI: End Sale transaction
GUI  -> EZShop: endSaleTransaction()
GUI <- EZShop: return true
Cashier <- GUI: Ask for payment type
Cashier -> GUI: Insert LoyaltyCard number
GUI -> EZShop: computePointsForSale() 
EZShop -> GUI: return points 
Cashier -> GUI: Manage payment(UC7)
GUI -> EZShop: modifyPointsOnCard()
EZShop -> CustomerImpl: customer.setPoints()
EZShop <- CustomerImpl: return true
EZShop -> GUI: return true
Cashier <- GUI: Print ticket
@enduml
```


### Scenario 7.1
```plantuml
@startuml
Actor User as U
U -> GUI: Manage payment by credit card
GUI -> EZShop: receiveCreditCardPayment()
EZShop -> EZShop: creditCardIsValid()
EZShop -> SaleTransactionImpl: check ticket
EZShop <- SaleTransactionImpl: return true
EZShop -> CreditCardImpl: check balance
EZShop <- CreditCardImpl: return true
GUI <- EZShop: return true
U <- GUI: successful message
@enduml
```

### Scenario 7.4
```plantuml
@startuml
Actor User as U
U -> GUI: Manage payment by cash
GUI -> EZShop: receiveCashPayment()
EZShop -> SaleTransactionImpl: saleTransaction.getPrice()
EZShop <- SaleTransactionImpl: return price
EZShop -> EZShop: update balance
GUI <- EZShop: return change
U <- GUI: successful message (includes change)
@enduml
```
### Scenario 8.1

```plantuml
@startuml
actor Cashier
Cashier -> GUI : Insert transactionID
GUI -> EZShop : startReturnTransaction()
EZShop -> ReturnTransactionImpl : new ReturnTransactionImpl()
ReturnTransactionImpl -> EZShop : return returnTransaction
EZShop -> GUI : return returnTransaction.getId()
GUI -> Cashier : Ask for product bar code
GUI -> Cashier : Ask for product units
Cashier -> GUI : Insert product bar code
Cashier -> GUI : Set N units of product P
GUI -> EZShop : returnProduct()
EZShop -> EZShop : openedReturnTransaction.get(returnId)
EZShop -> TicketEntryImpl : new TicketEntryImpl()
TicketEntryImpl -> EZShop : return ticketEntry
EZShop -> ReturnTransactionImpl : returnTransaction.addEntry(ticketEntry)
EZShop -> GUI : return true
GUI -> Cashier : Manage credit card return
Cashier -> GUI : Close return transaction
GUI -> EZShop : endReturnTransaction()
EZShop -> ProductTypeImpl : productType.setAmount()
EZShop -> GUI : return true
GUI -> Cashier : Successful message
@enduml
```
### Scenario 9.1

```plantuml
@startuml
actor Manager
Manager -> GUI : Select start date
Manager -> GUI : Select end date
GUI -> EZShop : getCreditsAndDebits()
EZShop -> GUI : return creditsAndDebits
GUI -> Manager : List displayed
@enduml
```

### Scenario 10.1
```plantuml
@startuml
actor Cashier
GUI -> Cashier: ask for credit card number
Cashier -> GUI: Insert credit card number
GUI -> EZShop: returnCreditCardPayment()
EZShop -> ReturnTransactionImpl:  returnTransaction.getPrice()
EZShop <- ReturnTransactionImpl: return money
GUI <- EZShop: return money
Cashier <- GUI: Successful message
@enduml
```

