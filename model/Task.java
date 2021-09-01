package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Task {
    private String title;
    private static ArrayList<Task> allTasks = new ArrayList<>();
    private ArrayList<LocalDate> allDatesOfTask;
    private String startDate;
    private String startTime;
    private String endTime;
    private int repeatNumber = -1;
    private RepeatFormat repeatFormat = RepeatFormat.D;
    public boolean haveLink;
    public String hasLink = "F";
    private String link;
    private long calendarId;
    private LocalDate startDateTimeFormat;
    private LocalDate endDateTimeFormat;


    public Task(String title) {
        this.title = title;
        allTasks.add(this);
    }

    public void addOrEditMethod() {
        allDatesOfTask = new ArrayList<>();
        if (repeatNumber == 0) {
            allDatesOfTask.add(startDateTimeFormat);
        } else if (repeatNumber > 0) {
            int i = 0;
            while (i < repeatNumber) {
                allDatesOfTask.add(findDatesAfter(repeatFormat, i));
                i++;
            }
        } else if (endDateTimeFormat != null) {
            for (int i = 0; ; i++) {
                LocalDate dateToAdd = findDatesAfter(repeatFormat, i);
                if (dateToAdd.isBefore(endDateTimeFormat) || dateToAdd.isEqual(endDateTimeFormat)) {
                    allDatesOfTask.add(dateToAdd);
                } else break;
            }
        }
    }

    public void setEndDateTimeFormat(LocalDate endDateTimeFormat) {
        this.endDateTimeFormat = endDateTimeFormat;
    }

    public void setStartDateTimeFormat(LocalDate startDateTimeFormat) {
        this.startDateTimeFormat = startDateTimeFormat;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String TOrF(boolean haveLink) {
        if (haveLink) hasLink = "T";
        else hasLink = "F";
        return hasLink;
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

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public void setRepeatFormat(RepeatFormat repeatFormat) {
        this.repeatFormat = repeatFormat;
    }

    public void setHaveLink(boolean haveLink) {
        this.haveLink = haveLink;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCalendarId() {
        return calendarId;
    }


    public String getTitle() {
        return title;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public static Task getTaskByTitle(String title) {
        for (Task currentTask : allTasks)
            if (currentTask.getTitle().equals(title))
                return currentTask;
        return null;
    }


    public ArrayList<LocalDate> getAllDatesOfTask() {
        return allDatesOfTask;
    }

    public static void removeTask(String title) {
        Calendar.getCalendarById(Task.getTaskByTitle(title).getCalendarId()).removeTask(Task.getTaskByTitle(title));
        allTasks.remove(Task.getTaskByTitle(title));
    }
}
