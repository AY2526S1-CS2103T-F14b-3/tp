package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.ALREADY_UNMARKED;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ASSIGNMENT_IN_PERSON;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;


/**
 * Resets a student's assignment status as unmarked
 */
public class UnmarkAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks the assignment of the student identified by the index number "
            + "used in the displayed person list and the assignment name.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "c/CLASS_GROUP "
            + "a/ASSIGNMENT_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "c/Math-2000 "
            + "a/Homework1\n";

    private static final Logger logger = LogsCenter.getLogger(UnmarkAssignmentCommand.class);

    private final Index targetIndex;
    private final Assignment assignment;

    /**
     * Creates a UnmarkAssignmentCommand.
     *
     * @param targetIndex index of the student in the displayed list
     * @param assignment assignment to unmark
     */
    public UnmarkAssignmentCommand(Index targetIndex, Assignment assignment) {
        this.targetIndex = targetIndex;
        this.assignment = assignment;
    }

    /**
     * Executes the unmark assignment command.
     *
     * Steps:
     * 1. Validate and fetch the target person from the displayed list.
     * 2. Retrieve the person's assignment set and validate the requested assignment exists.
     * 3. Find the matching assignment instance, unmark it as not completed, and
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
        Person personToUnmark = getPersonToUnmark(lastShownList);

        Set<Assignment> personAssignments = getPersonAssignmentSet(personToUnmark);
        ensureAssignmentExists(personAssignments, personToUnmark);

        // Create a new mutable set with all assignments
        Set<Assignment> updatedAssignments = createUpdatedAssignmentSet(personAssignments);
        Assignment markedAssignment = findAndUnmarkAssignment(updatedAssignments);

        // Create updated person with new assignments
        Person updatedPerson = personToUnmark.withAssignments(updatedAssignments);
        model.setPerson(personToUnmark, updatedPerson);

        return new CommandResult(formatSuccessMessage(markedAssignment, updatedPerson));
    }

    /**
     * Returns the person at the configured index from the provided displayed list.
     *
     * @throws CommandException if the index is out of bounds
     */
    private Person getPersonToUnmark(List<Person> lastShownList) throws CommandException {
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(targetIndex.getZeroBased());
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
                    "Person %s (index %d) does not have assignment '%s'. Person assignments: %s",
                    person.getName(), targetIndex.getZeroBased(), assignment.toString(),
                    assignmentsToString(assignments)));
            throw new CommandException(MESSAGE_INVALID_ASSIGNMENT_IN_PERSON);
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
    private String formatSuccessMessage(Assignment a, Person p) {
        String assignmentNameTitleCase = StringUtil.toTitleCase(a.getAssignmentName());
        String personNameTitleCase = StringUtil.toTitleCase(p.getName().fullName);
        return String.format(MESSAGE_UNMARK_PERSON_SUCCESS, assignmentNameTitleCase, personNameTitleCase);
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
        return targetIndex.equals(otherUnmarkAssignmentCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("assignment", assignment)
                .toString();
    }

}
