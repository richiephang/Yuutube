package yuutube;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class user {

    private static PreparedStatement pS;
    private static ResultSet result;
    private static boolean login_status = false;
    private static String user_name;
    private static int ID;
    private static int subscribeBoolean;
    private static int subscribersCount;

    //method to register
public static void register() {
        //user register

        String name, email, pass,pass2;

        Scanner s = new Scanner(System.in);
        System.out.print("\nEnter username: ");
        name = s.nextLine();
        
        while (checkUsername (name))
        {
            System.out.println("username has been taken, please try another one.");
            System.out.print("\nEnter another username: ");
            name = s.nextLine();
        }
        
        email=email();
        System.out.println();
        System.out.println("User name: "+name);
        System.out.println("Email: "+ email);
        pass=password();
        Register(name, email, pass);
        System.out.println();
        System.out.println("Please log in to you account");
        login();
    }
    
// method to confirm the password
    public static String password()
    {
        Scanner s= new Scanner (System.in);
        System.out.print("Enter password: ");
        String pass = s.nextLine();
        System.out.print("Reconfirm the password: ");
        String pass2= s.nextLine();
        while(!pass.equals(pass2))
        {
            System.out.println("Password does not match, please try again");
            System.out.print("Enter password: ");
            pass = s.nextLine();
            System.out.print("Reconfirm the password: ");
            pass2= s.nextLine();
        }
        return pass;
    }
    
    //check whether the email fullfil requirements
        public static String email()
    {
        Scanner s=new Scanner (System.in);
        System.out.println("Your email should be in the form of user@emailhost.com");
        System.out.println("Eg: abc@ ymail.com");
        System.out.println("Please take note that user should not contain character '@' ");
        System.out.print("Enter email: ");
        String email = s.nextLine();
        
        while(checkmail(email))
        {
            System.out.println("\nInvalid form of email");
            System.out.println("Please try again");
            System.out.print("Enter email: ");
            email=s.nextLine();
        } 
        
        while (checkEmail(email))
        {
            System.out.println("\nThe email has been registered");
            System.out.println("Please use another email ");
            System.out.print("Enter email: ");
            email=s.nextLine();
            while(checkmail(email))
            {
                System.out.println("\nInvalid form of email");
                System.out.println("Please try again");
                System.out.print("Enter email: ");
                email=s.nextLine();
            } 
        }
        

        return email;

    }
    
    //check whether email fullfil requirement
    public static boolean checkmail(String email)
    {
        boolean result=false;
        boolean check1=false;
        int j=0;
        for (int i=0; i<email.length(); i++)
        {
            if (email.charAt(i)=='@'&&i>0)
            {
                j+=1;
            }
        }
        if (j==1)
        {
            check1=true;
        }
        int check=0;
        boolean check2=true;
        if (check1&&check2)
        {
            result=false;
        }
        else
        {
            result=true;
        }
        return result;
    }
    
    
       
   public static void Register(String name, String email, String password) {
        //connect register form to MySQL
        connection a = new connection();
        try {

            //? is unspecified value, to substitute in an integer, string, double or blob value.
            String register = "INSERT INTO user (username,email,password) VALUES(?, ?, ?)";

            //insert record of register 
            pS = a.getConnection().prepareStatement(register);

            // create the mysql insert preparedstatement
            //.setString : placeholders that are only replaced with the actual values inside the system
            pS.setString(1, name);
            pS.setString(2, email);
            pS.setString(3, password);

            pS.executeUpdate(); //return int value

            System.out.println("Registered Successfully ");

        } catch (SQLException e) {
            System.out.println("Failed to register. Try again!");
        }
        try {
            
            String sql ="SELECT `user_ID` FROM `user` WHERE `email`='"+email+"'";
            pS = a.getConnection().prepareStatement(sql);
            result = pS.executeQuery();

            while (result.next()) {
                ID = result.getInt("user_ID");
            }

        } catch (SQLException ex) {
            System.out.println("get user id error");
        }
        try {

            String upload = "INSERT INTO `channel_subscription`(`channel_id`, `subscribers`) VALUES (" + ID + "," + 0 + ")";
            pS = a.getConnection().prepareStatement(upload);
            pS.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("initialize subs error");
        }

    }

    //method to check duplicate email
    
    public static boolean checkEmail(String email) {
        boolean dupEmail = false;
        try {
            connection b = new connection();

            String sql = "SELECT * FROM `user` WHERE `email`='"+email+"'";
            pS = b.getConnection().prepareStatement(sql);
          //  pS.setString(1, email);

            result = pS.executeQuery();

            if (result.next()) {
                //exist
                dupEmail = true;
            } else {
                dupEmail = false;
            }

        } catch (SQLException ex) {
            System.out.println("checkRegister error");
        }
        return dupEmail;
    }
    
    
    //method to check duplicate username
    
    public static boolean checkUsername(String username) {
        boolean dupUser = false;
        try {
            connection c = new connection();

            String sql = "SELECT * FROM `user` WHERE `username`='"+username+"'";
            pS = c.getConnection().prepareStatement(sql);
           // pS.setString(1, username);

            result = pS.executeQuery();

            if (result.next()) {
                //exist
                dupUser = true;
            } else {
                dupUser = false;
            }

        } catch (SQLException ex) {
            System.out.println("checkRegister error");
        }
        return dupUser;
    }

    //method to login
    public static void login() {
        Scanner s = new Scanner(System.in);
        try {
            connection a = new connection();

            String login = "SELECT * FROM `user` WHERE `email`= ? AND `password`= ?";
            pS = a.getConnection().prepareStatement(login);
            String email, pass;
            System.out.print("\nEnter email: ");
            email = s.nextLine();
            System.out.print("Enter password: ");
            pass = s.nextLine();

            pS.setString(1, email);
            pS.setString(2, pass);

            result = pS.executeQuery();

            if (result.next()) {
                //exist
                user_name = result.getString("username");
                ID = result.getInt("user_ID");
                System.out.println("\n****************************************");
                System.out.println("Welcome back " + user_name + "!");
                System.out.println("****************************************");
                login_status = true;
                tester.homepage();
                //  boolean upload_status = video.upload_status;
                if (video.isUpload_status()) {
                    video.uploadVideo();
                }
            } else {
                // nope
                System.out.println("Invalid pass or username");
            }

        } catch (SQLException ex) {
            System.out.println("connection error");
        }
    }
    
    public static void logout() 
    {
        Scanner s = new Scanner(System.in);

        user_name = null;
        ID = -99;
        System.out.println("You have been log out. Thank you :P ");
        login_status = false;
    }
    

    public static boolean isLogin_status() {
        return login_status;
    }

    public static String getUser_name() {
        return user_name;
    }

    public static int getID() {
        return ID;
    }

    // for manage account operation
    public static void getUserManage(int userID) throws SQLException, Exception {
        Scanner input = new Scanner(System.in);
        System.out.println("");
        System.out.println("What do you wish to do?");
        System.out.println("0. Return\n1. Display user's info\n2. Change your account's email address\n3. Change your account's password\n"
                + "4. Manage your channel");
        System.out.print("Please enter your choice: ");
        int userChoice = input.nextInt();
        System.out.println("");
        switch (userChoice) 
        {
            case 0:
                tester.homepage();;
                break;
            case 1:
                displayUserInfo(userID);
                break;
            case 2:
                changeEmail(userID);
                break;
            case 3:
                changePassword(userID);
                break;
            case 4:
                channelInfo(user_name);
                video.myVideo(user_name);
                break;

            default:
                tester.homepage();
                break; 
        }

    }

    public static ArrayList<String> displayUserInfo(int userID) throws SQLException, Exception {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("SELECT email, username FROM user WHERE user_ID = '" + userID + "'");
                ResultSet result = statement.executeQuery()) {
            ArrayList<String> array = new ArrayList<>();
            while (result.next()) {
                //System.out.println("Name: "+result.getString("displayName"));
                System.out.println("Username: " + result.getString("username"));
                System.out.println("Email: " + result.getString("email"));
                //array.add(result.getString("displayName"));
                array.add(result.getString("username"));
                array.add(result.getString("email"));
            }
            System.out.println("All records retrieved");
            getUserManage(userID);
            return array;
        } catch (SQLException e) {
            System.out.println("displayUserInfo error");

        }
        getUserManage(userID);
        return null;
    }
    

        
    private static void changeEmail(int userID) throws SQLException, Exception {
        Scanner input = new Scanner(System.in);
        connection a = new connection();
        System.out.print("Enter new email: ");
        String newEmail = input.nextLine();
        System.out.println("Do you confirm that you want to set your new email address to: " + newEmail);
        System.out.println("0. NO\n1. YES");
        int confirmEmail = input.nextInt();
        if (confirmEmail == 1) {
            updateEmail(userID, newEmail);
            getUserManage(userID);
        } else {
            getUserManage(userID);
        }
    }

    private static void changePassword(int userID) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter current password: ");
        String currentPassword = input.nextLine();
        int verified = checkCurrentPassword(userID, currentPassword);
        if (verified == 1) {
            System.out.print("Enter new password: ");
            String newPassword = input.nextLine();
            System.out.println("Confirm new password: ");
            String newPassword2 = input.nextLine();
            while (!newPassword2.equals(newPassword)) {
                System.out.println("Password entered not same, Please reentered your new password.");
                System.out.print("Enter new password: ");
                newPassword = input.nextLine();
                System.out.println("Confirm new password: ");
                newPassword2 = input.nextLine();
            }
            updatePassword(userID, newPassword);
            getUserManage(userID);
        } else {
            System.out.println("Password INCORRECT");
            getUserManage(userID);
        }
    }

    //method verifies currentPassword in FIELD user_password in Table userinfo
    private static int checkCurrentPassword(int userID, String currentPassword) throws SQLException, Exception {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("SELECT password FROM user WHERE user_ID = '" + userID + "' AND password = '" + currentPassword + "'");
                ResultSet result = statement.executeQuery()) {
            String user_password = "";
            while (result.next()) {
                user_password = result.getString("password");
            }
            if (currentPassword.equals(user_password)) {
                System.out.println("VERIFIED");
                return 1;
            }
        } catch (SQLException e) {
            System.out.println("checkCurrentPassword error");
        }
        return 0;
    }

    private static void updateEmail(int userID, String newEmail) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE user SET email = '" + newEmail + "' WHERE user_ID = '" + userID + "'")) {
            posted.executeUpdate();
            System.out.println("Successfully updated email address in user_info");
        } catch (SQLException e) {
            System.out.println("updateEmail error");
        }
    }

    private static void updatePassword(int userID, String newPassword) throws SQLException, Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE user SET password = '" + newPassword + "' WHERE user_ID = '" + userID + "'")) {
            posted.executeUpdate();
            System.out.println("Successfully updated user_password in user_info");
        } catch (SQLException e) {
            System.out.println("updatePassword error");
        }
    }

    // for subscription operation
    public static void userSubscription(int userID, int channel_userID) throws SQLException, Exception {
        Scanner input = new Scanner(System.in);
        //request user's action and store into user_history
        System.out.println("1. Subscribe\n2. Unsubscribe\n3. Cancel/Return");
        int userAction = input.nextInt();
        if(userID!=channel_userID){
            if (checkSubcribed(userID, channel_userID) == 0) { //if user does not subscribed the channel
                switch (userAction) {
                    case 1:
                        subscribeBoolean = 1;
                        increaseSubsCounts(channel_userID);
                        updateSubsStatus(userID, channel_userID, subscribeBoolean);
                        System.out.println("Subscribed");
                        break;
                    case 2:
                        System.out.println("You are not subscribing this channel.");
                        userSubscription(userID, channel_userID);
                        break;
                    case 3:
                        tester.homepage();
                        break;
                    default:
                        tester.homepage();
                        break;
                }
            } else { // if user subscribed the channel
                switch (userAction) {
                    case 1:
                        System.out.println("You are subscribing this channel.");
                        userSubscription(userID, channel_userID);
                        break;
                    case 2:
                        subscribeBoolean = 0;
                        decreaseSubsCounts(channel_userID);
                        updateSubsStatus(userID, channel_userID, subscribeBoolean);
                        System.out.println("Unsubscribed");
                        break;
                    case 3:
                        tester.homepage();
                        break;
                    default:
                        tester.homepage();
                        break;
                }
            }
        }else{
            System.out.println("You are not allowed to subscribe your own channel.");
        }
        video.videoFunction();
    }

    //method display subscription counts
    public static ArrayList<String> getSubscribe(int channel_userID) throws SQLException, Exception {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("SELECT channel_id, subscribers FROM channel_subscription WHERE channel_id = '" + channel_userID + "'");
                ResultSet result = statement.executeQuery()) {
            ArrayList<String> subsCount = new ArrayList<>();
            while (result.next()) {
                System.out.println("Channel ID: " + result.getString("channel_id"));
                System.out.println("Subscribers: " + result.getString("subscribers"));
                subsCount.add(result.getString("channel_id"));
                subsCount.add(result.getString("subscribers"));
            }
            System.out.println("ALL RECORD RETRIEVED"); // for debugging purpose
            return subsCount;
        } catch (SQLException e) {
            System.out.println("getSubscribe error");
        }
        return null;
    }

    //method return subscription boolean
    private static int checkSubcribed(int userID, int channel_userID) throws SQLException {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("Select user_id, channel_id, subscribed FROM user_subscription WHERE user_id = '" + userID + "' AND channel_id = '" + channel_userID + "'");
                ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt("subscribed");
            }
        } catch (SQLException e) {
            System.out.println("checkSubcribed error");
        }
        return 0;
    }

    //method update FIELD subscribers +1 in table channel_subs
    private static void increaseSubsCounts(int channel_userID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE channel_subscription SET subscribers =  subscribers +1 WHERE channel_id = '" + channel_userID + "'")) {
            posted.executeUpdate();
            System.out.println("Succesfully update channel_subs");
        } catch (SQLException e) {
            System.out.println("increaseSubsCounts error");
        }
    }

    private static void decreaseSubsCounts(int channel_userID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE channel_subscription SET subscribers =  subscribers -1 WHERE channel_id = '" + channel_userID + "'")) {
            posted.executeUpdate();
            System.out.println("Succesfully update channel_subs");
        } catch (SQLException e) {
            System.out.println("decreaseSubsCounts error");
        }
    }

    // Method update FIELD subscriber in table user_history
   private static void updateSubsStatus(int userID, int channel_userID, int subscribeBoolean) throws SQLException {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("INSERT INTO `user_subscription`(`user_id`, `channel_id`, `subscribed`) VALUES ('" + userID + "','" + channel_userID + "','" + subscribeBoolean + "')"
                        + "ON DUPLICATE KEY UPDATE `subscribed`='" + subscribeBoolean + "'")) {
            posted.executeUpdate();
            System.out.println("Successfully updated user_history");
        } catch (SQLException e) {
            System.out.println("updateSubsStatus error");
        }
    }

    // for comment operaation
    public static void userComment(int userID, int videoID) throws Exception {
        Scanner input = new Scanner(System.in);

        System.out.print("Add a comment: ");
        String inputComment = input.nextLine();
        // ask user to confirm his/her comment's content
        System.out.println("Are you sure you want to leave the comment \"" + inputComment + "\"");
        System.out.println("0. NO\n1. YES");
        int confirmComment = input.nextInt();
        if (confirmComment == 1) {
            updateVideoComment(userID, videoID, inputComment);
            //updateUserComment(userID, videoID, inputComment);
        } else {
           tester.homepage();
        }
        video.videoFunction();

    }


    //insert comment into field comment in table of video_details
    private static void updateVideoComment(int userID, int videoID, String inputComment) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("INSERT INTO video_comments(user_id, video_id, comment, username) VALUES('" + userID + "','" + videoID + "','" + inputComment+ "','" +user_name +"')"
                        + "ON DUPLICATE KEY UPDATE video_id='" + videoID + "', comment='" + inputComment + "', username='"+user_name+"'")) {
            posted.executeUpdate();
            System.out.println("Successfully insert video_comments");
        } catch (SQLException e) {
            System.out.println("updateVideoComment error");
        }
        System.out.println("Comment Posted.");
    }

        public static void channelInfo(String name){
         connection a = new connection();
       
        String sql2="SELECT `user_ID`FROM `user` WHERE `username`= '"+name+"'";
    
         try {
            pS = a.getConnection().prepareStatement(sql2);
            result = pS.executeQuery();
            if (result.next()) {
                //exist
                video.setChannelID(result.getInt("user_ID"));
            } else {
                // nope
                System.out.println("channel does not exist");
            }
        } catch (SQLException ex) {
            System.out.println("channel info 1 error");
        }
         
          String sql="SELECT `subscribers` FROM `channel_subscription` WHERE `channel_id`= '"+video.getChannelID()+"'";
          try {
            pS = a.getConnection().prepareStatement(sql);
            result = pS.executeQuery();
            if (result.next()) {
                //exist
                subscribersCount=result.getInt("subscribers");
            } else {
                // nope
                System.out.println("channel does not exist");
            }
        } catch (SQLException ex) {
            System.out.println("display subs count error");
        }
          
          System.out.println("Subscriber: "+subscribersCount);
    }
}
