package model;

import java.time.LocalDate;
import java.util.*;

public class Event {
    private String title;
    private static ArrayList<Event> allEvents = new ArrayList<>();
    public ArrayList<LocalDate> allDatesOfEvent = new ArrayList<>();
    private String startDate;
    private String endDate;
    private int repeatNumber = -1;
    private RepeatFormat repeatFormat = RepeatFormat.D;
    public boolean haveLink;
    public String hasLink = "F";
    private String link;
    private long calendarId;
    private LocalDate startDateTimeFormat;
    private LocalDate endDateTimeFormat;

    public Event(String title) {
        this.title = title;
        allEvents.add(this);
        addOrEditMethod();
    }

    public void addOrEditMethod() {
        allDatesOfEvent = new ArrayList<>();
        if (repeatNumber == 0)
            allDatesOfEvent.add(startDateTimeFormat);
        else if (repeatNumber > 0) {
            int i = 0;
            while (i < repeatNumber) {
                allDatesOfEvent.add(findDatesAfter(repeatFormat, i));
                i++;
            }
        } else if (endDateTimeFormat != null) {
            for (int i = 0; ; i++) {
                LocalDate dateToAdd = findDatesAfter(repeatFormat, i);
                if (dateToAdd.isBefore(endDateTimeFormat) || dateToAdd.isEqual(endDateTimeFormat))
                    allDatesOfEvent.add(dateToAdd);
                else break;
            }
        }
    }

    private LocalDate findDatesAfter(RepeatFormat repeatFormat, int countOfDatesToAdd) {
        if (repeatFormat.equals(RepeatFormat.D))
            return startDateTimeFormat.plusDays(countOfDatesToAdd);
        else if (repeatFormat.equals(RepeatFormat.W))
            return startDateTimeFormat.plusWeeks(countOfDatesToAdd);
        else if (repeatFormat.equals(RepeatFormat.M))
            return startDateTimeFormat.plusMonths(countOfDatesToAdd);
        else
            return startDateTimeFormat;
    }

    public void setStartDateTimeFormat(LocalDate startDateTimeFormat) {
        this.startDateTimeFormat = startDateTimeFormat;
    }

    public void setEndDateTimeFormat(LocalDate endDateTimeFormat) {
        this.endDateTimeFormat = endDateTimeFormat;
    }

    public String TOrF(boolean haveLink) {
        if (haveLink) hasLink = "T";
        else hasLink = "F";
        return hasLink;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setHaveLink(boolean haveLink) {
        this.haveLink = haveLink;
    }

    public void setRepeatFormat(RepeatFormat repeatFormat) {
        this.repeatFormat = repeatFormat;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public long getCalendarId() {
        return calendarId;
    }


    public static Event getEventByTitle(String title) {
        for (Event currentEvent : allEvents)
            if (currentEvent.getTitle().equals(title))
                return currentEvent;
        return null;
    }


    public ArrayList<LocalDate> getAllDatesOfEvent() {
        return allDatesOfEvent;
    }

    public static void removeEvent(String title) {
        Calendar.getCalendarById(Event.getEventByTitle(title).getCalendarId()).removeEvent(Event.getEventByTitle(title));
        allEvents.remove(Event.getEventByTitle(title));
    }


}

