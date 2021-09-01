package controller;

import model.Account;
import model.Calendar;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMenu {
    public static final Pattern strengthCheckPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");

    public static void getCommand() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine().trim();
            if (command.matches("Register\\s+\\S+\\s+\\S+"))
                processRegister(command);
            else if (command.matches("Login\\s+\\S+\\s+\\S+"))
                processLogin(command, scanner);
            else if (command.matches("Change\\s+Password\\s+\\S+\\s+\\S+\\s+\\S+"))
                processChangePassword(command);
            else if (command.matches("Remove\\s+\\S+\\s+\\S+"))
                processRemove(command);
            else if (command.matches("Show\\s+All\\s+Usernames"))
                processPrint();
            else if (command.equals("Exit")) break;
            else System.out.println("invalid command!");
        }
    }

    private static void processPrint() {
        String toHold = "";
        for (Account current : Account.getAllAccounts()) {
            toHold += current.getUsername() + ",";
        }
        String[] usernames = toHold.split(",");
        if (Account.getAllAccounts().isEmpty()) System.out.println("nothing");
        else {
            for (int i = 0; i < usernames.length; i++) {
                for (int j = i; j < usernames.length; j++) {
                    if (usernames[j].compareTo(usernames[i]) < 0) {
                        String temp = usernames[j];
                        usernames[j] = usernames[i];
                        usernames[i] = temp;
                    }
                }
            }
            for (String username : usernames)
                System.out.println(username);
        }
    }

    private static void processRemove(String command) {
        Pattern pattern = Pattern.compile("Remove\\s+(\\S+)\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String username = matcher.group(1);
        String password = matcher.group(2);
        if (!username.matches("\\w+"))
            System.out.println("invalid username!");
        else if (Account.getAccountByUsername(username) == null)
            System.out.println("no user exists with this username");
        else if (!password.matches("\\w+"))
            System.out.println("invalid password!");
        else if (!Account.getAccountByUsername(username).getPassword().equals(password))
            System.out.println("password is wrong!");
        else {
            Calendar.removeWithUsername(username);
            Account.removeAccount(username);
            System.out.println("removed successfully!");
        }
    }

    private static void processChangePassword(String command) {
        Pattern pattern = Pattern.compile("Change\\s+Password\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String username = matcher.group(1);
        String oldPassword = matcher.group(2);
        String newPassword = matcher.group(3);
        if (!username.matches("\\w+"))
            System.out.println("invalid username!");
        else if (Account.getAccountByUsername(username) == null)
            System.out.println("no user exists with this username");
        else if (!oldPassword.matches("\\w+"))
            System.out.println("invalid old password!");
        else if (!Account.getAccountByUsername(username).getPassword().equals(oldPassword))
            System.out.println("password is wrong!");
        else if (!newPassword.matches("\\w+"))
            System.out.println("invalid new password!");
        else if (!passwordStrength(newPassword))
            System.out.println("new password is weak!");
        else {
            Account.getAccountByUsername(username).setPassword(newPassword);
            System.out.println("password changed successfully!");
        }
    }

    private static void processRegister(String command) {
        Pattern pattern = Pattern.compile("Register\\s+(\\S+)\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String username = matcher.group(1);
        String password = matcher.group(2);
        if (!username.matches("\\w+"))
            System.out.println("invalid username!");
        else if (Account.getAccountByUsername(username) != null)
            System.out.println("a user exists with this username");
        else if (!password.matches("\\w+"))
            System.out.println("invalid password!");
        else if (!passwordStrength(password))
            System.out.println("password is weak!");
        else {
            Account account = new Account(username, password);
            account.setPassword(password);
            System.out.println("register successful!");
        }
    }

    private static void processLogin(String command, Scanner scanner) {
        Pattern pattern = Pattern.compile("Login\\s+(\\S+)\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String username = matcher.group(1);
        String password = matcher.group(2);
        if (!username.matches("\\w+"))
            System.out.println("invalid username!");
        else if (Account.getAccountByUsername(username) == null)
            System.out.println("no user exists with this username");
        else if (!password.matches("\\w+"))
            System.out.println("invalid password!");
        else if (!Account.getAccountByUsername(username).getPassword().equals(password))
            System.out.println("password is wrong!");
        else {
            System.out.println("login successful!");
            if (!Account.getAccountByUsername(username).hasLoggedInBefore) {
                Calendar calendar = new Calendar(username);
                System.out.println("calendar created successfully!");
                calendar.setOwner(username);
                calendar.isActive = true;
                Account.getAccountByUsername(username).calendarsOfAccount.add(calendar);
                Account.getAccountByUsername(username).hasLoggedInBefore = true;
            }
            MainMenu.getCommand(username, scanner);
        }
    }

    private static boolean passwordStrength(String password) {
        if (password.length() < 5) return false;
        return strengthCheckPattern.matcher(password).matches();
    }
}