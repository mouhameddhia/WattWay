package tn.esprit.entities;

public class Admin extends User {
    private FunctionAdmin functionAdmin;  // Use enum instead of String

    public Admin() {
        super();
        this.functionAdmin = FunctionAdmin.MANAGER;  // Default to MANAGER
    }

    public Admin(int idUser, String emailUser, String passwordUser, String firstNameUser, String lastNameUser,
                 FunctionAdmin functionAdmin) {
        super(idUser, emailUser, passwordUser, Role.ADMIN, firstNameUser, lastNameUser);

        this.functionAdmin = functionAdmin;
    }

    public Admin(String emailUser, String passwordUser, Role roleUser, String firstNameUser, String lastNameUser,
                 FunctionAdmin functionAdmin) {
        super(emailUser, passwordUser, Role.ADMIN, firstNameUser, lastNameUser);
        this.functionAdmin = functionAdmin;
    }
    // Constructor with role included
    public Admin(int idUser, String emailUser, String passwordUser, String firstNameUser, String lastNameUser,
                 FunctionAdmin functionAdmin, Role roleUser) {
        super(idUser, emailUser, passwordUser, roleUser, firstNameUser, lastNameUser);  // Pass role to super constructor
        this.functionAdmin = functionAdmin;
    }

    public Admin(int idUser, String email, String password, String firstName, String lastName, String role, FunctionAdmin functionAdmin, Object o) {
    }


    public FunctionAdmin getFunctionAdmin() {
        return functionAdmin;
    }

    public void setFunctionAdmin(FunctionAdmin functionAdmin) {
        this.functionAdmin = functionAdmin;
    }

    @Override
    public String toString() {
        return super.toString() + ", adminType='" + functionAdmin + "'}";
    }

    // Enum for admin functions
    public enum FunctionAdmin {
        MANAGER, HEAD_OF_MECHANICS
    }
}
