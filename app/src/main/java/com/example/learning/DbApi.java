package com.example.learning;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.learning.fragments.Deck;
import com.example.learning.fragments.Friends;
import com.example.learning.fragments.ImageUtils;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class DbApi {
    private SQLiteDatabase db;

    public DbApi(SQLiteDatabase db) {
        this.db = db;
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
//        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    //update by Zongwei Li,2022/4/20
    public String getSignDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
//        System.out.println(formatter.format(date));
        return formatter.format(date);

    }
    public ArrayList<DeckEntity> getAllPublicDecks(int userID) {
        ArrayList<DeckEntity> allDecks = new ArrayList<>();
        ArrayList<FolderEntity> folders = queryFolder(userID);
        for (FolderEntity folder : folders) {
            ArrayList<DeckEntity> decks = queryDeck(folder.getFolderID(), userID);
            for (DeckEntity deck : decks) {
                if(deck.getPub() == 1) {
                    allDecks.add(deck);
                }
            }
        }
        return allDecks;
    }


    public static String randomName(int min, int max) {
        String name;
        char[] nameChar;

        int nameLength = (int) (Math.random() * (max - min + 1)) + min;
        nameChar = new char[nameLength];
        nameChar[0] = (char) (Math.random() * 26 + 65);
        for (int i = 1; i < nameLength; i++) {
            nameChar[i] = (char) (Math.random() * 26 + 97);
        }
        name = new String(nameChar);
        return name;
    }

    public ArrayList<Card> queryCard(int deckID, int folderID, int userID) {

        ArrayList<Card> cards = new ArrayList<>();

        Cursor fcursor = db.query("card", null, null, null, null, null, null);
        if (fcursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int uid = fcursor.getInt(fcursor.getColumnIndex("u_id"));
                @SuppressLint("Range") int folder_id = fcursor.getInt(fcursor.getColumnIndex("folder_id"));
                @SuppressLint("Range") int deck_id = fcursor.getInt(fcursor.getColumnIndex("deck_id"));
                if (userID == uid && folder_id == folderID && deck_id == deckID) {
                    @SuppressLint("Range") int fid = fcursor.getInt(fcursor.getColumnIndex("card_id"));
                    @SuppressLint("Range") String question = fcursor.getString(fcursor.getColumnIndex("card_question"));
                    @SuppressLint("Range") String answer = fcursor.getString(fcursor.getColumnIndex("card_answer"));
                    @SuppressLint("Range") String time = fcursor.getString(fcursor.getColumnIndex("time"));
                    @SuppressLint("Range") int level = fcursor.getInt(fcursor.getColumnIndex("level"));
                    Card card = new Card(question, answer, fid, level, time, folderID, userID, deckID);
                    cards.add(card);
                }
            } while (fcursor.moveToNext());
        }
        fcursor.close();
        return cards;
    }

    public ArrayList<DeckEntity> queryDeck(int folderID, int userID) {
        ArrayList<DeckEntity> deckEntities = new ArrayList<>();

        Cursor fcursor = db.query("deck", null, null, null, null, null, null);
        if (fcursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int uid = fcursor.getInt(fcursor.getColumnIndex("u_id"));
                @SuppressLint("Range") int folder_id = fcursor.getInt(fcursor.getColumnIndex("folder_id"));
                if (userID == uid && folder_id == folderID) {
                    @SuppressLint("Range") int fid = fcursor.getInt(fcursor.getColumnIndex("deck_id"));
                    @SuppressLint("Range") String name = fcursor.getString(fcursor.getColumnIndex("deck_name"));
                    @SuppressLint("Range") String description = fcursor.getString(fcursor.getColumnIndex("deck_description"));
                    @SuppressLint("Range") String time = fcursor.getString(fcursor.getColumnIndex("time"));
                    @SuppressLint("Range") int completion = fcursor.getInt(fcursor.getColumnIndex("completion"));
                    @SuppressLint("Range") int frequency = fcursor.getInt(fcursor.getColumnIndex("frequency"));
                    @SuppressLint("Range") String dayOfWeek = fcursor.getString(fcursor.getColumnIndex("day_of_week"));
                    @SuppressLint("Range") int interval = fcursor.getInt(fcursor.getColumnIndex("interval"));
                    @SuppressLint("Range") String coverPath = fcursor.getString(fcursor.getColumnIndex("cover_path"));
                    @SuppressLint("Range") int pub = fcursor.getInt(fcursor.getColumnIndex("public"));
                    DeckEntity deckEntity = new DeckEntity(name, completion, description, time, frequency, dayOfWeek, interval, fid, userID, folderID, coverPath, pub);
                    deckEntities.add(deckEntity);
                }
            } while (fcursor.moveToNext());
        }
        fcursor.close();
        return deckEntities;
    }

    public ArrayList<FolderEntity> queryFolder(int userID) {
        ArrayList<FolderEntity> folders = new ArrayList<>();
        Cursor fcursor = db.query("folder", null, null, null, null, null, null);
        if (fcursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int uid = fcursor.getInt(fcursor.getColumnIndex("u_id"));

                if (userID == uid) {
                    @SuppressLint("Range") int fid = fcursor.getInt(fcursor.getColumnIndex("folder_id"));
                    @SuppressLint("Range") String name = fcursor.getString(fcursor.getColumnIndex("folder_name"));
                    @SuppressLint("Range") String description = fcursor.getString(fcursor.getColumnIndex("folder_description"));
                    @SuppressLint("Range") String time = fcursor.getString(fcursor.getColumnIndex("time"));
                    FolderEntity folder = new FolderEntity(name, description, fid, time, userID);
                    folders.add(folder);
                }
            } while (fcursor.moveToNext());
        }
        fcursor.close();
        return folders;
    }

    public long insertFolder(String folderName, String folderDescription, int userID) {
        long id = -1;
        int[] arrary = new int[1000];
        boolean justice = false;
        int count = 0;

        Cursor check_cursor = db.query("user", null, null, null, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("u_id"));
                arrary[count] = pid;
                count = count + 1;
            } while (check_cursor.moveToNext());

        }
        for (int i = 0; i < arrary.length; i++) {
            if (arrary[i] == userID) {
                justice = true;
            }
        }


        if (justice == true) {
            ContentValues values2 = new ContentValues();
            values2.put("folder_name", folderName);
            values2.put("folder_description", folderDescription);
            values2.put("u_id", userID);
            String time = getDate();
            values2.put("time", time);
            id = db.insert("folder", null, values2);
            System.out.println("create folder: " + folderName + "with description: " + folderDescription + "for user:" + userID);

        } else {
            System.out.println("not create folder: " + folderName + "with description: " + folderDescription + "for user:" + userID);
        }
        return id;
    }

    public long insertUserFull(String username, String email, String password, String profilePicture) {
        long id = -1;
        if (username != null) {
            ContentValues values1 = new ContentValues();
            values1.put("name", username);
            values1.put("email", email);
            values1.put("password", password);
            values1.put("profile_picture", profilePicture);
            id = db.insert("user", null, values1);
            System.out.println("create user: " + username);
        } else {
            System.out.println("not create user: " + username);
        }
        // generate default folder for user
        long folderId = insertFolder("default folder", "description for default folder", (int)id);
        int frequency = -1;
        String dayOfWeek = "";
        int interval = 0;
        int pub = 1;
        String path = "/storage/emulated/0/Android/data/com.example.learning/files/deckCovers/default1.jpg";
        long deckId = insertDeck("default deck", "description for default deck", 0, frequency, dayOfWeek, interval, (int)folderId, (int)id, path, 1);
        long cardId = insertCard("default card", "1 + 1 = ?", "2", 0, (int) deckId, (int) folderId, (int)id);
        return id;

    }

    public long insertDeck(String deckName, String deckDescription, int completion, int frequency, String dayofWeek, int interval, int folderID, int userID, String cover, int pub) {
        int[] arrary = new int[1000];
        boolean justice = false;
        int count = 0;
        long id = -1;
        Cursor check_cursor = db.query("folder", null, null, null, null, null, null);

        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("folder_id"));
                arrary[count] = pid;
                count = count + 1;
            } while (check_cursor.moveToNext());

        }
        for (int i = 0; i < arrary.length; i++) {
            if (arrary[i] == folderID) {
                justice = true;
            }
        }

        if (justice == true) {
            ContentValues values2 = new ContentValues();
            values2.put("deck_name", deckName);
            values2.put("deck_description", deckDescription);
            values2.put("folder_id", folderID);
            values2.put("completion", completion);
            values2.put("u_id", userID);
            values2.put("frequency", frequency);
            values2.put("day_of_week", dayofWeek);
            values2.put("interval", interval);
            values2.put("public", pub);
            values2.put("cover_path", cover);
            String time = getDate();
            values2.put("time", time);
            id = db.insert("deck", null, values2);
            System.out.println("create folder: " + deckName + "with description: " + deckDescription + "for folder: " + folderID);

        } else {
            System.out.println("not create folder: " + deckName + "with description: " + deckDescription + "for folder: " + folderID);
        }
        return id;
    }

    public int queryUser(String userName, String email, String password) {
        String[] arrary = new String[1000];
        String[] passwords = new String[1000];
        String[] userNames = new String[1000];
        int[] userIDs = new int[1000];
        int count = 0;
        Cursor check_cursor = db.query("user", null, null, null, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email1 = check_cursor.getString(check_cursor.getColumnIndex("email"));
                System.out.println("query user" + email1);
                @SuppressLint("Range") String password1 = check_cursor.getString(check_cursor.getColumnIndex("password"));
                @SuppressLint("Range") String userName1 = check_cursor.getString(check_cursor.getColumnIndex("name"));
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("u_id"));
                arrary[count] = email1;
                passwords[count] = password1;
                userNames[count] = userName1;
                userIDs[count] = pid;
                count = count + 1;
            } while (check_cursor.moveToNext());
        }
        System.out.println(email);
        for (int i = 0; i < count; i++) {
            System.out.println(arrary[i]);
            if (arrary[i].equals(email)) {
                if (passwords[i].equals(password) && userNames[i].equals(userName)) {
                    return userIDs[i];
                } else {
                    return -1;
                }
            }
        }
        return -2;
    }

    public String queryUserName(int uid) {
        Cursor check_cursor = db.query("user", null, null, null, null, null, null);

        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("u_id"));
                if (pid == uid) {
                    @SuppressLint("Range") String name = check_cursor.getString(check_cursor.getColumnIndex("name"));
                    return name;
                }

            } while (check_cursor.moveToNext());

        }
        return "";
    }

    public String queryUserProfileURL(int userId) {
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = cursor.getInt(cursor.getColumnIndex("u_id"));
                if (pid == userId) {
                    @SuppressLint("Range") String userProfileURL = cursor.getString(cursor.getColumnIndex("profile_picture"));
                    return userProfileURL;
                }
            } while (cursor.moveToNext());
        }
        return "";
    }

    public long insertCard(String cardName, String cardQuestion, String cardAnswer, int hardness, int deckID, int folderID, int userID) {
        int[] arrary = new int[1000];
        boolean justice = false;
        int count = 0;
        long id = -1;
        Cursor check_cursor = db.query("deck", null, null, null, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("deck_id"));
                arrary[count] = pid;
                count = count + 1;
            } while (check_cursor.moveToNext());

        }
        for (int i = 0; i < arrary.length; i++) {
            if (arrary[i] == deckID) {
                justice = true;
            }
        }


        if (justice == true) {
            ContentValues values2 = new ContentValues();
            // values2.put("card_name", cardName);
            values2.put("card_question", cardQuestion);
            values2.put("card_answer", cardAnswer);
            values2.put("deck_id", deckID);
            values2.put("folder_id", folderID);
            values2.put("level", hardness);
            values2.put("u_id", userID);
            values2.put("folder_id", folderID);
            String time = getDate();
            values2.put("time", time);

            id = db.insert("card", null, values2);
            System.out.println("create folder: " + cardName + "with answer: " + cardAnswer + "for deck:" + deckID);

        } else {
            System.out.println("not create folder: " + cardName + "with description: " + cardAnswer + "for deck:" + deckID);
        }
        return id;
    }
    public long insertSign(int u_id,String status){
        int[] arrary = new int[1000];
        boolean justice = false;
        int count = 0;
        long id = -1;
        String date = getSignDate();
        Cursor check_cursor = db.query("user", null, null, null, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = check_cursor.getInt(check_cursor.getColumnIndex("u_id"));
                arrary[count] = pid;
                count = count + 1;
            } while (check_cursor.moveToNext());

        }
        for (int i = 0; i < arrary.length; i++) {
            if (arrary[i] == u_id) {
                justice = true;
            }
        }

        if (justice == true) {
            ContentValues values2 = new ContentValues();
            // values2.put("card_name", cardName);

            values2.put("u_id", u_id);
            values2.put("date", date);
            values2.put("status", status);

            id = db.insert("sign", null, values2);
            System.out.println("User: " +u_id + " Sign in: " + date);


        } else {
            System.out.println("User: " +u_id + " No Sign in: " + date);
        }
        return id;

    }

    public boolean checkSign(int u_id,String checkdate){
        String user_id=Integer.toString(u_id);
        Cursor check_cursor = db.query("sign", null, "u_id =? and date=?", new String[]{user_id, checkdate}, null, null, null);
        if (check_cursor.getCount()==0){
            return false;
        }
        return true;

    }
    public int getPresentdays(int u_id){
        String user_id=Integer.toString(u_id);
        Cursor check_cursor = db.query("sign", null, "u_id =?", new String[]{user_id}, null, null, null);
        return check_cursor.getCount();
    }
    public int getDonenumber(int u_id){
        String user_id=Integer.toString(u_id);
        Cursor check_cursor = db.query("deck", null, "u_id =? and completion=?", new String[]{user_id,"1"}, null, null, null);
        return check_cursor.getCount();

    }
    public int getOngoingnumber(int u_id){
        String user_id=Integer.toString(u_id);
        Cursor check_cursor = db.query("deck", null, "u_id =? and completion=?", new String[]{user_id,"0"}, null, null, null);
        return check_cursor.getCount();

    }
    public int getDecknumber(int u_id){
        String user_id=Integer.toString(u_id);
        Cursor check_cursor = db.query("deck", null, "u_id =?", new String[]{user_id}, null, null, null);
        return check_cursor.getCount();

    }
    public ArrayList<String> getUserInfo(int userID) {
        String user_id = Integer.toString(userID);

        ArrayList<String> arrary = new ArrayList<>();

        int count = 0;
        Cursor check_cursor = db.query("user", null, "u_id =?", new String[]{user_id}, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email = check_cursor.getString(check_cursor.getColumnIndex("email"));
                @SuppressLint("Range") String phone = check_cursor.getString(check_cursor.getColumnIndex("phone_number"));

                @SuppressLint("Range") String password = check_cursor.getString(check_cursor.getColumnIndex("password"));
                @SuppressLint("Range") String userName = check_cursor.getString(check_cursor.getColumnIndex("name"));
                arrary.add(email);
                arrary.add(userName);
                arrary.add(phone);
                arrary.add(password);

            } while (check_cursor.moveToNext());



        }
        return arrary;
    }
    public void UpdateUserIfo(int userID,String user_name,String phone_number,String password){
        String user_id=Integer.toString(userID);
        ContentValues values = new ContentValues();
        values.put("name",user_name);
        values.put("phone_number",phone_number);
        values.put("password",password);
        db.update("user",values,"u_id = ?",new String[]{user_id});
    }
    public ArrayList<Integer> getUserCardLevel(int userID) {
        String user_id = Integer.toString(userID);
        ArrayList<Integer> arrary = new ArrayList<>();
        int count = 0;
        int easy=0;
        int hard=0;
        int forgot=0;

        Cursor check_cursor = db.query("card", null, "u_id =?", new String[]{user_id}, null, null, null);
        if (check_cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int level = check_cursor.getInt(check_cursor.getColumnIndex("level"));
                if (level == 0){
                    easy=easy+1;
                }
                if (level == 1){
                    hard=hard+1;
                }
                if (level==2){
                    forgot=forgot+1;
                }



            } while (check_cursor.moveToNext());




        }
        arrary.add(easy);
        arrary.add(hard);
        arrary.add(forgot);
        return arrary;

    }
    public void updateUserImg(int userID,String path){
        String user_id=Integer.toString(userID);
        ContentValues values = new ContentValues();
        values.put("profile_picture",path);
        db.update("user",values,"u_id = ?",new String[]{user_id});
    }


    // get all cards given a user
    public ArrayList<Row> getAllCards(int userID) {
        ArrayList<Row> allCards = new ArrayList<>();
        ArrayList<FolderEntity> folders = queryFolder(userID);
        for (FolderEntity folder : folders) {
            ArrayList<DeckEntity> decks = queryDeck(folder.getFolderID(), userID);
            for (DeckEntity deck : decks) {
                ArrayList<Card> cards = queryCard(deck.getDeckID(), folder.getFolderID(), userID);
                for (Card card : cards) {
                    Row row = new Row(deck, card);
                    allCards.add(row);
                }
            }
        }
        return allCards;
    }

    // get all decks given a user
    public ArrayList<DeckEntity> getAllDecks(int userID) {
        ArrayList<DeckEntity> allDecks = new ArrayList<>();
        ArrayList<FolderEntity> folders = queryFolder(userID);
        for (FolderEntity folder : folders) {
            ArrayList<DeckEntity> decks = queryDeck(folder.getFolderID(), userID);
            for (DeckEntity deck : decks) {
                allDecks.add(deck);
            }
        }
        return allDecks;
    }

    // get all public decks for a specific user
    public ArrayList<DeckEntity> getPublicDecks(int userId) {
        ArrayList<DeckEntity> publicDecks = new ArrayList<>();
        ArrayList<DeckEntity> decks = getAllDecks(userId);
        for (DeckEntity deck : decks) {
            if (deck.getPub() == 1) {
                publicDecks.add(deck);
            }
        }
        return publicDecks;
    }

    // get all cards inside a specific deck
    public ArrayList<Row> getCardsFromDeck(DeckEntity deck) {
        ArrayList<Row> cardsFromDeck = new ArrayList<>();
        ArrayList<Card> cards = queryCard(deck.getDeckID(), deck.getFolderId(), deck.getUserId());
        for (Card card : cards) {
            Row row = new Row(deck, card);
            cardsFromDeck.add(row);
        }
        return cardsFromDeck;
    }

    // get all the decks that need to be reminded for today
    // input: today's week day number
    public ArrayList<DeckEntity> getDecksForReminder(int userID, String todayDayOfWeek) {
        // define list of decks as final output
        ArrayList<DeckEntity> decksForReminder = new ArrayList<>();

        /* filter principles:
        - scenario 1: if frequency = 0 or 1
            - include if dayOfWeek includes the value of todayDayOfWeek (i.e. today's day of week)
        - scenario 2: if frequency = 2 (monthly)
            - include if today minus the deck creation date is divisible by interval
         */
        ArrayList<DeckEntity> allDecks = getAllDecks(userID);
        for (DeckEntity deck : allDecks) { // filter the decks by frequency level
            // scenario 1: daily or weekly
            if (deck.getFrequency() == 0 | deck.getFrequency() == 1) {
                ArrayList<String> dayOfWeek = new ArrayList<>( // split dayOfWeek by ";" separator
                        Arrays.asList(deck.getDayOfWeek().split(";")));
                if (dayOfWeek.contains(todayDayOfWeek)) {
                    decksForReminder.add(deck);
                }
            }

            /* scenario 2: monthly
            using the "deck" database, we check if the difference between today and the date the deck was created is divisible by the interval value
            if yes, then we add that deck to today's deck list for the user to study
             */
            else if (deck.getFrequency() == 2) {
                String creationDateString = deck.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    Date creationDate = formatter.parse(creationDateString);
                    Date todayDate = new Date(System.currentTimeMillis());
                    int diff = Days.daysBetween(new DateTime(creationDate).toLocalDate(), new DateTime(todayDate).toLocalDate()).getDays();
                    if (diff % deck.getInterval() == 0) { // if diff is divisible by interval value, then we add this deck to today's study list
                        decksForReminder.add(deck);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return decksForReminder;
    }


    public ArrayList<Row> getCardsForReminder(int userID, String todayDayOfWeek) {
        ArrayList<Row> cardsForReminder = new ArrayList<>();
        ArrayList<DeckEntity> decksForReminder = getDecksForReminder(userID, todayDayOfWeek);
        for (DeckEntity deck : decksForReminder) {
            ArrayList<Card> cards = queryCard(deck.getDeckID(), deck.getFolderId(), userID);
            for (Card card : cards) {
                Row row = new Row(deck, card);
                cardsForReminder.add(row);
            }
        }
        return cardsForReminder;
    }

    public String getDayOfWeek(int intDayOfWeek) {
        String dayOfWeek = "";
        switch (intDayOfWeek) {
            case 1:
                dayOfWeek = "Sunday";
                break;
            case 2:
                dayOfWeek = "Monday";
                break;
            case 3:
                dayOfWeek = "Tuesday";
                break;
            case 4:
                dayOfWeek = "Wednesday";
                break;
            case 5:
                dayOfWeek = "Thursday";
                break;
            case 6:
                dayOfWeek = "Friday";
                break;
            case 7:
                dayOfWeek = "Saturday";
                break;
        }
        return dayOfWeek;
    }

    // functions for friends
    public ArrayList<FriendEntity> queryFriends(int userId) {
        ArrayList<FriendEntity> friends = new ArrayList<>();
        Cursor cursor = db.query("friend", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int uid = cursor.getInt(cursor.getColumnIndex("u_id"));
                if (userId == uid) {
                    @SuppressLint("Range") int friendId = cursor.getInt(cursor.getColumnIndex("friend_id"));
                    @SuppressLint("Range") String friendStatusString = cursor.getString(cursor.getColumnIndex("status"));
                    String friendName = queryUserName(friendId);
                    String friendPicture = queryUserProfileURL(friendId);
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));

                    FriendEntity friend = new FriendEntity(friendId, friendStatusString, friendName, friendPicture, date);
                    friends.add(friend);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

    public long insertFriend(int userId, int friendId, FriendStatus status) {
        long id = -1;
        int[] array = new int[1000];
        boolean justice = false;
        int count = 0;

        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int pid = cursor.getInt(cursor.getColumnIndex("u_id"));
                array[count] = pid;
                count = count + 1;
            } while (cursor.moveToNext());
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == userId) {
                justice = true;
            }
        }

        if (justice == true) {
            ContentValues values = new ContentValues();
            values.put("u_id", userId);
            values.put("friend_id", friendId);
            String date = getDate();
            values.put("date", date);
            values.put("status", status.name());
            id = db.insert("friend", null, values);
            System.out.println("user " + userId + " created friend connection with another user " + friendId);
        } else {
            System.out.println("no friend connection created.");
        }
        return id;
    }

    public ArrayList<FriendEntity> getConfirmedFriends(int userId) {
        ArrayList<FriendEntity> friends = queryFriends(userId);
        ArrayList<FriendEntity> confirmedFriends = new ArrayList<>();
        for (FriendEntity friend : friends) {
            if (friend.getFriendStatus() == FriendStatus.FRIEND) {
                confirmedFriends.add(friend);
            }
        }
        return confirmedFriends;
    }

    public ArrayList<FriendEntity> getIncomingFriendRequests(int userId) {
        ArrayList<FriendEntity> friends = queryFriends(userId);
        ArrayList<FriendEntity> incomingFriendRequests = new ArrayList<>();
        for (FriendEntity friend : friends) {
            if (friend.getFriendStatus() == FriendStatus.FRIEND_REQUESTED) {
                incomingFriendRequests.add(friend);
            }
        }
        return incomingFriendRequests;
    }

    public void updateFriendStatus(int userId, FriendEntity friend, FriendStatus newStatus) {
        String query = "UPDATE friend SET status = '" + newStatus.name() +
                "', date = '" + getDate() +
                "' WHERE u_id = " + userId +
                " AND friend_id = " + friend.getFriendId();
        db.execSQL(query);
    }

    public void deleteFriend(int userId, int friendId) {
        String query = "DELETE FROM friend WHERE u_id = " + userId +
                " AND friend_id = " + friendId;
        db.execSQL(query);
    }

    public int queryUserByEmail(String email) { // returns userId
        String query = "SELECT u_id FROM user WHERE email = '" + email + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("u_id"));
                return userId;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return -1;
    }
    public void deleteFolder(int userId, int folderId){
        String queryDeck = "DELETE FROM deck WHERE u_id = " + userId +
                " AND folder_id = " + folderId;
        String query = "DELETE FROM folder WHERE u_id = " + userId +
                " AND folder_id = " + folderId;
        db.execSQL(queryDeck);
        db.execSQL(query);

    }
    public void deleteDeck(int userId, int folderId, int deckId){
        String query = "DELETE FROM deck WHERE u_id = " + userId +
                " AND folder_id = " + folderId + " AND deck_id = " + deckId;
        db.execSQL(query);
    }

    // send friend request function: first check if friendship already exists in the 'friend' table, and then insert
    public void sendFriendRequest(int userId, int friendId) {
        // check if record already exists, if so, do not insert but return
        ArrayList<FriendEntity> friends = queryFriends(userId);
        for (FriendEntity friend : friends) {
            if (friend.getFriendId() == friendId) { // if record exists, return -1 and do not insert
                return;
            }
        }

        friends = queryFriends(friendId);
        for (FriendEntity friend : friends) {
            if (friend.getFriendId() == userId) {
                return;
            }
        }

        insertFriend(friendId, userId, FriendStatus.FRIEND_REQUESTED);
    }

    public void updateCardLevel(int folderId, int deckId, int cardId, Difficulty difficulty) {
        int level = -1; // initialized to be -1 if no level has been entered
        switch (difficulty) {
            case EASY: level = 0; break;
            case HARD: level = 1; break;
            case FORGOT: level = 2; break;
        }
        String query = "UPDATE card SET level = " + level +
                " WHERE folder_id = " + folderId +
                " AND deck_id = " + deckId +
                " AND card_id = " + cardId;
        db.execSQL(query);
    }
}
