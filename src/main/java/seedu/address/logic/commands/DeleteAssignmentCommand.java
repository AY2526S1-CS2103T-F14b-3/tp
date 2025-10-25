package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.classgroup.ClassGroup;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Deletes assignment(s) from an existing person in the address book.
 */
public class DeleteAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unassign";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Delete assignment(s) from the person identified "
            + "by the index number used in the displayed person list. \n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_CLASSGROUP + "CLASS_GROUP "
            + PREFIX_ASSIGNMENT + "ASSIGNMENT "
            + "[" + PREFIX_ASSIGNMENT + "ASSIGNMENT]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_CLASSGROUP + "Math 3PM "
            + PREFIX_ASSIGNMENT + "ScienceTopic2 "
            + PREFIX_ASSIGNMENT + "MathHW1";

    public static final String MESSAGE_DELETE_ASSIGNMENT_SUCCESS = "Deleted assignment(s) from: %1$s";
    public static final String MESSAGE_ASSIGNMENT_NOT_DELETED = "At least one assignment to delete must be provided.";
    public static final String MESSAGE_ASSIGNMENT_NOT_EXIST = "Cannot delete non-existent assignment(s): %s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final DeleteAssignmentDescriptor deleteAssignmentDescriptor;

    /**
     * @param index of the person in the filtered person list to add assignment to
     * @param deleteAssignmentDescriptor details to add assignment to the person with
     */
    public DeleteAssignmentCommand(Index index, DeleteAssignmentDescriptor deleteAssignmentDescriptor) {
        requireNonNull(index);
        requireNonNull(deleteAssignmentDescriptor);

        this.index = index;
        this.deleteAssignmentDescriptor = new DeleteAssignmentDescriptor(deleteAssignmentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (!deleteAssignmentDescriptor.isAssignmentDeleted()
                || deleteAssignmentDescriptor.getAssignments().get().isEmpty()) {
            throw new CommandException(MESSAGE_ASSIGNMENT_NOT_DELETED);
        }

        Person editedPerson = createEditedPerson(personToEdit, deleteAssignmentDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_DELETE_ASSIGNMENT_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Returns the intersection of the person's current assignments and those requested for deletion.
     */
    private static Set<Assignment> findDeletableAssignments(Person person, DeleteAssignmentDescriptor desc) {
        Set<Assignment> newAssignments = desc.getAssignments().orElse(Set.of());
        return person.getAssignments().stream()
                .filter(newAssignments::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Returns assignments requested for deletion that do not exist on the person.
     */
    private static Set<Assignment> findNonExistentAssignments(Person person, DeleteAssignmentDescriptor desc) {
        Set<Assignment> requested = desc.getAssignments().orElse(Set.of());
        Set<Assignment> existing = person.getAssignments();
        return requested.stream()
                .filter(a -> !existing.contains(a))
                .collect(Collectors.toSet());
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code deleteAssignmentDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, DeleteAssignmentDescriptor deleteAssignmentDescriptor)
            throws CommandException {

        assert personToEdit != null;

        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Level updatedLevel = personToEdit.getLevel();
        Set<ClassGroup> updatedClassGroup = personToEdit.getClassGroups();
        Set<Assignment> allAssignments = new HashSet<>(personToEdit.getAssignments());
        Set<Assignment> toDelete = findDeletableAssignments(personToEdit, deleteAssignmentDescriptor);
        Set<Assignment> nonExistent = findNonExistentAssignments(personToEdit, deleteAssignmentDescriptor);

        if (!nonExistent.isEmpty()) {
            String missingNames = nonExistent.stream()
                    .map(Assignment::toString)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_ASSIGNMENT_NOT_EXIST, missingNames));
        }

        Set<Assignment> updatedAssignments = new HashSet<>(allAssignments);
        updatedAssignments.removeAll(toDelete);

        return new Person(updatedName, updatedPhone, updatedLevel, updatedClassGroup, updatedAssignments);
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
        if (!(other instanceof DeleteAssignmentCommand)) {
            return false;
        }

        DeleteAssignmentCommand otherDeleteAssignmentCommand = (DeleteAssignmentCommand) other;
        return index.equals(otherDeleteAssignmentCommand.index)
                && deleteAssignmentDescriptor.equals(otherDeleteAssignmentCommand.deleteAssignmentDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("deleteAssignmentDescriptor", deleteAssignmentDescriptor)
                .toString();
    }

    /**
     * Stores the details of assignment(s) to delete from the person. All other fields will remain unchanged.
     */
    public static class DeleteAssignmentDescriptor {
        private Set<Assignment> assignments;

        public DeleteAssignmentDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code assignments} is used internally.
         */
        public DeleteAssignmentDescriptor(DeleteAssignmentDescriptor toCopy) {
            setAssignments(toCopy.assignments);
        }

        /**
         * Returns true if at least one assignment is set to be deleted.
         */
        public boolean isAssignmentDeleted() {
            return CollectionUtil.isAnyNonNull(assignments);
        }

        /**
         * Sets {@code assignments} to this object's {@code assignments}.
         * A defensive copy of {@code assignments} is used internally.
         */
        public void setAssignments(Set<Assignment> assignments) {
            this.assignments = (assignments != null) ? new HashSet<>(assignments) : null;
        }

        /**
         * Returns an unmodifiable assignment set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code assignments} is null.
         */
        public Optional<Set<Assignment>> getAssignments() {
            return (assignments != null) ? Optional.of(Collections.unmodifiableSet(assignments)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof DeleteAssignmentDescriptor)) {
                return false;
            }

            DeleteAssignmentDescriptor otherDeleteAssignmentDescriptor = (DeleteAssignmentDescriptor) other;
            return Objects.equals(assignments, otherDeleteAssignmentDescriptor.assignments);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("assignments", assignments)
                    .toString();
        }
    }
}
