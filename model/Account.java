package model;

import java.util.ArrayList;

public class Account {
    private String username;
    private String password;
    private static ArrayList<Account> allAccounts = new ArrayList<>();
    public ArrayList<Calendar> calendarsOfAccount = new ArrayList<>();
    public boolean hasLoggedInBefore = false;

    public Account(String username, String password) {
        this.username = username;
        this.setPassword(password);
        allAccounts.add(this);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Calendar getCalendarByIdInAccount(long id) {
        for (Calendar currentCalendar : calendarsOfAccount)
            if (currentCalendar.getId() == id) return currentCalendar;
        return null;
    }

    public String getUsername() {
        return username;
    }

    public static ArrayList<Account> getAllAccounts() {
        return allAccounts;
    }

    public static void removeAccount(String username) {
        allAccounts.remove(Account.getAccountByUsername(username));
    }

    public static Account getAccountByUsername(String username) {
        for (Account currentAccount : allAccounts)
            if (currentAccount.getUsername().equals(username))
                return currentAccount;
        return null;
    }
}
