package tn.esprit.main;

import tn.esprit.entities.Admin;
import tn.esprit.entities.Client;
import tn.esprit.entities.Feedback;
import tn.esprit.entities.User;
import tn.esprit.services.FeedbackServices;
import tn.esprit.services.UserServices;
import tn.esprit.utils.MyDatabase;

import java.util.Date;

public class Main {

   public static void main(String[] args) {

      MyDatabase db1 = MyDatabase.getInstance();

      UserServices userServices = new UserServices();
      FeedbackServices feedbackServices = new FeedbackServices();

//---------------------------------------CREATE --------------------------------------------
    /*  Client newClient = new Client("dhaw619@example.com", "password123", User.Role.CLIENT,
              "dhaw", "chiri", "58455890", "Credit Card", "123 Main St");


      userServices.add(newClient);
     // System.out.println("New client added!");*/


      //-------------------------------get UserbyID---------------------------------------------------
   /*   User foundUser = userServices.getById(1);
      if (foundUser != null) {
         System.out.println("Found user: " + foundUser);
      } else {
         System.out.println("User not found!");
      }*/

      //-----------------------------------list users info-------------------------------------------------
/*
      System.out.println("All Users:");
      for (User user : userServices.getAll()) {
         System.out.println(user);
      }
*/
      //--------------------------------Update user(client or admin)-----------------------------------------------

     /* Client updatedClient = new Client("updatedclient@example.com", "newpassword", User.Role.CLIENT,
              "Jane", "Doe", "9876543210", "PayPal", "456 Elm St");
      userServices.updateAccount(1, updatedClient);
      System.out.println("User updated!");*/

      //----------------------------------- Delete----------------------------------
/*
      int clientId = 2;
      userServices.delete(clientId);
      System.out.println("Client deleted successfully!");

*/
      //---------------------------------------- Login ------------------------------------
     /* String email = "dhaw619@example.com";
      String password = "password123";
      User loggedInUser = userServices.login(email, password );
      if (loggedInUser != null) {
         System.out.println("Login successful! Welcome " + loggedInUser.getFirstNameUser());
      } else {
         System.out.println(" Invalid email or password.");
      }*/









      /// ----------------------------------------------------------------FEEDBACK CRUD----------------------------------------------

      /// ---create---

    /*  Feedback newFeedback = new Feedback("bad!", 2, new Date(), 2);

      feedbackServices.add(newFeedback);
*/


      ///  ---- get FEEDBACK by iD ----
     /* Feedback foundFeedback = feedbackServices.getById(2);
      if (foundFeedback != null) {
         System.out.println("Found Feedback: " + foundFeedback);
      } else {
         System.out.println("Feedback not found!");
      }*/

      ///  --- show all feedbacks
/*
      System.out.println("All Feedbacks:");
      for (Feedback feedback : feedbackServices.getAll()) {
         System.out.println(feedback);
      }

*/


      ///----- Update feedback-------
      /*
      Feedback updatedFeedback = new Feedback("Good service, but can improve.", 4, new Date(), 1);
      feedbackServices.updateAccount(1, updatedFeedback);
      */

      /// ---- Delete feedback ----
      /*
      feedbackServices.delete(3);
      */







   }
}
