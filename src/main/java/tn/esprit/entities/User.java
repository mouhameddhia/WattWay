package tn.esprit.entities;

public class User {
    private int idUser;
    private String emailUser;
    private String passwordUser;
    private Role roleUser;
    protected String firstNameUser;
    protected String lastNameUser;

    public void getLastNameUser(String lastName) {
    }

    public enum Role {
        ADMIN, CLIENT
    }


    public User() {
        this.idUser = 0;  // Or leave it to be assigned later
        this.emailUser = "";
        this.passwordUser = "";
        this.roleUser = Role.CLIENT;
        this.firstNameUser = "";
        this.lastNameUser = "";

    }


    public User(int idUser, String emailUser, String passwordUser, Role roleUser,String firstNameUser, String lastNameUser) {
        this.idUser = idUser;
        this.emailUser = emailUser;
        this.passwordUser = passwordUser;
        this.roleUser = roleUser;
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
    }


    public User(String emailUser, String passwordUser, Role roleUser,String firstNameUser, String lastNameUser) {
        this.emailUser = emailUser;
        this.passwordUser = passwordUser;
        this.roleUser = roleUser;
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;

    }


    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public Role getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(Role roleUser) {
        this.roleUser = roleUser;
    }
    public String getFirstNameUser() {
        return firstNameUser;
    }
    public void setFirstNameUser(String firstNameUser) {
        this.firstNameUser = firstNameUser;
    }

    public void setLastNameUser(String lastNameUser) {
        this.lastNameUser = lastNameUser;
    }

    public String getLastNameUser() {
        return lastNameUser;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + idUser +
                ", email='" + emailUser + '\'' +
                ", role=" + roleUser +
                ", firstName='" + firstNameUser + '\'' +
                ", lastName='" + lastNameUser + '\'' +
                '}';
    }

}
