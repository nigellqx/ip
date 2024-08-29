package parser;


import exception.TakoException;
import task.TaskList;
import task.ToDo;
import task.Deadline;
import task.Event;
import ui.Ui;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;


/**
 * Parse all commands that is
 * being input into Tako chatbot.
 */
public class Parser {

    /**
     * Checks whether the input command is recognizable by Tako.
     *
     * @param command the string that is being input into Tako.
     * @throws TakoException if the command is not recognized.
     */
    public static void parse(String command) {
        try {
            if (command.equalsIgnoreCase("bye")) {
                Ui.exit();
            } else if (command.equalsIgnoreCase("list")) {
                Ui.printList();
            } else if (command.toLowerCase().startsWith("mark")) {
                parseMark(command);
            } else if (command.toLowerCase().startsWith("unmark")) {
                parseUnmark(command);
            } else if (command.toLowerCase().startsWith("todo")) {
                parseTodo(command);
            } else if (command.toLowerCase().startsWith("deadline")) {
                parseDeadline(command);
            } else if (command.toLowerCase().startsWith("event")) {
                parseEvent(command);
            } else if (command.toLowerCase().startsWith("delete")) {
                parseDelete(command);
            } else if (command.toLowerCase().startsWith("find")) {
                parseFind(command);
            } else {
                throw new TakoException("Tako does not understand this command!");
            }
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }


    /**
     * Marks the task as complete according to the input
     *
     * @param command
     * @throws TakoException if the mark command is not in the right form
     */
    public static void parseFind(String command) {
        try {
            if (command.length() > 4 && command.charAt(4) == ' ' && !command.substring(5).isBlank()) {
                String targetString = command.substring(5);
                Ui.findTargetString(targetString);
            } else {
                throw new TakoException("Wrong format! Find command should have the form 'find x', where x is not empty");
            }
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    public static void parseMark(String command) {
        try {
            if (command.length() > 4 && command.charAt(4) == ' ') {
                try {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    TaskList.markTask(taskNumber);
                } catch (NumberFormatException e) {
                    throw new TakoException("Wrong format! mark command should have the form 'mark x'," +
                                            " where x is an integer");
                }
            }
            throw new TakoException("Wrong format! mark command should have the form 'mark x'," +
                                    " where x is an integer");
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    /**
     * Marks the task as incomplete according to the input
     *
     * @param command
     * @throws TakoException if the unmark command is not in the right form
     */
    public static void parseUnmark(String command) {
        try {
            if (command.length() > 6 && command.charAt(6) == ' ') {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    TaskList.unmarkTask(taskNumber);
                } catch (NumberFormatException e) {
                    throw new TakoException("Wrong format! Unmark command should have the form 'unmark x'," +
                                            " where x is an integer");
                }
            }
            throw new TakoException("Wrong format! Unmark command should have the form 'unmark x'," +
                                    " where x is an integer");
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    /**
     * Deletes the task according to the input from the list
     *
     * @param command
     * @throws TakoException if the delete command is not in the right form
     */
    public static void parseDelete(String command) {
        try {
            if (command.length() > 6 && command.charAt(6) == ' ') {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    TaskList.deleteTask(taskNumber);
                } catch (NumberFormatException e) {
                    throw new TakoException("Wrong format! Delete command should have the form 'delete x'," +
                                            " where x is an integer");
                }
            }
            throw new TakoException("Wrong format! Delete command should have the form 'delete x'," +
                                    " where x is an integer");
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    /**
     * Breaks down the command into its description
     * Make the todo task according to the description
     *
     * @param command
     * @throws TakoException if the todo command is not in the right form
     */
    public static void parseTodo(String command) {
        try {
            if (command.length() > 4 && command.charAt(4) == ' ' && !command.substring(5).isBlank()) {
                String description = command.substring(5);
                TaskList.addTask(new ToDo(description));
            } else {
                throw new TakoException("Wrong format! Todo command should have the form 'mark x'," +
                                        " where x is not empty");
            }
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    /**
     * Breaks down the command into its description and when it is due
     * Make the deadline task according to the description and its due date
     *
     * @param command
     * @throws TakoException if the deadline command is not in the right form
     */
    public static void parseDeadline(String command) {
        try {
            if (command.length() > 8 && command.charAt(8) == ' ') {
                if (command.contains("/by")) {
                    int byPosition = command.indexOf("/by");
                    if (!command.substring(8, byPosition).isBlank()) {
                        if (command.length() > byPosition + 3 && command.charAt(byPosition + 3) == ' ') {
                            if (!command.substring(byPosition + 4).isBlank()) {
                                String description = command.substring(9, byPosition);
                                String by = command.substring(byPosition + 4);
                                if (checkValidDate(by)) {
                                    TaskList.addTask(new Deadline(description, LocalDate.parse(by)));
                                }
                            }
                        }
                    }
                }
            }
            throw new TakoException("Wrong format! Deadline command should have the form " +
                                    "'deadline x /by YYYY-MM-DD', where x is not empty");
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }

    /**
     * Breaks down the command into its description, starting date and end date
     * Make the event task according to the description, start and end date
     *
     * @param command
     * @throws TakoException if the event command is not in the right form
     */
    public static void parseEvent(String command) {
        try {
            int fromPosition = command.indexOf("/from");
            int toPosition = command.indexOf("/to");
            if (toPosition > fromPosition) {
                if (command.length() > 5 && command.charAt(5) == ' ') {
                    if (command.contains("/from")) {
                        if (!command.substring(5, fromPosition).isBlank()) {
                            if (command.length() > fromPosition + 5 && command.charAt(fromPosition + 5) == ' ') {
                                if (command.contains("/to")) {
                                    if (!command.substring(fromPosition + 5, toPosition).isBlank()) {
                                        if (command.length() > toPosition + 3 &&
                                                command.charAt(toPosition + 3) == ' ') {
                                            if (!command.substring(toPosition + 4).isBlank()) {
                                                String description = command.substring(6, fromPosition);
                                                String from = command.substring(fromPosition + 6, toPosition - 1);
                                                String to = command.substring(toPosition + 4);
                                                if (checkValidDate(from) && checkValidDate(to)) {
                                                    TaskList.addTask(new Event(description, LocalDate.parse(from),
                                                                               LocalDate.parse(to)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            throw new TakoException("Wrong format! Event command should have the form" +
                                    " 'event x /from YYYY-MM-DD /to YYYY-MM-DD', where x is not empty");
        } catch (TakoException e) {
            System.out.println(e.message());
            Ui.promptInput();
        }
    }


    /**
     * Checks whether the input is of the correct format and if so,
     * whether it is an existing date
     *
     *
     * @param date
     * @return boolean of whether the date is valid
     */
    public static boolean checkValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Changes the date from MONTH DD YYYY to a local date class
     *
     * @param date
     * @return LocalDate object
     */
    public static LocalDate changeDateToLocalDate(String date) {

        int firstSpace = date.indexOf(' ');
        String month = date.substring(0, firstSpace);
        int intMonth = Month.valueOf(month).getValue();
        String stringMonth;
        if (intMonth < 10) {
            stringMonth = "0" + Integer.toString(intMonth);
        } else {
            stringMonth = Integer.toString(intMonth);
        }
        String day = date.substring(firstSpace + 1, firstSpace + 3);
        String year = date.substring(firstSpace + 4);
        return LocalDate.parse(year + "-" + stringMonth + "-" + day);
    }
}