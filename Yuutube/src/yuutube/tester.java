package yuutube;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class tester {

    private static PreparedStatement pS;
    private static ResultSet result;

    public static void main(String[] args) {
        System.out.println("Welcome to Yuu-tube!");
        
        video.trendingNow();
        System.out.println();
        homepage();

    }
    //method to display homepage
    public static void homepage()
    {
        clearConsole();
        if (user.isLogin_status()==false)
        {
            menu();
        }
        else
        {
            userMenu();
        }
    }

        public static void menu() {
        Scanner s = new Scanner(System.in);
        System.out.println("What do you wish to do now?");
        System.out.println("1. Log in ");
        System.out.println("2. Register");
        System.out.println("3. Search for video or channel");
        System.out.println("4. Exit the program");
        System.out.print("Please enter your choice (1-4): ");
        int choice = s.nextInt();
        switch (choice) {
            case 1:
                user.login();
                break;
            case 2:
                user.register();
                break;
            case 3:
                video.search();
                break;
            case 4:
                end();
                break;
            default:
                System.out.println("Invalid choice");
                System.out.println("Please enter a valid choice");
                homepage();
                break;
        }
        System.out.println();
        homepage();
    }
    
    public static void userMenu()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("\nWhat do you wish to do now?");
        System.out.println("1. Search for video or channel");
        System.out.println("2. Upload video");
        System.out.println("3. Manage you account");
        System.out.println("4. Exit the program");
        System.out.println("5. Log out");
        System.out.print("Please enter your choice (1-5): ");
        int choice = s.nextInt();
        switch (choice) {
            case 1:
                video.search();
                break;
            case 2:
                video.uploadVideo();
                break;
            case 3:
                  try {
                user.getUserManage(user.getID());
            } catch (Exception ex) {
                Logger.getLogger(tester.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
            case 4:
                end();
            case 5:
                user.logout();
                break;
            default:
                System.out.println("Invalid choice");
                System.out.println("Please enter a valid choice");
                homepage();
                break;
        }
        System.out.println();
        homepage();
    }
    
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {   
        //  Handle any exceptions.
        }
    }

    

    public static void end() {
        Scanner s = new Scanner(System.in);
        System.out.println("Are you sure you want to exit our program? (yes/no)");
        String dec = s.next();
        if (dec.equalsIgnoreCase("no")) {
            homepage();
            System.out.println();
        } else if (dec.equalsIgnoreCase("yes")) {
            System.out.println("We are very appreciate that you are using our program");
            String txt = "Thank you <3";
            for (int i = 0; i < txt.length(); i++) {
                System.out.printf("%c", txt.charAt(i));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println();
            System.exit(0);
        } else {
            System.out.println("Please enter a valid choice");
            System.out.println();
            end();
        }
    }
}
