package Banking.Bank.Service;

import Banking.Bank.Model.Customer;
import Banking.Bank.Model.Transaction;
import Banking.Bank.Repositiry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    public Customer saveUser(Customer user) {
        return userRepository.save(user);
    }

    public Optional<Customer> loginUser(String email, String password) {
        Optional<Customer> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    public String payBill(Long customerId, String biller, Double amount) {
        Optional<Customer> userOptional = userRepository.findById(customerId);
        if (userOptional.isPresent()) {
            Customer user = userOptional.get();

            if (user.getBalance() < amount) {
                return "Insufficient balance";
            }

            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);

            Transaction transaction = new Transaction();
            transaction.setType("bill payment");
            transaction.setDescription("Payment to " + biller);
            transaction.setAmount(amount);
            transaction.setCustomerId(customerId);
            transactionService.saveTransaction(transaction);

            return "Bill payment successful";
        }
        return "User not found";
    }

    public String transferFunds(Long senderId, String recipientUsername, Double amount) {
        Optional<Customer> senderOptional = userRepository.findById(senderId);
        Optional<Customer> recipientOptional = userRepository.findByName(recipientUsername);

        if (senderOptional.isEmpty() || recipientOptional.isEmpty()) {
            return "Sender or recipient not found";
        }

        Customer sender = senderOptional.get();
        Customer recipient = recipientOptional.get();

        if (sender.getBalance() < amount) {
            return "Insufficient balance";
        }

        sender.setBalance(sender.getBalance() - amount);
        userRepository.save(sender);

        recipient.setBalance(recipient.getBalance() + amount);
        userRepository.save(recipient);

        Transaction senderTransaction = new Transaction();
        senderTransaction.setType("transfer");
        senderTransaction.setDescription("Transfer to " + recipientUsername);
        senderTransaction.setAmount(amount);
        senderTransaction.setCustomerId(senderId);
        transactionService.saveTransaction(senderTransaction);

        Transaction recipientTransaction = new Transaction();
        recipientTransaction.setType("transfer");
        recipientTransaction.setDescription("Received from " + sender.getName());
        recipientTransaction.setAmount(amount);
        recipientTransaction.setCustomerId(recipient.getId());
        transactionService.saveTransaction(recipientTransaction);

        return "Transfer successful";
    }

    public Optional<Customer> getUserById(Long customerId) {
        return userRepository.findById(customerId);
    }
}
