package tn.esprit.entities;
import javafx.beans.property.*;

public class Client extends User {

    private String phoneNumberClient;
    private PaymentDetails paymentDetailsClient;  // Use enum instead of String
    private String addressClient;

    // Default constructor
    public Client() {
        super();  // Call the default constructor of the superclass (User)
        this.phoneNumberClient = "";
        this.paymentDetailsClient = PaymentDetails.PAYPAL;  // Default to PAYPAL
        this.addressClient = "";
    }

    // Normal constructor with all fields
    public Client(int idUser, String emailUser, String passwordUser, String firstNameUser, String lastNameUser,
                  String phoneNumberClient, PaymentDetails paymentDetailsClient, String addressClient) {
        super(idUser, emailUser, passwordUser, Role.CLIENT, firstNameUser, lastNameUser);

        this.phoneNumberClient = phoneNumberClient;
        this.paymentDetailsClient = paymentDetailsClient;
        this.addressClient = addressClient;
    }

    public Client(String emailUser, String passwordUser, Role roleUser, String firstNameUser, String lastNameUser,
                  String phoneNumberClient, PaymentDetails paymentDetailsClient, String addressClient) {
        super(emailUser, passwordUser, Role.CLIENT, firstNameUser, lastNameUser);  // Set the role to CLIENT
        this.phoneNumberClient = phoneNumberClient;
        this.paymentDetailsClient = paymentDetailsClient;
        this.addressClient = addressClient;
    }
    // Constructor with role
// Constructor with role included
    public Client(int idUser, String emailUser, String passwordUser, String firstNameUser, String lastNameUser,
                  String phoneNumberClient, PaymentDetails paymentDetailsClient, String addressClient, Role roleUser) {
        super(idUser, emailUser, passwordUser, roleUser, firstNameUser, lastNameUser);  // Pass role to super constructor
        this.phoneNumberClient = phoneNumberClient;
        this.paymentDetailsClient = paymentDetailsClient;
        this.addressClient = addressClient;
    }

    public Client(int idUser, String email, String password, String firstName, String lastName, String phoneNumber, PaymentDetails paymentDetails, String address, String role) {
    }

    public Client(int idUser, String email, String password, String firstName, String lastName, String role, String phoneNumber, PaymentDetails paymentDetails, String address, String role1) {
    }


    // Getters and setters

    public String getPhoneNumberClient() {
        return phoneNumberClient;
    }

    public void setPhoneNumberClient(String phoneNumberClient) {
        this.phoneNumberClient = phoneNumberClient;
    }

    public PaymentDetails getPaymentDetailsClient() {
        return paymentDetailsClient;
    }

    public void setPaymentDetailsClient(PaymentDetails paymentDetailsClient) {
        this.paymentDetailsClient = paymentDetailsClient;
    }

    public String getAddressClient() {
        return addressClient;
    }

    public void setAddressClient(String addressClient) {
        this.addressClient = addressClient;
    }

    @Override
    public String toString() {
        return super.toString() + ", phoneNumber='" + phoneNumberClient + "', address='" + addressClient + "', paymentDetails='" + paymentDetailsClient + "'}";
    }

    // Enum for payment details options
    public enum PaymentDetails {
        PAYPAL, CREDIT_CARD, BANK_TRANSFER
    }
}
