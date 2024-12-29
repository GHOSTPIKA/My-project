package Banking.Bank.Controller;

import Banking.Bank.Model.Customer;
import Banking.Bank.Model.Transaction;
import Banking.Bank.Service.UserService;
import Banking.Bank.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/signup")
    public ResponseEntity<Customer> signup(@RequestBody Customer user) {
        Customer savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Customer user) {
        Optional<Customer> loggedInUser = userService.loginUser(user.getEmail(), user.getPassword());

        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(loggedInUser.get());
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/{customerId}/pay-bill")
    public ResponseEntity<?> payBill(@PathVariable Long customerId, @RequestBody BillPaymentRequest request) {
        String response = userService.payBill(customerId, request.getBiller(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{customerId}/transfer-funds")
    public ResponseEntity<?> transferFunds(
            @PathVariable Long customerId,
            @RequestBody TransferRequest request) {
        String response = userService.transferFunds(customerId, request.getRecipientUsername(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long customerId) {
        List<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable Long customerId) {
        Optional<Customer> customer = userService.getUserById(customerId);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get().getBalance());
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}

class BillPaymentRequest {
    private String biller;
    private Double amount;

    public String getBiller() { return biller; }
    public void setBiller(String biller) { this.biller = biller; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}

class TransferRequest {
    private String recipientUsername;
    private Double amount;

    public String getRecipientUsername() { return recipientUsername; }
    public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
