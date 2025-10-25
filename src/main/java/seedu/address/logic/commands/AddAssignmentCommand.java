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
import java.util.stream.Stream;

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
 * Adds assignment(s) to an existing person in the address book.
 */
public class AddAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "assign";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add Assignment(s) to the person identified "
            + "by the index number used in the displayed person list. \n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_CLASSGROUP + "CLASS_GROUP "
            + PREFIX_ASSIGNMENT + "ASSIGNMENT "
            + "[" + PREFIX_ASSIGNMENT + "ASSIGNMENT]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_CLASSGROUP + "Math 3PM "
            + PREFIX_ASSIGNMENT + "ScienceTopic2 "
            + PREFIX_ASSIGNMENT + "MathHW1";

    public static final String MESSAGE_SUCCESS = "Added assignment(s) to: %1$s";
    public static final String MESSAGE_ASSIGNMENT_NOT_ADDED = "At least one assignment to add must be provided.";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "Duplicate assignment(s): %s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_STUDENT_NOT_IN_CLASS_GROUP = "Student does not belong to the class group: %s";
    private final Index index;
    private final AddAssignmentDescriptor addAssignmentDescriptor;

    /**
     * @param index of the person in the filtered person list to add assignment to
     * @param addAssignmentDescriptor details to add assignment to the person with
     */
    public AddAssignmentCommand(Index index, AddAssignmentDescriptor addAssignmentDescriptor) {
        requireNonNull(index);
        requireNonNull(addAssignmentDescriptor);

        this.index = index;
        this.addAssignmentDescriptor = new AddAssignmentDescriptor(addAssignmentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (addAssignmentDescriptor.getAssignments().isEmpty()
                || addAssignmentDescriptor.getAssignments().get().isEmpty()) {
            throw new CommandException(MESSAGE_ASSIGNMENT_NOT_ADDED);
        }

        // Validate that the student belongs to the specified class group(s)
        validateStudentClassGroups(personToEdit, addAssignmentDescriptor);

        Set<Assignment> duplicates = findDuplicateAssignments(personToEdit, addAssignmentDescriptor);
        if (!duplicates.isEmpty()) {
            String duplicateNames = duplicates.stream()
                    .map(Assignment::toString)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_DUPLICATE_ASSIGNMENT, duplicateNames));
        }


        Person editedPerson = createEditedPerson(personToEdit, addAssignmentDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Validates that the student belongs to all class groups specified in the assignments.
     *
     * @param person The person to validate
     * @param desc The descriptor containing the assignments to add
     * @throws CommandException if the student does not belong to any of the specified class groups
     */
    private static void validateStudentClassGroups(Person person, AddAssignmentDescriptor desc)
            throws CommandException {
        Set<Assignment> newAssignments = desc.getAssignments().orElse(Set.of());
        Set<String> personClassGroupNames = person.getClassGroups().stream()
                .map(ClassGroup::getClassGroupName)
                .collect(Collectors.toSet());

        for (Assignment assignment : newAssignments) {
            if (!personClassGroupNames.contains(assignment.classGroupName)) {
                throw new CommandException(String.format(MESSAGE_STUDENT_NOT_IN_CLASS_GROUP,
                        assignment.classGroupName));
            }
        }
    }

    /**
     * Finds and returns the set of duplicate assignments between the given {@code Person}'s
     * existing assignments and those specified in the {@code AddAssignmentDescriptor}.
     */
    private static Set<Assignment> findDuplicateAssignments(Person person, AddAssignmentDescriptor desc) {
        Set<Assignment> newAssignments = desc.getAssignments().orElse(Set.of());
        // Intersect: existing ∩ new
        return person.getAssignments().stream()
                .filter(newAssignments::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code addAssignmentDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, AddAssignmentDescriptor addAssignmentDescriptor) {
        assert personToEdit != null;

        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Level updatedLevel = personToEdit.getLevel();
        Set<ClassGroup> updatedClassGroup = personToEdit.getClassGroups();
        Set<Assignment> updatedAssignments = Stream.concat(
                personToEdit.getAssignments().stream(),
                addAssignmentDescriptor.getAssignments().orElse(Set.of()).stream()
        ).collect(Collectors.toSet());


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
        if (!(other instanceof AddAssignmentCommand)) {
            return false;
        }

        AddAssignmentCommand otherAddAssignmentCommand = (AddAssignmentCommand) other;
        return index.equals(otherAddAssignmentCommand.index)
                && addAssignmentDescriptor.equals(otherAddAssignmentCommand.addAssignmentDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("addAssignmentDescriptor", addAssignmentDescriptor)
                .toString();
    }

    /**
     * Stores the details to allocate assignment to the person with. All other fields will remain unchanged.
     */
    public static class AddAssignmentDescriptor {
        private Set<Assignment> assignments;

        public AddAssignmentDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code assignments} is used internally.
         */
        public AddAssignmentDescriptor(AddAssignmentDescriptor toCopy) {
            setAssignments(toCopy.assignments);
        }

        /**
         * Returns true if at least one assignment is added.
         */
        public boolean isAssignmentAdded() {
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
            if (!(other instanceof AddAssignmentDescriptor)) {
                return false;
            }

            AddAssignmentDescriptor otherAddAssignmentDescriptor = (AddAssignmentDescriptor) other;
            return Objects.equals(assignments, otherAddAssignmentDescriptor.assignments);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("assignments", assignments)
                    .toString();
        }
    }
}
