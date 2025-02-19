package tn.esprit.services;

import tn.esprit.entities.Admin;
import tn.esprit.entities.Client;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices implements IService<User> {

    private Connection con;

    public UserServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(User entity) {
        if (entity instanceof Admin) {
            // Logic for adding Admins
            String query = "INSERT INTO User (emailUser, passwordUser, roleUser, firstNameUser, lastNameUser, functionAdmin) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, entity.getEmailUser());
                ps.setString(2, entity.getPasswordUser());
                ps.setString(3, User.Role.ADMIN.name()); // Set role to ADMIN
                ps.setString(4, entity.getFirstNameUser());
                ps.setString(5, entity.getLastNameUser());

                // Assuming Admin class has a getFunctionAdmin() method
                Admin admin = (Admin) entity;
                ps.setString(6, String.valueOf(admin.getFunctionAdmin())); // Set the functionAdmin for Admin

                ps.executeUpdate();
                System.out.println("Admin registered successfully.");
            } catch (SQLException e) {
                System.out.println("Error adding admin: " + e.getMessage());
            }
            return; // Stop further processing
        }

        // Logic for adding other user types (e.g., Client)
        String query = "INSERT INTO User (emailUser, passwordUser, roleUser, firstNameUser, lastNameUser, phoneNumber, paymentDetails, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, entity.getEmailUser());
            ps.setString(2, entity.getPasswordUser());
            ps.setString(3, User.Role.CLIENT.name());
            ps.setString(4, entity.getFirstNameUser());
            ps.setString(5, entity.getLastNameUser());

            if (entity instanceof Client) {
                Client client = (Client) entity;
                ps.setString(6, client.getPhoneNumberClient());
                ps.setString(7, client.getPaymentDetailsClient().name()); // Store the enum name
                ps.setString(8, client.getAddressClient());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();
            System.out.println("Client registered successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    public boolean updateAccount(int idUser, User updatedUser) {
        String query = "UPDATE User SET emailUser=?, passwordUser=?, roleUser=?, firstNameUser=?, lastNameUser=?, "
                + "phoneNumber=?, paymentDetails=?, address=?, functionAdmin=? WHERE idUser=?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, updatedUser.getEmailUser());
            ps.setString(2, updatedUser.getPasswordUser());
            ps.setString(3, updatedUser.getRoleUser().toString());
            ps.setString(4, updatedUser.getFirstNameUser());
            ps.setString(5, updatedUser.getLastNameUser());

            if (updatedUser instanceof Client client) {
                // Set Client-specific fields
                ps.setString(6, client.getPhoneNumberClient());
                ps.setString(7, client.getPaymentDetailsClient().name());  // Store the enum name
                ps.setString(8, client.getAddressClient());
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else if (updatedUser instanceof Admin admin) {
                // Set Admin-specific field
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setString(9, admin.getFunctionAdmin().name());  // Store the enum name
            }

            ps.setInt(10, idUser); // WHERE condition
            ps.executeUpdate();
            System.out.println("Account updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating account: " + e.getMessage());
        }
        return false;
    }























    public void delete(int idUser) {
        String query = "DELETE FROM User WHERE idUser = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUser);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User with ID " + idUser + " deleted successfully.");
            } else {
                System.out.println("No user found with ID " + idUser + " to delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public User getById(int id) {
        String query = "SELECT * FROM User WHERE idUser = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("idUser");
                String email = rs.getString("emailUser");
                String password = rs.getString("passwordUser");
                String firstName = rs.getString("firstNameUser");
                String lastName = rs.getString("lastNameUser");
                String role = rs.getString("roleUser");

                if ("CLIENT".equals(role)) {
                    return new Client(idUser, email, password, firstName, lastName,
                            rs.getString("phoneNumber"),
                            Client.PaymentDetails.valueOf(rs.getString("paymentDetails")),  // Convert string to enum
                            rs.getString("address"));
                } else if ("ADMIN".equals(role)) {
                    return new Admin(idUser, email, password, firstName, lastName,
                            Admin.FunctionAdmin.valueOf(rs.getString("functionAdmin")));  // Convert string to enum
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user by ID: " + e.getMessage());
        }
        return null;
    }

    public  User getUserByEmail(String email) {
        String query = "SELECT * FROM User WHERE emailUser = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("idUser");
                String password = rs.getString("passwordUser");
                String firstName = rs.getString("firstNameUser");
                String lastName = rs.getString("lastNameUser");
                String role = rs.getString("roleUser");

                if ("CLIENT".equals(role)) {
                    return new Client(idUser, email, password, firstName, lastName,
                            rs.getString("phoneNumber"),
                            Client.PaymentDetails.valueOf(rs.getString("paymentDetails")),  // Convert string to enum
                            rs.getString("address"));
                } else if ("ADMIN".equals(role)) {
                    return new Admin(idUser, email, password, firstName, lastName,
                            Admin.FunctionAdmin.valueOf(rs.getString("functionAdmin")));  // Convert string to enum
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idUser = rs.getInt("idUser");
                String email = rs.getString("emailUser");
                String password = rs.getString("passwordUser");
                String firstName = rs.getString("firstNameUser");
                String lastName = rs.getString("lastNameUser");
                String role = rs.getString("roleUser");

                if ("CLIENT".equals(role)) {
                    users.add(new Client(idUser, email, password, firstName, lastName,
                            rs.getString("phoneNumber"),
                            Client.PaymentDetails.valueOf(rs.getString("paymentDetails")),  // Convert string to enum
                            rs.getString("address")));
                } else if ("ADMIN".equals(role)) {
                    users.add(new Admin(idUser, email, password, firstName, lastName,
                            Admin.FunctionAdmin.valueOf(rs.getString("functionAdmin"))));  // Convert string to enum
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all users: " + e.getMessage());
        }
        return users;
    }
    @Override
    public User login(String email, String password) {
        String query = "SELECT * FROM User WHERE emailUser = ? AND passwordUser = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("idUser");
                User.Role role = User.Role.valueOf(rs.getString("roleUser"));
                String firstName = rs.getString("firstNameUser");
                String lastName = rs.getString("lastNameUser");

                if (role == User.Role.CLIENT) {
                    String phone = rs.getString("phoneNumber");
                    Client.PaymentDetails payment = Client.PaymentDetails.valueOf(rs.getString("paymentDetails"));  // Convert string to enum
                    String address = rs.getString("address");

                    return new Client(userId, email, password, firstName, lastName, phone, payment, address);
                } else if (role == User.Role.ADMIN) {
                    Admin.FunctionAdmin function = Admin.FunctionAdmin.valueOf(rs.getString("functionAdmin"));
                    return new Admin(userId, email, password, firstName, lastName, function);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }






}
