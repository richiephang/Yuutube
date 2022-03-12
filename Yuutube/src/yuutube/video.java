package yuutube;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class video {

    private static String videoChoose;
    private static String channelChoose;
    private static int videoID;
    private static int channelID;
    private static String file_path;
    private static boolean upload_status = false;
    private static PreparedStatement pS;
    private static ResultSet result;
    private static int liked = 0;
    private static int disliked = 0;
    private static String[] channelVid;

    //method to search keyword
    public static void search() {
        Scanner s = new Scanner(System.in);

        System.out.println("\nPress 1 to search video, press 2 to search channel");
        int searchStatus = s.nextInt();

        System.out.print("Enter keyword: ");
        //buffer
        s.nextLine();
        String keyword = s.nextLine();
        //initialize variables  
        String[] storedValue = null;
        String[] matches = null;
        int countMatch = 0;

        if (searchStatus == 1) {
            int count = 0, c = 0;
            //find array size
            try {
                connection a = new connection();
                String searchVideo = "SELECT * FROM `video`";
                pS = a.getConnection().prepareStatement(searchVideo);
                result = pS.executeQuery();

                while (result.next()) {
                    count++;
                }
                a.getConnection().close();

            } catch (SQLException ex) {
                System.out.println("Connection failed");
            }
            //store videoName to array
            storedValue = new String[count];
            try {
                connection a = new connection();

                String searchVideo = "SELECT * FROM `video`";
                pS = a.getConnection().prepareStatement(searchVideo);
                result = pS.executeQuery();
                while (result.next()) {
                    storedValue[c] = result.getString("videoname");
                    c++;
                }
                a.getConnection().close();
            } catch (SQLException ex) {
                System.out.println("Connection error");
            }

        } else if (searchStatus == 2) {
            int count = 0, c = 0;
            //find array size
            try {
                connection a = new connection();

                String searchVideo = "SELECT * FROM `user`";
                pS = a.getConnection().prepareStatement(searchVideo);
                result = pS.executeQuery();

                while (result.next()) {
                    count++;
                }
                a.getConnection().close();

            } catch (SQLException ex) {
                System.out.println("array size channel error");
            }
            //store channelName to array
            storedValue = new String[count];
            try {
                connection a = new connection();

                String searchVideo = "SELECT * FROM `user`";
                pS = a.getConnection().prepareStatement(searchVideo);
                result = pS.executeQuery();
                while (result.next()) {
                    storedValue[c] = result.getString("username");
                    c++;
                }
                a.getConnection().close();
            } catch (SQLException ex) {
                System.out.println("channel storing error");
            }
        }
        //find number of matches
        for (int i = 0; i < storedValue.length; i++) {
            if (storedValue[i].toLowerCase().contains(keyword.toLowerCase())) {
                countMatch++;
            }
        }

        matches = new String[countMatch];
        //Store matched into array 
        for (int i = 0, j = 0; i < storedValue.length; i++, j++) {
            if (storedValue[i].toLowerCase().contains(keyword.toLowerCase())) {
                matches[j] = storedValue[i];
            } else {
                j--;
            }
        }

        //matching score
        double[] matchingScore = new double[matches.length];
        for (int i = 0; i < matches.length; i++) {
            matchingScore[i] = matchScore(keyword, matches[i]);

        }
        //sorting    
        for (int j = 0; j < matchingScore.length - 1; j++) {
            for (int k = 0; k < matchingScore.length - 1 - j; k++) {
                if (matchingScore[k] < matchingScore[k + 1]) {
                    double temp2 = matchingScore[k];
                    String temp = matches[k];

                    matchingScore[k] = matchingScore[k + 1];
                    matches[k] = matches[k + 1];

                    matchingScore[k + 1] = temp2;
                    matches[k + 1] = temp;
                }
            }
        }
        
       if(matches.length==0){
           System.out.println("No results found, please try another keyword.");
           System.out.println("");
           tester.homepage();
       }  
        
        System.out.println("Results: ");
        for (int i = 0; i < matches.length; i++) {
            System.out.println(i + ") " + matches[i]);
        }

        if (searchStatus == 1) {
            //search result

            System.out.print("Choose a video: ");
            int videoChoice = s.nextInt();
            videoChoose = matches[videoChoice];      //storing video name chosen
            videoInfo(videoChoose);
            videoFunction();

        } else if (searchStatus == 2) {
            //channel result 
            System.out.print("Choose a channel: ");
            int channelChoice = s.nextInt();
            channelChoose = matches[channelChoice];      //storing channel name chosen
            System.out.println("\nWelcome to channel of " + channelChoose);
            user.channelInfo(channelChoose);
            displayVideo(channelChoose);
            System.out.println("\nWhat do you wish to do?");
            System.out.println("1. Subscription for the channel");
            System.out.println("2. Choose a video");
            System.out.print("Please enter you choice: ");
            int i = s.nextInt();
            switch (i) {
                case 1:
                    if (user.isLogin_status() == true) {
                        try {
                            user.userSubscription(user.getID(), channelID);
                        } catch (Exception ex) {
                            Logger.getLogger(video.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        System.out.println("Failed to subsribe");
                        System.out.println("You haven't log in the account");
                        System.out.println("Please log in");
                        System.out.println();
                        tester.homepage();
                    }
                    break;
                case 2:
                    System.out.print("Enter your video choice: ");
                    int x = s.nextInt();
                    videoInfo(channelVid[x]);
                    videoFunction();
                    break;
                default:
                    System.out.println("Invalid Choice");
                    tester.homepage();
            }
        }

    }

    //Levenshtein Distance (searching algo)
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    //method to calc similarity score
    public static double matchScore(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
            /* both strings are zero length */ }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    //method to display all video info

    public static void videoInfo(String videoname) {
        try {
            connection a = new connection();

            String videoinfo = "SELECT * FROM `video` WHERE `videoname`= ?";

            pS = a.getConnection().prepareStatement(videoinfo);

            pS.setString(1, videoname);
            result = pS.executeQuery();
            if (result.next()) {
                //exist
                //store filepath
                file_path = result.getString("filepath");
                videoID = result.getInt("video_ID");
                channelID = result.getInt("channel_id");
                System.out.println("\nVideo name : " + videoname);
                System.out.println("Uploader : " + result.getString("username"));
                System.out.println("Likes : " + result.getInt("likes"));
                System.out.println("Dislikes : " + result.getInt("dislike"));
                System.out.println("Views : " + result.getInt("view"));
                System.out.println("***Comments section***");
                displayComment(videoID);

            } else {
                // nope
                System.out.println("Video not found");
            }

        } catch (SQLException ex) {
            System.out.println("connection error");
        }
    }
 //method to display comment

    public static void displayComment(int videoID) {
        try {
            connection a = new connection();

            String comment = "SELECT * FROM `video_comments` WHERE `video_ID`= '" + videoID + "'";

            pS = a.getConnection().prepareStatement(comment);

            //  pS.setString(4, videoname);
            result = pS.executeQuery();
            while (result.next()) {
                //exist
                System.out.println(result.getString("username") + " : " + result.getString("comment"));

            }

        } catch (SQLException ex) {
            System.out.println("connection error");
        }
    }

    //method to delete video record in MySql
    public static void deleteVideo(String videoname) {
        try {
            connection a = new connection();

            String del = "delete FROM `video` WHERE `videoname`= ?";
            pS = a.getConnection().prepareStatement(del);

            pS.setString(1, videoname);

            pS.executeUpdate();
            System.out.println("Video deleted");

        } catch (SQLException ex) {
            System.out.println("Delete error");
        }
    }

      //method to play video
    public static void playVideo(String fpath) {
      //  String link = "file:///" + fpath;

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(fpath));

                connection a = new connection();
                String viewAdd = "UPDATE video SET view =  view+1 WHERE filepath = ?"; //view+=1
                pS = a.getConnection().prepareStatement(viewAdd);

                pS.setString(1, fpath);
                pS.executeUpdate();

            } catch (URISyntaxException | IOException ex) {
                System.out.println("Failed to play video.");
            } catch (SQLException ex) {
                System.out.println("update view error");
            }
        }
        videoFunction();
    }

    //method to upload video
    public static void uploadVideo() {
        Scanner s = new Scanner(System.in);
        System.out.println("1)Upload video file.");
        System.out.println("2)Upload video link.");
        System.out.print("Enter your choice: ");
        int uploadChoice = s.nextInt();
        upload_status = true;
        connection a = new connection();
        try {
            String upload = "INSERT INTO video (username,likes,filepath,videoname,dislike,view, channel_id) VALUES(?, ?, ?, ?, ?, ?, ?)";
            pS = a.getConnection().prepareStatement(upload);
            s.nextLine();
            System.out.println("Enter video name: ");
            String videoName = s.nextLine();
            String filepath;
            if (uploadChoice == 1) {
                System.out.println("Enter video's file path : ");
                System.out.println("eg: C:/Users/Videos/sample.mp4");
                filepath = "file:///" +s.nextLine();
            }
            else {
                System.out.println("Enter video's link : ");
                filepath = s.nextLine();
            }
                
            pS.setString(1, user.getUser_name());
            pS.setInt(2, 0);
            pS.setString(3, filepath);
            pS.setString(4, videoName);
            pS.setInt(5, 0);
            pS.setInt(6, 0);
            pS.setInt(7, user.getID());
            pS.executeUpdate();

            System.out.println("Video uploaded");
        } catch (SQLException ex) {
            System.out.println("upload video error");
                }
    }
    public static void setChannelID(int channelID) {
        video.channelID = channelID;
    }

    public static int getChannelID() {
        return channelID;
    }
    
    public static boolean isUpload_status() {
        return upload_status;
    }

    //method to like or dislike
    public static void userLikeDislike(int userID, int videoID) throws Exception {
        Scanner input = new Scanner(System.in);
        //prompt user input
        System.out.println("1. like\n2. dislike\n3. cancle/return");
        int userInput = input.nextInt();

        switch (userInput) {
            //like
            case 1:

                if (checkLiked(userID, videoID) == 0 && checkDisliked(userID, videoID) == 0) { // user never like nor dislike the video before
                    updateLikeDislike(userID, videoID, 1, 0);
                    increaseLikeCounts(videoID);
                } else if (checkLiked(userID, videoID) == 1 && checkDisliked(userID, videoID) == 0) {   //user liked the video before
                    System.out.println("You,ve liked this video, do you wish to unlike?\n0. NO\n1. YES");
                    int unlike = input.nextInt();
                    if (unlike == 1) {
                        updateLikeDislike(userID, videoID, 0, 0);
                        decreaseLikeCounts(videoID);
                    }
                } else if (checkLiked(userID, videoID) == 0 && checkDisliked(userID, videoID) == 1) {   //user disliked the video before
                    updateLikeDislike(userID, videoID, 1, 0);
                    increaseLikeCounts(videoID);
                    decreaseDislikeCounts(videoID);
                } else 
                {
                    tester.homepage();
                    break;
                }

                break;
            // dislike
            case 2:

                if (checkLiked(userID, videoID) == 0 && checkDisliked(userID, videoID) == 0) { // user never like nor dislike the video before
                    updateLikeDislike(userID, videoID, 0, 1);
                    increaseDislikeCounts(videoID);
                } else if (checkLiked(userID, videoID) == 1 && checkDisliked(userID, videoID) == 0) {   //user liked the video before
                    updateLikeDislike(userID, videoID, 0, 1);
                    increaseDislikeCounts(videoID);
                    decreaseLikeCounts(videoID);
                } else if (checkLiked(userID, videoID) == 0 && checkDisliked(userID, videoID) == 1) {   //user disliked the video before
                    System.out.println("You,ve disliked this video, do you wish to undislike?\n0. NO\n1. YES");
                    int undislike = input.nextInt();
                    if (undislike == 1) {
                        updateLikeDislike(userID, videoID, 0, 0);
                        decreaseDislikeCounts(videoID);
                    }
                } else 
                {
                    tester.homepage();
                    break;
                }

                break;
            // return/cancle
            case 3:
                tester.homepage();
                break;
            default:
                tester.homepage();
                break;
        }
        videoFunction();
    }

    //checked whether user have liked or disliked the video
    private static int checkLiked(int userID, int videoID) throws SQLException {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("SELECT `video_id`, `liked` FROM `user_history` WHERE user_id = '" + userID + "' AND video_id = '" + videoID + "'");
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                liked = rs.getInt("liked");
            }
        } catch (SQLException e) {
            System.out.println("checkLiked error");
        }
        return liked;
    }

    private static int checkDisliked(int userID, int videoID) throws SQLException {
        connection a = new connection();
        try (PreparedStatement statement
                = a.getConnection().prepareStatement("Select video_id, disliked FROM user_history WHERE user_id = '" + userID + "' AND video_id = '" + videoID + "'");
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                disliked = result.getInt("disliked");
            }
        } catch (SQLException e) {
            System.out.println("checkDisliked error");
        }
        return disliked;
    }

    //increase or decrease number of likes or dislikes: 
    private static void increaseLikeCounts(int videoID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE video SET likes =  likes+1 WHERE video_ID = '" + videoID + "'")) {
            posted.executeUpdate();
            System.out.println("Update video successful");
        } catch (SQLException e) {
            System.out.println("increaseLikeCounts error");
        }

    }

    private static void decreaseLikeCounts(int videoID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE video SET likes =  likes-1 WHERE video_ID = '" + videoID + "'")) {
            posted.executeUpdate();
            System.out.println("Update video successful");
        } catch (SQLException e) {
            System.out.println("decreaseLikeCounts error");
        }

    }

    private static void increaseDislikeCounts(int videoID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE video SET dislike =  dislike+1 WHERE video_ID = '" + videoID + "'")) {
            posted.executeUpdate();
            System.out.println("Successfully Updated video");
        } catch (SQLException e) {
            System.out.println("increaseDislikeCounts ERROR");
        }

    }

    private static void decreaseDislikeCounts(int videoID) throws Exception {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("UPDATE video SET dislike =  dislike-1 WHERE video_ID = '" + videoID + "'")) {
            posted.executeUpdate();
            System.out.println("Successfully updated video");
        } catch (SQLException e) {
            System.out.println("decreaseDislikeCounts error");
        }

    }

    //update the user_history database
    private static void updateLikeDislike(int userID, int videoID, int likeBoolean, int dislikeBoolean) throws SQLException {
        connection a = new connection();
        try (PreparedStatement posted
                = a.getConnection().prepareStatement("INSERT INTO `user_history`(`user_id`, `video_id`, `liked`, `disliked`) VALUES ('"+userID+"','"+videoID+"','"+likeBoolean+"','"+dislikeBoolean+"')"
                        + "ON DUPLICATE KEY UPDATE `liked`='"+likeBoolean+"',`disliked`='"+dislikeBoolean+"'")) {
            posted.executeUpdate();
            System.out.println("Successfully updated user_history");
        } catch (SQLException e) {
            System.out.println("updateLikeDislike error");
        }
    }
    
    public static void myVideo(String userName){
        Scanner s = new Scanner(System.in);
        String[] myvid=null;
            //find array size
            int count =0, c =0;
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video` WHERE username = '" + userName + "'";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();

            while (result.next()) {
                count++;
            }
            a.getConnection().close();

        } catch (SQLException ex) {
            System.out.println("myVideo error");
        }
        //store videoName to array
        myvid = new String[count];
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video` WHERE username = '" + userName + "'";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();
            while (result.next()) {
                myvid[c] = result.getString("videoname");
                c++;
            }
            a.getConnection().close();
        } catch (SQLException ex) {
            System.out.println("myvideodisplay error");
        }
        System.out.println("***Your videos***");
         System.out.println("Total videos: "+count);   
        for (int i = 0; i < myvid.length; i++) {
            System.out.println(i+") "+myvid[i]);
        }
        if (count==0)
        {
            System.out.println("You do not have any video");
            System.out.println();
            try {
                user.getUserManage(user.getID());
                } catch (Exception ex) {
                    Logger.getLogger(tester.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
        else
        {
            System.out.println("\nWhat you want to do next?");
            System.out.println("1. Delete your video");
            System.out.println("2. Display video info");
            System.out.println("3. Return to homepage");
            System.out.print("Your Choice: ");
            int userChoice = s.nextInt();
            switch (userChoice) {
            case 1:
                System.out.print("Please choose a video to delete: ");
                int videoChoice = s.nextInt();
                deleteVideo(myvid[videoChoice]);
                break;
            case 2:
                System.out.print("Please choose a video: ");
                int infoChoice = s.nextInt();
                videoInfo(myvid[infoChoice]);
                break;
            case 3:
                tester.homepage();
                break;
            default:
                tester.homepage();
                break;  
            }
            System.out.println();
            myVideo(userName);
        }
    }
    
    public static void trendingNow() {
        String[] storedValue = null;
        int[] viewList = {};
        int count = 0, c = 0;
        //find array size
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video`";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();

            while (result.next()) {
                count++;
            }
            a.getConnection().close();

        } catch (SQLException ex) {
            System.out.println("Connection failed");
        }
        //store videoName to array
        storedValue = new String[count];
        viewList = new int[count];
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video`";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();
            while (result.next()) {
                storedValue[c] = result.getString("videoname");
                viewList[c] = result.getInt("view");
                c++;
            }
            a.getConnection().close();
        } catch (SQLException ex) {
            System.out.println("Connection error");
        }

        for (int i = 0; i < viewList.length - 1; i++) {
            for (int j = 0; j < viewList.length - i - 1; j++) {
                if (viewList[j + 1] > viewList[j]) {
                    int hold = viewList[j];
                    viewList[j] = viewList[j + 1];
                    viewList[j + 1] = hold;
                    String temp = storedValue[j];
                    storedValue[j] = storedValue[j + 1];
                    storedValue[j + 1] = temp;

                }
            }
        }
        System.out.println("Trending video on Yuu-tube"); //the top line of table
          for (int i = 0; i < viewList.length; i++) {
            if (i < 5) {
                System.out.println("# " + (i + 1) + "\t" + storedValue[i]);
            }
        }

    }
    //method to display a channel's video
        public static void displayVideo(String channelName){
    
     Scanner s = new Scanner(System.in);
        
        //find array size
        int count = 0, c = 0;
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video` WHERE username = '" + channelName + "'";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();

            while (result.next()) {
                count++;
            }
            a.getConnection().close();

        } catch (SQLException ex) {
            System.out.println("display video array error");
        }
        //store videoName to array
        channelVid = new String[count];
        try {
            connection a = new connection();

            String searchVideo = "SELECT * FROM `video` WHERE username = '" + channelName + "'";
            pS = a.getConnection().prepareStatement(searchVideo);
            result = pS.executeQuery();
            while (result.next()) {
                channelVid[c] = result.getString("videoname");
                c++;
            }
            a.getConnection().close();
        } catch (SQLException ex) {
            System.out.println("channel videodisplay error");
        }
        System.out.println("***"+channelName+" videos***");
        System.out.println("Total videos: "+count);
        for (int i = 0; i < channelVid.length; i++) {
            System.out.println(i + ") " + channelVid[i]);
        }
    }
    public static void videoFunction(){
        Scanner s = new Scanner(System.in);
     System.out.println("\nWhat do you wish to do?");
        System.out.println("1. Watch the video");
        System.out.println("2. Comment on the video");
        System.out.println("3. Like/dislike the video");
        System.out.println("4. Subscription for the channel");
        System.out.println("5. Return to homepage");
        System.out.print("Please enter you choice: ");
        int i = s.nextInt();
        switch (i) {
            case 1:
                playVideo(file_path);
                
                break;
            case 2:
            {
                if (user.isLogin_status()==true)
                try {
                    user.userComment(user.getID(),videoID);
                } catch (Exception ex) {
                    Logger.getLogger(video.class.getName()).log(Level.SEVERE, null, ex);
                }
                else 
                {
                    System.out.println("Failed to comment ");
                    System.out.println("You haven't log in the account");
                    System.out.println("Please log in");
                    System.out.println();
                    tester.homepage();
                }
            }
                
                break;

            case 3:
                if (user.isLogin_status()==true) 
                {
                    try {
                        userLikeDislike (user.getID(),videoID);
                        } catch (Exception ex) {
                            Logger.getLogger(video.class.getName()).log(Level.SEVERE, null, ex);
                            }
                } 
                else 
                {
                    System.out.println("Failed to like/dislike the video");
                    System.out.println("You haven't log in the account");
                    System.out.println("Please log in");
                    System.out.println();
                    tester.homepage();

                }
                
                break;
            case 4: 
                    if (user.isLogin_status()==true) 
                    {
                       try {
                            user.userSubscription(user.getID(), channelID);
                        } catch (Exception ex) {
                            Logger.getLogger(video.class.getName()).log(Level.SEVERE, null, ex);
                            }  
                    }
                    else 
                    {
                        System.out.println("Failed to subsribe");
                        System.out.println("You haven't log in the account");
                        System.out.println("Please log in");
                        System.out.println();
                        tester.homepage();
                    }
                
                    break;
           case 5:
                 tester.homepage();
                 break;
            default:
                System.out.println("Invalid Choice");
                tester.homepage();
                break;
        }
    }

}
