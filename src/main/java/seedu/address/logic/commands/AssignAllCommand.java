package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.classgroup.ClassGroup;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Assigns an assignment to all students in a specified class group.
 */
public class AssignAllCommand extends Command {

    public static final String COMMAND_WORD = "assignall";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns an assignment to all students in the specified class group.\n"
            + "Parameters: "
            + PREFIX_CLASSGROUP + "CLASS_GROUP "
            + PREFIX_ASSIGNMENT + "ASSIGNMENT\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CLASSGROUP + "Math 3PM "
            + PREFIX_ASSIGNMENT + "Homework1";

    public static final String MESSAGE_SUCCESS = "Assigned assignment '%1$s' to %2$d student(s) in class '%3$s'";
    public static final String MESSAGE_NO_STUDENTS_FOUND = "No students found in class group: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    public static final String MESSAGE_ALREADY_ASSIGNED =
            "All students in class '%1$s' already have the assignment '%2$s' assigned";

    private static final Logger logger = LogsCenter.getLogger(AssignAllCommand.class);

    private final String classGroupName;
    private final Assignment assignment;

    /**
     * Creates an AssignAllCommand to assign the specified assignment to all students
     * in the specified class group.
     *
     * @param classGroupName The name of the class group to filter students by.
     * @param assignment The assignment to be assigned to all students in the class group.
     */
    public AssignAllCommand(String classGroupName, Assignment assignment) {
        requireNonNull(classGroupName);
        requireNonNull(assignment);
        this.classGroupName = classGroupName;
        this.assignment = assignment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing AssignAllCommand for class group: " + classGroupName
                + ", assignment: " + assignment.getAssignmentName());

        List<Person> fullList = model.getAddressBook().getPersonList();

        // Filter students who have the specified class group
        List<Person> studentsInClass = fullList.stream()
                .filter(person -> hasClassGroup(person, classGroupName))
                .collect(Collectors.toList());

        logger.fine(() -> String.format("Found %d student(s) in class group '%s'",
                studentsInClass.size(), classGroupName));

        if (studentsInClass.isEmpty()) {
            logger.warning("No students found in class group: " + classGroupName);
            throw new CommandException(String.format(MESSAGE_NO_STUDENTS_FOUND,
                    StringUtil.toTitleCase(classGroupName)));
        }

        int assignedCount = 0;
        // Assign the assignment to each student in the class
        for (Person person : studentsInClass) {
            // Check if the person already has this assignment
            if (!person.getAssignments().contains(assignment)) {
                Person editedPerson = createPersonWithAssignment(person, assignment);

                if (!person.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
                    logger.severe(() -> String.format("Duplicate person detected while assigning to %s",
                            person.getName()));
                    throw new CommandException(MESSAGE_DUPLICATE_PERSON);
                }

                model.setPerson(person, editedPerson);
                assignedCount++;
                logger.fine(() -> String.format("Assigned '%s' to %s",
                        assignment, person.getName()));
            } else {
                logger.fine(() -> String.format("%s already has assignment '%s'",
                        person.getName(), assignment));
            }
        }

        // If all students already have the assignment, output error message
        if (assignedCount == 0) {
            logger.warning(() -> String.format(
                    "All students in class '%s' already have assignment '%s'",
                    classGroupName, assignment));
            throw new CommandException(String.format(MESSAGE_ALREADY_ASSIGNED,
                    StringUtil.toTitleCase(classGroupName), StringUtil.toTitleCase(assignment.getAssignmentName())));
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        final int finalAssignedCount = assignedCount;
        logger.info(() -> String.format("Successfully assigned '%s' to %d student(s) in class '%s'",
                assignment.getAssignmentName(), finalAssignedCount, classGroupName));

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                assignment.getAssignmentName(), assignedCount, classGroupName));
    }

    /**
     * Checks if the person has a class group with the specified name.
     *
     * @param person The person to check.
     * @param classGroupName The name of the class group to look for.
     * @return true if the person has a class group with the specified name, false otherwise.
     */
    private boolean hasClassGroup(Person person, String classGroupName) {
        return person.getClassGroups().stream()
                .map(ClassGroup::getClassGroupName)
                .anyMatch(name -> name.equalsIgnoreCase(classGroupName));
    }

    /**
     * Creates and returns a {@code Person} with the assignment added to their existing assignments.
     *
     * @param person The original person.
     * @param assignment The assignment to add.
     * @return A new Person with the assignment added.
     */
    private static Person createPersonWithAssignment(Person person, Assignment assignment) {
        assert person != null;

        Name name = person.getName();
        Phone phone = person.getPhone();
        Level level = person.getLevel();
        Set<ClassGroup> classGroups = person.getClassGroups();
        Set<Assignment> updatedAssignments = Stream.concat(
                person.getAssignments().stream(),
                Stream.of(assignment)
        ).collect(Collectors.toSet());

        return new Person(name, phone, level, classGroups, updatedAssignments);
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignAllCommand)) {
            return false;
        }

        AssignAllCommand otherCommand = (AssignAllCommand) other;
        return classGroupName.equals(otherCommand.classGroupName)
                && assignment.equals(otherCommand.assignment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classGroupName", classGroupName)
                .add("assignment", assignment)
                .toString();
    }
}
