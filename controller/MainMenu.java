package controller;

import model.*;
import model.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu {
    public static void getCommand(String username, Scanner scanner) {
        setDefault(username);
        while (true) {
            String command = scanner.nextLine().trim();
            if (command.matches("Create\\s+New\\s+Calendar\\s+\\S+"))
                createProcess(command, username);
            else if (command.matches("Open\\s+Calendar\\s+\\d+"))
                openProcess(command, scanner, username);
            else if (command.matches("Enable\\s+Calendar\\s+\\d+"))
                enableProcess(command, username);
            else if (command.matches("Disable\\s+Calendar\\s+\\d+"))
                disableProcess(command, username);
            else if (command.matches("Delete\\s+Calendar\\s+\\d+"))
                deleteProcess(command, username);
            else if (command.matches("Remove\\s+Calendar\\s+\\d+"))
                removeProcess(command, username, scanner);
            else if (command.matches("Edit\\s+Calendar\\s+\\d+\\s+\\S+"))
                editProcess(command, username);
            else if (command.matches("Share\\s+Calendar\\s+\\d+\\s+(?:(?:\\S+)+\\s*)+"))
                shareProcess(command, username);
            else if (command.matches("Show\\s+(?:[0-9]{4}[_][0-9]{2}[_][0-9]{2})"))
                showDateProcess(command);
            else if (command.matches("Show\\s+Calendars"))
                showCalendarProcess(username);
            else if (command.matches("Show\\s+Enabled\\s+Calendars"))
                showEnabledProcess(username);
            else if (command.matches("Logout")) {
                System.out.println("logout successful");
                break;
            } else System.out.println("invalid command!");
        }
    }

    private static void showEnabledProcess(String username) {
        Collections.sort(Account.getAccountByUsername(username).calendarsOfAccount, new Comparator<Calendar>() {
            @Override
            public int compare(Calendar o1, Calendar o2) {
                return Double.compare(o1.getId(), o2.getId());
            }
        });
        String hold = "";
        ArrayList<Calendar> checkExistence = new ArrayList<>();
        for (Calendar calendar : Account.getAccountByUsername(username).calendarsOfAccount)
            if (calendar.isActive) {
                hold += "Calendar: " + calendar.getTitle() + " " + calendar.getId() + ",";
                checkExistence.add(calendar);
            }
        if (checkExistence.isEmpty()) System.out.println("nothing");
        else {
            String[] toPrint = hold.split(",");
            for (int i = 0; i < toPrint.length; i++)
                System.out.println(toPrint[i]);
        }
    }

    private static void showCalendarProcess(String username) {
        Collections.sort(Calendar.getAllCalendars(), new Comparator<Calendar>() {
            @Override
            public int compare(Calendar o1, Calendar o2) {
                return Double.compare(o1.getId(), o2.getId());
            }
        });
        String hold = "";
        ArrayList<Calendar> checkExistence = new ArrayList<>();
        for (Calendar calendar : Calendar.getAllCalendars())
            if (Calendar.getCalendarById(calendar.getId()).getOwner().equals(username)) {
                hold += "Calendar: " + calendar.getTitle() + " " + calendar.getId() + ",";
                checkExistence.add(calendar);
            }
        if (checkExistence.isEmpty()) System.out.println("nothing");
        else {
            String[] toPrint = hold.split(",");
            for (int i = 0; i < toPrint.length; i++)
                System.out.println(toPrint[i]);
        }
    }

    private static void showDateProcess(String command) {
        Pattern pattern = Pattern.compile("Show\\s+([0-9]{4}[_][0-9]{2}[_][0-9]{2})");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String date = matcher.group(1);
        String[] dates = date.split("_");
        LocalDate dateToCheck = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
        if (!checkDateValidation(date)) System.out.println("date is invalid!");
        else {
            ArrayList<Event> holdEvents = new ArrayList<>();
            String printEvent = "";
            System.out.println("events on " + date + ":");
            for (Calendar thisCalendar : Calendar.getAllCalendars()) {
                if (thisCalendar.isActive) {
                    for (Event thisEvent : thisCalendar.getAllEvents()) {
                        for (LocalDate thisDate : thisEvent.getAllDatesOfEvent())
                            if (thisDate != null)
                                if (thisDate.isEqual(dateToCheck))
                                    holdEvents.add(thisEvent);
                    }
                }
            }
            for (Event thisEvent : holdEvents)
                printEvent += "Event: " + thisEvent.getTitle() + " " + thisEvent.TOrF(thisEvent.haveLink) + ",";
            String[] finalPrintEvent = printEvent.split(",");
            for (int i = 0; i < finalPrintEvent.length; i++)
                for (int j = 0; j < finalPrintEvent.length; j++) {
                    if (finalPrintEvent[j].compareTo(finalPrintEvent[i]) > 0) {
                        String temp = finalPrintEvent[j];
                        finalPrintEvent[j] = finalPrintEvent[i];
                        finalPrintEvent[i] = temp;
                    }
                }
            if (!holdEvents.isEmpty())
                for (String event : finalPrintEvent)
                    System.out.println(event);
            ArrayList<Task> holdTasks = new ArrayList<>();
            String printTask = "";
            System.out.println("tasks on " + date + ":");
            for (Calendar thisCalendar : Calendar.getAllCalendars()) {
                if (thisCalendar.isActive) {
                    for (Task thisTask : thisCalendar.getAllTasks()) {
                        for (LocalDate thisDate : thisTask.getAllDatesOfTask())
                            if (thisDate != null)
                                if (thisDate.isEqual(dateToCheck))
                                    holdTasks.add(thisTask);
                    }
                }
            }
            Collections.sort(holdTasks, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    int c = o1.getStartTime().compareTo(o2.getStartTime());
                    if (c == 0) c = o1.getTitle().compareTo(o2.getTitle());
                    return c;
                }
            });
            String hold = "";
            for (Task currentTask : holdTasks)
                hold += "Task: " + currentTask.getTitle() + " " + currentTask.TOrF(currentTask.haveLink) +
                        " " + currentTask.getStartTime() + " " + currentTask.getEndTime() + ",";
            String[] toPrint = hold.split(",");
            if (!holdTasks.isEmpty())
                for (String task : toPrint) {
                    System.out.println(task);
                }
        }
    }

    private static void shareProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Share\\s+Calendar\\s+(\\d+)\\s+((?:(?:\\S+)+\\s*)+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            Pattern patternForUsernames = Pattern.compile("(\\S+)");
            Matcher matcherUsernames = patternForUsernames.matcher(matcher.group(2));
            String usernames = "";
            while (matcherUsernames.find()) {
                if (!matcherUsernames.group(1).equals(usernames)) usernames += matcherUsernames.group(1) + ",";
            }
            String[] listOfUsers = usernames.split(",");
            int countExistent = 0;
            for (Account account : Account.getAllAccounts()) {
                for (int i = 0; i < listOfUsers.length; i++)
                    if (account.getUsername().equals(listOfUsers[i])) {
                        countExistent++;
                    }
            }
            if (Calendar.getCalendarById(id) == null) {
                System.out.println("there is no calendar with this ID!");
            } else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");

            else if (!Calendar.getCalendarById(id).getOwner().equals(username))
                System.out.println("you don't have access to share this calendar!");

            else {
                int countInvalidUsernames = 0;
                for (int i = 0; i < listOfUsers.length; i++) {
                    if (!listOfUsers[i].matches("\\w+"))
                        countInvalidUsernames++;
                }
                if (countInvalidUsernames > 0) System.out.println("invalid username!");
                else if (countExistent != listOfUsers.length) System.out.println("no user exists with this username!");
                else {
                    System.out.println("calendar shared!");
                    label:
                    for (Account account : Account.getAllAccounts())
                        for (int i = 0; i < listOfUsers.length; i++)
                            if (account.getUsername().equals(listOfUsers[i])) {
                                if (!account.calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                                    Calendar.addCalendarToAccount(listOfUsers[i], id);
                                continue label;
                            }
                }
            }
        }
    }

    private static void editProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Edit\\s+Calendar\\s+(\\d+)\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            String newTitle = matcher.group(2);
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else if (!Calendar.getCalendarById(id).getOwner().equals(username))
                System.out.println("you don't have access to edit this calendar!");
            else if (!newTitle.matches("\\w+")) System.out.println("invalid title!");
            else {
                Calendar.getCalendarById(id).setTitle(newTitle);
                System.out.println("calendar title edited!");
            }
        }
    }

    private static void removeProcess(String command, String username, Scanner scanner) {
        Pattern pattern = Pattern.compile("Remove\\s+Calendar\\s+(\\d+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else if (!Calendar.getCalendarById(id).getOwner().equals(username)) {
                System.out.println("calendar removed!");
                Account.getAccountByUsername(username).calendarsOfAccount.remove(Calendar.getCalendarById(id));

            } else {
                System.out.println("do you want to delete this calendar?");
                String answer = scanner.nextLine();
                if (answer.equals("no")) System.out.println("OK!");
                else {
                    System.out.println("calendar deleted!");
                    for (Account account : Account.getAllAccounts())
                        account.calendarsOfAccount.remove(Calendar.getCalendarById(id));
                    Calendar.getAllCalendars().remove(Calendar.getCalendarById(id));
                }
            }
        }
    }

    private static void deleteProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Delete\\s+Calendar\\s+(\\d+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else if (!Calendar.getCalendarById(id).getOwner().equals(username))
                System.out.println("you don't have access to delete this calendar!");
            else {
                System.out.println("calendar deleted!");
                for (Account account : Account.getAllAccounts())
                    account.calendarsOfAccount.remove(Calendar.getCalendarById(id));
                Calendar.getAllCalendars().remove(Calendar.getCalendarById(id));
            }
        }
    }

    private static void disableProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Disable\\s+Calendar\\s+(\\d+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else {
                System.out.println("calendar disabled successfully!");
                Calendar.getCalendarById(id).disable();
            }
        }
    }

    private static void enableProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Enable\\s+Calendar\\s+(\\d+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else {
                System.out.println("calendar enabled successfully!");
                Account.getAccountByUsername(username).getCalendarByIdInAccount(id).enable();
            }
        }
    }

    private static void openProcess(String command, Scanner scanner, String username) {
        Pattern pattern = Pattern.compile("Open\\s+Calendar\\s+(\\d+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        if (matcher.group(1).length() > 9) System.out.println("there is no calendar with this ID!");
        else {
            long id = Long.parseLong(matcher.group(1));
            if (Calendar.getCalendarById(id) == null) System.out.println("there is no calendar with this ID!");
            else if (!Account.getAccountByUsername(username).calendarsOfAccount.contains(Calendar.getCalendarById(id)))
                System.out.println("you have no calendar with this ID!");
            else {
                System.out.println("calendar opened successfully!");
                CalendarMenu.getCommand(id, username, scanner);
            }
        }
    }

    private static void createProcess(String command, String username) {
        Pattern pattern = Pattern.compile("Create\\s+New\\s+Calendar\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String title = matcher.group(1);
        if (!title.matches("\\w+"))
            System.out.println("invalid title!");
        else {
            System.out.println("calendar created successfully!");
            Calendar calendar = new Calendar(title);
            Account.getAccountByUsername(username).calendarsOfAccount.add(calendar);
            calendar.setOwner(username);
        }
    }

    public static void setDefault(String username) {
        long min = 999999999;
        ArrayList<Calendar> defaultActive = new ArrayList<>();
        for (Calendar calendarToActive : Account.getAccountByUsername(username).calendarsOfAccount) {
            calendarToActive.disable();
            if (calendarToActive.getTitle().equals(username))
                defaultActive.add(calendarToActive);
        }
        if (defaultActive.size() == 0)
            for (Calendar calendarToActive : Account.getAccountByUsername(username).calendarsOfAccount)
                calendarToActive.disable();
        if (defaultActive.size() >= 1) {
            for (Calendar calendarToActive : defaultActive) {
                calendarToActive.disable();
                if (calendarToActive.getId() <= min) {
                    min = calendarToActive.getId();
                }
            }
            Calendar.getCalendarById(min).enable();
        }
    }

    public static boolean checkDateValidation(String startDate) {
        if (startDate.matches("[0-9]{4}[_][0-9]{2}[_][0-9]{2}")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            sdf.setLenient(false);
            try {
                Date d1 = sdf.parse(startDate);
                return true;
            } catch (ParseException e) {
                return false;
            }
        } else
            return true;
    }
}
