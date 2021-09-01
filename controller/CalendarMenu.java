package controller;

import model.*;
import model.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarMenu {
    public static void getCommand(long id, String username, Scanner scanner) {
        while (true) {
            String command = scanner.nextLine().trim();
            if (command.matches("Show\\s+Events"))
                showEventProcess(username, id);
            else if (command.matches("Show\\s+tasks"))
                showTaskProcess(username, id);
            else if (command.matches("Add\\s+Event\\s+(\\S+)\\s+([\\d]{4}_\\d\\d_\\d\\d)\\s+((?:[\\d_]+)|(?:None))\\s+(?:([DWM])\\s+)?([TF])"))
                addEvent(command, id, username, scanner);
            else if (command.matches("Add\\s+Task\\s+(\\S+)\\s+(\\d\\d_\\d\\d)\\s+" +
                    "(\\d\\d_\\d\\d)\\s+([\\d]{4}_\\d\\d_\\d\\d)\\s+((?:[\\d_]+)|(?:None))\\s+(?:([DWM])\\s+)?([TF])"))
                addTask(command, id, username, scanner);
            else if (command.matches("Edit\\s+Event\\s+\\S+\\s+(?:title|kind of repeat|repeat|meet)\\s+(?:\\S+|(?:M|D|W)|(?:T|F))"))
                editEvent(command, scanner, username, id);
            else if (command.matches("Edit\\s+Task\\s+(\\S+)\\s+(title|kind of repeat|repeat|meet|start time|end time)\\s+" +
                    "(\\S+|(?:M|D|W)|(?:T|F)|(?:[0-9][0-9][_][0-9][0-9]))"))
                editTask(command, scanner, username, id);
            else if (command.matches("Delete\\s+Event\\s+\\S+"))
                deleteEvent(command, username, id);
            else if (command.matches("Delete\\s+Task\\s+\\S+"))
                deleteTask(command, username, id);
            else if (command.matches("Back")) break;
            else System.out.println("invalid command!");
        }
    }

    private static void deleteTask(String command, String username, long id) {
        Pattern pattern = Pattern.compile("Delete\\s+Task\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String title = matcher.group(1);
        if (!Calendar.getCalendarById(id).getOwner().equals(username))
            System.out.println("you don't have access to do this!");
        else if (!title.matches("\\w+")) System.out.println("invalid title!");
        else if (Task.getTaskByTitle(title) == null) System.out.println("there is no task with this title!");
        else {
            Task.removeTask(title);
            System.out.println("task deleted successfully!");
        }
    }

    private static void deleteEvent(String command, String username, long id) {
        Pattern pattern = Pattern.compile("Delete\\s+Event\\s+(\\S+)");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String title = matcher.group(1);
        if (!Calendar.getCalendarById(id).getOwner().equals(username))
            System.out.println("you don't have access to do this!");
        else if (!title.matches("\\w+")) System.out.println("invalid title!");
        else if (Event.getEventByTitle(title) == null) System.out.println("there is no event with this title!");
        else {
            Event.removeEvent(title);
            System.out.println("event deleted successfully!");
        }
    }

    private static void editTask(String command, Scanner scanner, String username, long id) {
        Pattern pattern = Pattern.compile("Edit\\s+Task\\s+(\\S+)\\s+(title|kind of repeat|repeat|meet|start time|end time)\\s+" +
                "(\\S+|(?:M|D|W)|(?:T|F)|(?:[0-9][0-9][_][0-9][0-9]))");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String titleOfTask = matcher.group(1);
        String notSureWhat = matcher.group(2);
        String toChange = matcher.group(3);
        if (!Calendar.getCalendarById(id).getOwner().equals(username)) {
            System.out.println("you don't have access to do this!");
            return;
        } else if (!titleOfTask.matches("\\w+")) {
            System.out.println("invalid title!");
            return;
        } else if (Task.getTaskByTitle(titleOfTask) == null) {
            System.out.println("there is no task with this title!");
            return;
        } else if (notSureWhat.equals("title")) {
            if (!toChange.matches("\\w+")) {
                System.out.println("invalid title!");
                return;
            } else if (Task.getTaskByTitle(toChange) != null) {
                System.out.println("there is another task with this title!");
                return;
            } else {
                Task.getTaskByTitle(titleOfTask).setTitle(toChange);
                titleOfTask = toChange;
                Task.getTaskByTitle(titleOfTask).addOrEditMethod();
            }
        } else if (notSureWhat.equals("kind of repeat")) {
            if (!toChange.matches("M|D|W")) {
                System.out.println("invalid command!");
                return;
            } else {
                Task task = Task.getTaskByTitle(titleOfTask);
                if (toChange.equals("D")) task.setRepeatFormat(RepeatFormat.D);
                else if (toChange.equals("W")) task.setRepeatFormat(RepeatFormat.W);
                else task.setRepeatFormat(RepeatFormat.M);
            }
        } else if (notSureWhat.equals("repeat")) {
            if (!toChange.matches("\\d+")) {
                System.out.println("invalid command!");
                return;
            } else
                Task.getTaskByTitle(titleOfTask).setRepeatNumber(Integer.parseInt(toChange));
        } else if (notSureWhat.equals("meet")) {
            if (!toChange.matches("T|F")) {
                System.out.println("invalid command!");
                return;
            } else {
                boolean checkLink = true;
                if (toChange.equals("F")) checkLink = false;
                Task.getTaskByTitle(titleOfTask).setHaveLink(checkLink);
                if (checkLink) Task.getTaskByTitle(titleOfTask).setLink(scanner.nextLine());
            }
        } else if (notSureWhat.equals("start time")) {
            if (!toChange.matches("[0-9][0-9][_][0-9][0-9]")) {
                System.out.println("invalid command!");
                return;
            } else Task.getTaskByTitle(titleOfTask).setStartTime(toChange);
        } else if (notSureWhat.equals("end time")) {
            if (!toChange.matches("[0-9][0-9][_][0-9][0-9]")) {
                System.out.println("invalid command!");
                return;
            } else Task.getTaskByTitle(titleOfTask).setEndTime(toChange);
        }
        System.out.println("task edited!");
        Task.getTaskByTitle(titleOfTask).addOrEditMethod();
    }

    private static void editEvent(String command, Scanner scanner, String username, long id) {
        Pattern pattern = Pattern.compile("Edit\\s+Event\\s+(\\S+)\\s+(title|kind of repeat|repeat|meet)\\s+(\\S+|(?:M|D|W)|(?:T|F))");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String titleOfEvent = matcher.group(1);
        String notSureWhat = matcher.group(2);
        String toChange = matcher.group(3);
        if (!Calendar.getCalendarById(id).getOwner().equals(username)) {
            System.out.println("you don't have access to do this!");
            return;
        } else if (!titleOfEvent.matches("\\w+")) {
            System.out.println("invalid title!");
            return;
        } else if (Event.getEventByTitle(titleOfEvent) == null) {
            System.out.println("there is no event with this title!");
            return;
        } else if (notSureWhat.equals("title")) {
            if (!toChange.matches("\\w+")) {
                System.out.println("invalid title!");
                return;
            } else if (Event.getEventByTitle(toChange) != null) {
                System.out.println("there is another event with this title!");
                return;
            } else {
                Event.getEventByTitle(titleOfEvent).setTitle(toChange);
                Event.getEventByTitle(toChange).addOrEditMethod();
                titleOfEvent = toChange;
            }
        } else if (notSureWhat.equals("kind of repeat")) {
            if (!toChange.matches("M|D|W")) {
                System.out.println("invalid command!");
                return;
            } else {
                Event event = Event.getEventByTitle(titleOfEvent);
                if (toChange.equals("D")) event.setRepeatFormat(RepeatFormat.D);
                else if (toChange.equals("W")) event.setRepeatFormat(RepeatFormat.W);
                else event.setRepeatFormat(RepeatFormat.M);
                Event.getEventByTitle(titleOfEvent).addOrEditMethod();
            }
        } else if (notSureWhat.equals("repeat")) {
            if (!toChange.matches("\\d+")) {
                System.out.println("invalid command!");
                return;
            } else
                Event.getEventByTitle(titleOfEvent).setRepeatNumber(Integer.parseInt(toChange));
        } else if (notSureWhat.equals("meet")) {
            if (!toChange.matches("T|F")) {
                System.out.println("invalid command!");
                return;
            } else {
                boolean checkLink = true;
                if (toChange.equals("F")) checkLink = false;
                Event.getEventByTitle(titleOfEvent).setHaveLink(checkLink);
                if (checkLink) Event.getEventByTitle(titleOfEvent).setLink(scanner.nextLine());
            }
        }
        System.out.println("event edited!");
        Event.getEventByTitle(titleOfEvent).addOrEditMethod();
    }

    private static void addTask(String command, long id, String username, Scanner scanner) {
        Pattern pattern = Pattern.compile("Add\\s+Task\\s+(\\S+)\\s+(\\d\\d_\\d\\d)\\s+(\\d\\d_\\d\\d)\\s+([\\d]{4}_\\d\\d_\\d\\d)\\s+" +
                "((?:[\\d_]+)|(?:None))\\s+(?:([DWM])\\s+)?([TF])");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String title = matcher.group(1);
        String startTime = matcher.group(2);
        String endTime = matcher.group(3);
        String startDate = matcher.group(4);
        String notSure = matcher.group(5);
        String repeatForm = matcher.group(6);
        String doesHaveLink = matcher.group(7);
        String[] toChangeFormat = startDate.split("_");
        boolean checkLink = true;
        if (doesHaveLink.equals("F")) checkLink = false;
        if (errorMethod(username, id, startDate, title)) {
            if (notSure.matches("[0-9]{4}[_][0-9]{2}[_][0-9]{2}")) {
                if (!checkDateValidation(notSure)) {
                    System.out.println("invalid end date!");
                    return;
                }
            }
            if (Task.getTaskByTitle(title) != null) {
                System.out.println("there is another task with this title!");
                return;
            }
            if (notSure.matches("None") && repeatForm != null) {
                System.out.println("invalid command!");
                return;
            }
            LocalDate startDateNewFormat = LocalDate.of(Integer.parseInt(toChangeFormat[0]), Integer.parseInt(toChangeFormat[1]), Integer.parseInt(toChangeFormat[2]));
            Task task = createTask(title, id, checkLink, startDate, startTime, endTime, repeatForm);
            task.setStartDateTimeFormat(startDateNewFormat);
            if (notSure.matches("[0-9]{4}[_][0-9]{2}[_][0-9]{2}")) {
                String[] toChangeTheFormat = notSure.split("_");
                LocalDate endDateNewFormat = LocalDate.of(Integer.parseInt(toChangeTheFormat[0]), Integer.parseInt(toChangeTheFormat[1]), Integer.parseInt(toChangeTheFormat[2]));
                task.setEndDateTimeFormat(endDateNewFormat);
            } else if (notSure.matches("\\d+"))
                task.setRepeatNumber(Integer.parseInt(notSure));
            else task.setRepeatNumber(0);
            if (doesHaveLink.equals("T"))
                setLinkTask(scanner, task);
            task.addOrEditMethod();
        }
    }

    private static void addEvent(String command, long id, String username, Scanner scanner) {
        Pattern pattern = Pattern.compile("Add\\s+Event\\s+(\\S+)\\s+([\\d]{4}_\\d\\d_\\d\\d)\\s+((?:[\\d_]+)|(?:None))\\s+(?:([DWM])\\s+)?([TF])");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        String title = matcher.group(1);
        String startDate = matcher.group(2);
        String notSure = matcher.group(3);
        String repeatForm = matcher.group(4);
        String doesHaveLink = matcher.group(5);
        String[] toChangeFormat = startDate.split("_");
        boolean checkLink = true;
        if (doesHaveLink.equals("F")) checkLink = false;
        if (errorMethod(username, id, startDate, title)) {
            if (notSure.matches("[0-9]{4}[_][0-9]{2}[_][0-9]{2}")) {
                if (!checkDateValidation(notSure)) {
                    System.out.println("invalid end date!");
                    return;
                }
            }
            if (Event.getEventByTitle(title) != null) {
                System.out.println("there is another event with this title!");
                return;
            }
            if (notSure.matches("None") && repeatForm != null) {
                System.out.println("invalid command!");
                return;
            }
            LocalDate startDateNewFormat = LocalDate.of(Integer.parseInt(toChangeFormat[0]), Integer.parseInt(toChangeFormat[1]), Integer.parseInt(toChangeFormat[2]));
            Event event = createEvent(title, id, checkLink, startDate, repeatForm);
            event.setStartDateTimeFormat(startDateNewFormat);
            if (notSure.matches("[0-9]{4}[_][0-9]{2}[_][0-9]{2}")) {
                String[] toChangeTheFormat = notSure.split("_");
                LocalDate endDateNewFormat = LocalDate.of(Integer.parseInt(toChangeTheFormat[0]), Integer.parseInt(toChangeTheFormat[1]), Integer.parseInt(toChangeTheFormat[2]));
                event.setEndDateTimeFormat(endDateNewFormat);
            } else if (notSure.matches("\\d+"))
                event.setRepeatNumber(Integer.parseInt(notSure));
            else if (notSure.matches("None")) event.setRepeatNumber(0);
            if (doesHaveLink.equals("T"))
                setLinkEvent(scanner, event);
            event.addOrEditMethod();
        }
    }

    private static void showTaskProcess(String username, long id) {
        if (Calendar.getCalendarById(id).getAllTasks().isEmpty()) System.out.println("nothing");
        else {
            Collections.sort(Calendar.getCalendarById(id).getAllTasks(), new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    int c = o1.getStartDate().compareTo(o2.getStartDate());
                    if (c == 0) c = o1.getStartTime().compareTo(o2.getStartTime());
                    if (c == 0) c = o1.getTitle().compareTo(o2.getTitle());
                    return c;
                }
            });
            String hold = "";
            for (Task currentTask : Calendar.getCalendarById(id).getAllTasks())
                hold += "Task: " + currentTask.getTitle() + " " + currentTask.TOrF(currentTask.haveLink) +
                        " " + currentTask.getStartTime() + " " + currentTask.getEndTime() + ",";
            String[] toPrint = hold.split(",");
            System.out.println("tasks of this calendar:");
            for (String task : toPrint)
                System.out.println(task);
        }
    }

    private static void showEventProcess(String username, long id) {
        String hold = "";
        if (Calendar.getCalendarById(id).getAllEvents().isEmpty()) System.out.println("nothing");
        else {
            for (Event currentEvent : Calendar.getCalendarById(id).getAllEvents())
                hold += "Event: " + currentEvent.getTitle() + " " + currentEvent.TOrF(currentEvent.haveLink) + ",";
            String[] toPrint = hold.split(",");
            for (int i = 0; i < toPrint.length; i++)
                for (int j = 0; j < toPrint.length; j++) {
                    if (toPrint[j].compareTo(toPrint[i]) > 0) {
                        String temp = toPrint[j];
                        toPrint[j] = toPrint[i];
                        toPrint[i] = temp;
                    }
                }
            System.out.println("events of this calendar:");
            for (String event : toPrint)
                System.out.println(event);
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

    private static boolean errorMethod(String username, long id, String startDate, String title) {
        if (!Calendar.getCalendarById(id).getOwner().equals(username))
            System.out.println("you don't have access to do this!");
        else if (!title.matches("\\w+"))
            System.out.println("invalid title!");
        else if (!checkDateValidation(startDate))
            System.out.println("invalid start date!");
        else return true;
        return false;
    }


    private static Task createTask(String title, long id, boolean checkLink, String startDate, String startHour, String endHour, String repeatForm) {
        Task task = new Task(title);
        System.out.println("task added successfully!");
        task.setTitle(title);
        Calendar.getCalendarById(id).addTask(task);
        task.setCalendarId(id);
        task.setHaveLink(checkLink);
        task.setStartTime(startHour);
        task.setStartDate(startDate);
        task.setEndTime(endHour);
        task.setCalendarId(id);
        if (repeatForm != null) {
            if (repeatForm.equals("D")) task.setRepeatFormat(RepeatFormat.D);
            else if (repeatForm.equals("W")) task.setRepeatFormat(RepeatFormat.W);
            else task.setRepeatFormat(RepeatFormat.M);
        }
        return task;
    }

    private static Event createEvent(String title, long id, boolean checkLink, String startDate, String repeatForm) {
        Event event = new Event(title);
        System.out.println("event added successfully!");
        Calendar.getCalendarById(id).addEvent(event);
        Event.getEventByTitle(title).setCalendarId(id);
        event.setHaveLink(checkLink);
        event.setCalendarId(id);
        event.setStartDate(startDate);
        if (repeatForm != null) {
            if (repeatForm.equals("D")) event.setRepeatFormat(RepeatFormat.D);
            else if (repeatForm.equals("W")) event.setRepeatFormat(RepeatFormat.W);
            else event.setRepeatFormat(RepeatFormat.M);
        }
        return event;
    }

    private static void setLinkTask(Scanner scanner, Task task) {
        String link = scanner.nextLine();
        task.setLink(link);
        task.setHaveLink(true);
    }

    private static void setLinkEvent(Scanner scanner, Event event) {
        String link = scanner.nextLine();
        event.setLink(link);
        event.setHaveLink(true);
    }
}
