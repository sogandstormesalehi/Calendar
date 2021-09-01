package model;

import java.util.ArrayList;

public class Calendar {
    private String title;
    private static long idCounter = 0;
    private long id;
    private static ArrayList<Calendar> allCalendars = new ArrayList<>();
    public ArrayList<Event> eventsOfThisCalendar = new ArrayList<>();
    public ArrayList<Task> tasksOfThisCalendar = new ArrayList<>();

    public boolean isActive = false;
    private String owner;

    public Calendar(String title) {
        this.setId();
        this.title = title;
        allCalendars.add(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    private void setId() {
        this.id = ++idCounter;
    }

    public long getId() {
        return id;
    }

    public void disable() {
        if (isActive) isActive = false;
    }

    public void enable() {
        if (!isActive) isActive = true;
    }

    public String getTitle() {
        return title;
    }


    public static ArrayList<Calendar> getAllCalendars() {
        return allCalendars;
    }


    public void addTask(Task task) {
        tasksOfThisCalendar.add(task);
    }

    public void addEvent(Event event) {
        eventsOfThisCalendar.add(event);
    }

    public void removeTask(Task task) {
        tasksOfThisCalendar.remove(task);
    }

    public void removeEvent(Event event) {
        eventsOfThisCalendar.remove(event);
    }

    public ArrayList<Event> getAllEvents() {
        return eventsOfThisCalendar;
    }

    public ArrayList<Task> getAllTasks() {
        return tasksOfThisCalendar;
    }

    public static Calendar getCalendarById(long id) {
        for (Calendar currentCalendar : allCalendars)
            if (currentCalendar.id == id)
                return currentCalendar;
        return null;
    }

    public static void addCalendarToAccount(String username, long id) {
        Account.getAccountByUsername(username).calendarsOfAccount.add(Calendar.getCalendarById(id));
    }

    public static void removeWithUsername(String username) {
        ArrayList<Calendar> toRemove = new ArrayList<>();
        for (Calendar calendar : allCalendars)
            if (calendar.getOwner().equals(username))
                toRemove.add(calendar);
        for (Calendar calendar : toRemove)
            if (allCalendars.contains(calendar))
                allCalendars.remove(calendar);

    }
}
