package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.ALREADY_UNMARKED;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ASSIGNMENT_IN_PERSON;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS;

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
 * Resets one or more students' assignment status as unmarked
 */
public class UnmarkAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks the assignment of student(s) identified by the index number(s) "
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

    private static final Logger logger = LogsCenter.getLogger(UnmarkAssignmentCommand.class);

    private final List<Index> targetIndices;
    private final Assignment assignment;

    /**
     * Creates a UnmarkAssignmentCommand.
     *
     * @param targetIndices list of indices of the students in the displayed list
     * @param assignment assignment to unmark
     */
    public UnmarkAssignmentCommand(List<Index> targetIndices, Assignment assignment) {
        this.targetIndices = targetIndices;
        this.assignment = assignment;
    }

    /**
     * Executes the unmark assignment command.
     *
     * Steps:
     * 1. Validate and fetch the target persons from the displayed list.
     * 2. For each person, retrieve their assignment set and validate the requested assignment exists.
     * 3. Find the matching assignment instance, unmark it as not completed, and
     *    return a formatted success message.
     *
     * @param model the model which provides access to the displayed person list and persistence
     * @return a CommandResult containing the success message
     * @throws CommandException if any target index is invalid or the assignment is not present for any person
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> unmarkedPersons = new ArrayList<>();
        Set<Person> alreadyUnmarkedPersons = new java.util.LinkedHashSet<>();
        Set<Person> peopleToUnmark = new java.util.LinkedHashSet<>();

        // First validate indices and collect people to unmark
        for (Index targetIndex : targetIndices) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            Person personToUnmark = lastShownList.get(targetIndex.getZeroBased());
            Set<Assignment> personAssignments = getPersonAssignmentSet(personToUnmark);
            ensureAssignmentExists(personAssignments, personToUnmark);

            // Check if the assignment is already unmarked
            Assignment match = personAssignments.stream()
                    .filter(a -> a.equals(assignment))
                    .findAny()
                    .orElse(null);

            if (match != null && !match.isMarked()) {
                alreadyUnmarkedPersons.add(personToUnmark);
            } else {
                peopleToUnmark.add(personToUnmark);
            }
        }

        // If all people are already unmarked, throw an error
        if (peopleToUnmark.isEmpty() && !alreadyUnmarkedPersons.isEmpty()) {
            throw new CommandException(ALREADY_UNMARKED);
        }

        // Unmark assignments for people who aren't already unmarked
        for (Person personToUnmark : peopleToUnmark) {
            Set<Assignment> personAssignments = getPersonAssignmentSet(personToUnmark);
            Set<Assignment> updatedAssignments = createUpdatedAssignmentSet(personAssignments);

            // Find and unmark the assignment
            Assignment match = updatedAssignments.stream()
                    .filter(a -> a.equals(assignment))
                    .findAny()
                    .orElse(null);

            if (match != null && match.isMarked()) {
                Assignment unmarkedAssignment = match.unmark();
                updatedAssignments.remove(match);
                updatedAssignments.add(unmarkedAssignment);

                // Create updated person with new assignments
                Person updatedPerson = personToUnmark.withAssignments(updatedAssignments);
                model.setPerson(personToUnmark, updatedPerson);
                unmarkedPersons.add(updatedPerson);
            }
        }

        return new CommandResult(formatSuccessMessage(assignment, unmarkedPersons));
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
     * Finds the matching Assignment instance in the given set and unmarks it.
     *
     * @param assignments the set of assignments to search
     * @return the Assignment instance that was unmarked, or null if none matched (should not happen when validated)
     * @throws CommandException if the assignment is already unmarked
     */
    private Assignment findAndUnmarkAssignment(Set<Assignment> assignments) throws CommandException {
        Assignment match = assignments.stream()
                .filter(a -> a.equals(assignment))
                .findAny()
                .orElse(null);

        if (match == null) {
            // should not reach here because ensureAssignmentExists was called before
            return null;
        }

        if (!match.isMarked()) {
            throw new CommandException(ALREADY_UNMARKED);
        }

        Assignment unmarkedAssignment = match.unmark();
        assignments.remove(match);
        assignments.add(unmarkedAssignment);
        return unmarkedAssignment;
    }

    /**
     * Formats the user-visible success message after unmarking an assignment.
     */
    private String formatSuccessMessage(Assignment a, List<Person> persons) {
        String assignmentNameTitleCase = StringUtil.toTitleCase(a.getAssignmentName());
        List<String> names = persons.stream()
                .map(p -> StringUtil.toTitleCase(p.getName().fullName))
                .collect(Collectors.toList());
        String personNames = String.join(", ", names);
        return String.format(MESSAGE_UNMARK_PERSON_SUCCESS, assignmentNameTitleCase, personNames);
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
        if (!(other instanceof UnmarkAssignmentCommand)) {
            return false;
        }

        UnmarkAssignmentCommand otherUnmarkAssignmentCommand = (UnmarkAssignmentCommand) other;
        return targetIndices.equals(otherUnmarkAssignmentCommand.targetIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .add("assignment", assignment)
                .toString();
    }

}
