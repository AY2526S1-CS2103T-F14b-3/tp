package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.ALREADY_MARKED;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ASSIGNMENT_IN_PERSON;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_MARK_PERSON_SUCCESS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;


/**
 * Sets one or more students' assignment status as marked
 */
public class MarkAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the assignment of student(s) identified by the index number(s) "
            + "used in the displayed student list and the assignment name.\n"
            + "Parameters: [INDEX]... [INDEX_RANGE]... (e.g., '1' for single student or '1-5' for multiple students) "
            + "c/CLASS "
            + "a/ASSIGNMENT\n"
            + "Example 1: " + COMMAND_WORD + " 1 "
            + "c/Math-2000 "
            + "a/Homework1\n"
            + "Example 2: " + COMMAND_WORD + " 1-5 "
            + "c/Math-2000 "
            + "a/Homework1\n"
            + "Example 3: " + COMMAND_WORD + " 1 3-5 7 "
            + "c/Math-2000 "
            + "a/Homework1\n";

    private static final Logger logger = LogsCenter.getLogger(MarkAssignmentCommand.class);

    private final List<Index> targetIndices;
    private final Assignment assignment;

    /**
     * Creates a MarkAssignmentCommand.
     *
     * @param targetIndices list of indices of the students in the displayed list
     * @param assignment assignment to mark
     */
    public MarkAssignmentCommand(List<Index> targetIndices, Assignment assignment) {
        this.targetIndices = targetIndices;
        this.assignment = assignment;
    }

    /**
     * Executes the mark assignment command.
     *
     * Steps:
     * 1. Validate and fetch the target person from the displayed list.
     * 2. Retrieve the person's assignment set and validate the requested assignment exists.
     * 3. Find the matching assignment instance, mark it as completed, and
     *    return a formatted success message.
     *
     * @param model the model which provides access to the displayed person list and persistence
     * @return a CommandResult containing the success message
     * @throws CommandException if the target index is invalid or the assignment is not present for the person
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> markedPersons = new ArrayList<>();
        Set<Person> alreadyMarkedPersons = new java.util.LinkedHashSet<>();
        Set<Person> peopleToMark = new java.util.LinkedHashSet<>();

        // First validate indices and collect people to mark
        for (Index targetIndex : targetIndices) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            Person personToMark = lastShownList.get(targetIndex.getZeroBased());
            Set<Assignment> personAssignments = getPersonAssignmentSet(personToMark);
            ensureAssignmentExists(personAssignments, personToMark);

            // Check if the assignment is already marked
            Assignment match = personAssignments.stream()
                    .filter(a -> a.equals(assignment))
                    .findAny()
                    .orElse(null);

            if (match != null && match.isMarked()) {
                alreadyMarkedPersons.add(personToMark);
            } else {
                peopleToMark.add(personToMark);
            }
        }

        // If all people are already marked, throw an error
        if (peopleToMark.isEmpty() && !alreadyMarkedPersons.isEmpty()) {
            throw new CommandException(ALREADY_MARKED);
        }

        // Mark assignments for people who aren't already marked
        for (Person personToMark : peopleToMark) {
            Set<Assignment> personAssignments = getPersonAssignmentSet(personToMark);
            Set<Assignment> updatedAssignments = createUpdatedAssignmentSet(personAssignments);

            // Find and mark the assignment
            Assignment match = updatedAssignments.stream()
                    .filter(a -> a.equals(assignment))
                    .findAny()
                    .orElse(null);

            if (match != null && !match.isMarked()) {
                Assignment markedAssignment = match.mark();
                updatedAssignments.remove(match);
                updatedAssignments.add(markedAssignment);

                // Create updated person with new assignments
                Person updatedPerson = personToMark.withAssignments(updatedAssignments);
                model.setPerson(personToMark, updatedPerson);
                markedPersons.add(updatedPerson);
            }
        }

        return new CommandResult(formatSuccessMessage(assignment, markedPersons));
    }

    /**
     * Returns the set of assignments for the given person.
     */
    private Set<Assignment> getPersonAssignmentSet(Person person) {
        return person.getAssignments();
    }

    /**
     * Validates that the specified `assignment` exists in the person's assignment set.
     *
     * @throws CommandException if the assignment is not present
     */
    private void ensureAssignmentExists(Set<Assignment> assignments, Person person) throws CommandException {
        if (!assignments.contains(assignment)) {
            // Log helpful debug info to aid troubleshooting
            logger.warning(() -> String.format(
                    "Person %s does not have assignment '%s'. Person assignments: %s",
                    person.getName(), assignment.toString(),
                    assignmentsToString(assignments)));
            throw new CommandException(String.format(
                    MESSAGE_INVALID_ASSIGNMENT_IN_PERSON, assignment.getAssignmentName())
            );
        }
    }

    /**
     * Returns a compact string representation of the given assignments set for logging.
     */
    private String assignmentsToString(Set<Assignment> assignments) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<Assignment> it = assignments.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a new mutable set of assignments with the same contents as the input set.
     */
    private Set<Assignment> createUpdatedAssignmentSet(Set<Assignment> originalAssignments) {
        return new java.util.HashSet<>(originalAssignments);
    }

    /**
     * Finds the matching Assignment instance in the given set and marks it.
     *
     * @param assignments the set of assignments to search
     * @return the Assignment instance that was marked, or null if none matched (should not happen when validated)
     * @throws CommandException if the assignment is already marked
     */
    private Assignment findAndMarkAssignment(Set<Assignment> assignments) throws CommandException {
        Assignment match = assignments.stream()
                .filter(a -> a.equals(assignment))
                .findAny()
                .orElse(null);

        if (match == null) {
            // should not reach here because ensureAssignmentExists was called before
            return null;
        }

        if (match.isMarked()) {
            throw new CommandException(ALREADY_MARKED);
        }

        Assignment markedAssignment = match.mark();
        assignments.remove(match);
        assignments.add(markedAssignment);
        return markedAssignment;
    }

    /**
     * Formats the user-visible success message after marking an assignment.
     */
    private String formatSuccessMessage(Assignment a, List<Person> persons) {
        String assignmentNameTitleCase = StringUtil.toTitleCase(a.getAssignmentName());
        List<String> names = persons.stream()
                .map(p -> StringUtil.toTitleCase(p.getName().fullName))
                .collect(Collectors.toList());
        String personNames = String.join(", ", names);
        return String.format(MESSAGE_MARK_PERSON_SUCCESS, assignmentNameTitleCase, personNames);
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

        // instanceof handles nulls
        if (!(other instanceof MarkAssignmentCommand)) {
            return false;
        }

        MarkAssignmentCommand otherMarkAssignmentCommand = (MarkAssignmentCommand) other;
        return targetIndices.equals(otherMarkAssignmentCommand.targetIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .add("assignment", assignment)
                .toString();
    }
}
