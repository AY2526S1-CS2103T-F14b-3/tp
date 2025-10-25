package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;
import seedu.address.testutil.AssignmentBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for AddAssignmentCommand.
 */
public class AddAssignmentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests successful addition of an assignment to a person in an unfiltered list.
     * Verifies that the command correctly updates the person with the new assignment
     * and produces the expected success message.
     */
    @Test
    public void execute_addAssignment_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Ensure the person has the class group that the assignment belongs to
        String classGroup = "test-class";
        Person personWithClassGroup = new PersonBuilder(personToEdit)
                .withClassGroups(classGroup)
                .build();
        model.setPerson(personToEdit, personWithClassGroup);

        Assignment validAssignment = new AssignmentBuilder()
                .withName("HW1")
                .withClassGroup(classGroup)
                .build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(validAssignment));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = new PersonBuilder(personWithClassGroup)
                .withAssignments(classGroup, validAssignment.getAssignmentName())
                .build();
        expectedModel.setPerson(personWithClassGroup, editedPerson);

        String expectedMessage = String.format(AddAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(editedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that attempting to add a duplicate assignment to a person fails.
     * Verifies that the command throws an exception with the appropriate error message
     * when trying to add an assignment that already exists for the person.
     */
    @Test
    public void execute_duplicateAssignment_failure() {
        String classGroup = "default-class";
        Assignment duplicate = new AssignmentBuilder()
                .withName("EXISTING")
                .withClassGroup(classGroup)
                .build();

        Person original = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Add the class group and assignment to the person
        Person withAssignment = new PersonBuilder(original)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, duplicate.getAssignmentName())
                .build();
        model.setPerson(original, withAssignment);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(duplicate));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(command, model, String.format(
                AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, duplicate));
    }

    /**
     * Tests that attempting to add an assignment with a class group the student doesn't belong to fails.
     * Verifies that the command throws an exception when the student is not enrolled in the
     * class group specified for the assignment.
     */
    @Test
    public void execute_studentNotInClassGroup_failure() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        // Create an assignment with a class group the student doesn't have
        Assignment assignmentWithInvalidClass = new AssignmentBuilder()
                .withName("HW1")
                .withClassGroup("nonexistent-class")
                .build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignmentWithInvalidClass));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, String.format(
                AddAssignmentCommand.MESSAGE_STUDENT_NOT_IN_CLASS_GROUP, "nonexistent-class"));
    }

    /**
     * Tests that attempting to add an assignment to an invalid person index fails.
     * Verifies that the command throws an exception when the specified index is
     * out of bounds of the filtered person list.
     */
    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Assignment assignment = new AssignmentBuilder().withName("HW-OUT").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));
        AddAssignmentCommand command = new AddAssignmentCommand(outOfBounds, descriptor);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests the equals method of AddAssignmentCommand.
     * Verifies that commands with the same index and assignment descriptor are equal,
     * and that commands with different indices or descriptors are not equal.
     */
    @Test
    public void equals() {
        Assignment a1 = new AssignmentBuilder().withName("A1").build();
        Assignment a2 = new AssignmentBuilder().withName("A2").build();

        AddAssignmentCommand.AddAssignmentDescriptor desc1 = new AddAssignmentCommand.AddAssignmentDescriptor();
        desc1.setAssignments(Set.of(a1));
        AddAssignmentCommand.AddAssignmentDescriptor desc1Copy = new AddAssignmentCommand.AddAssignmentDescriptor();
        desc1Copy.setAssignments(Set.of(a1));
        AddAssignmentCommand.AddAssignmentDescriptor desc2 = new AddAssignmentCommand.AddAssignmentDescriptor();
        desc2.setAssignments(Set.of(a2));

        AddAssignmentCommand addA1First = new AddAssignmentCommand(INDEX_FIRST_PERSON, desc1);
        AddAssignmentCommand addA1FirstCopy = new AddAssignmentCommand(INDEX_FIRST_PERSON, desc1Copy);
        AddAssignmentCommand addA2First = new AddAssignmentCommand(INDEX_FIRST_PERSON, desc2);
        AddAssignmentCommand addA1Second = new AddAssignmentCommand(INDEX_SECOND_PERSON, desc1);

        // same object -> true
        assertTrue(addA1First.equals(addA1First));

        // same values -> true
        assertTrue(addA1First.equals(addA1FirstCopy));

        // different assignment -> false
        assertFalse(addA1First.equals(addA2First));

        // different index -> false
        assertFalse(addA1First.equals(addA1Second));

        // null -> false
        assertFalse(addA1First.equals(null));
    }

    /**
     * Tests the toString method of AddAssignmentCommand.
     * Verifies that the string representation contains the command's index
     * and assignment descriptor in the expected format.
     */
    @Test
    public void toStringMethod() {
        Assignment assignment = new AssignmentBuilder().withName("HW1").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        String expected = AddAssignmentCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON
                + ", addAssignmentDescriptor=" + descriptor + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Tests the toString method of AddAssignmentDescriptor.
     * Verifies that the string representation contains the descriptor's
     * assignments in the expected format.
     */
    @Test
    public void descriptorToStringMethod() {
        Assignment assignment = new AssignmentBuilder().withName("Math-HW").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));

        String expected = AddAssignmentCommand.AddAssignmentDescriptor.class.getCanonicalName()
                + "{assignments=" + Set.of(assignment) + "}";
        assertEquals(expected, descriptor.toString());
    }

    /**
     * Tests that isAssignmentAdded returns true when assignments are present.
     * Verifies that the descriptor correctly indicates when at least one
     * assignment has been added.
     */
    @Test
    public void descriptorIsAssignmentAdded_withAssignments_returnsTrue() {
        Assignment assignment = new AssignmentBuilder().withName("Quiz1").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));

        assertTrue(descriptor.isAssignmentAdded());
    }

    /**
     * Tests that isAssignmentAdded returns false when no assignments are present.
     * Verifies that the descriptor correctly indicates when no assignments
     * have been added.
     */
    @Test
    public void descriptorIsAssignmentAdded_noAssignments_returnsFalse() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        assertFalse(descriptor.isAssignmentAdded());
    }

    /**
     * Tests that executing a command with no assignments provided fails.
     * Verifies that the command throws an exception with the appropriate error message
     * when an empty assignment descriptor is used.
     */
    @Test
    public void execute_noAssignmentsProvided_failure() {
        AddAssignmentCommand.AddAssignmentDescriptor emptyDescriptor = new AddAssignmentCommand
                .AddAssignmentDescriptor();
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, emptyDescriptor);

        assertCommandFailure(command, model, AddAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_ADDED);
    }

    /**
     * Tests that executing a command with empty assignment set fails.
     * Verifies that the command throws an exception when the assignment set is explicitly empty.
     */
    @Test
    public void execute_emptyAssignmentSet_failure() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of());
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, AddAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_ADDED);
    }

    /**
     * Tests the copy constructor of AddAssignmentDescriptor.
     * Verifies that the copy constructor creates a proper defensive copy.
     */
    @Test
    public void descriptorCopyConstructor_validDescriptor_success() {
        Assignment assignment = new AssignmentBuilder().withName("Test-HW").build();
        AddAssignmentCommand.AddAssignmentDescriptor original = new AddAssignmentCommand.AddAssignmentDescriptor();
        original.setAssignments(Set.of(assignment));

        AddAssignmentCommand.AddAssignmentDescriptor copy = new AddAssignmentCommand.AddAssignmentDescriptor(original);

        assertEquals(original, copy);
        assertTrue(original.getAssignments().equals(copy.getAssignments()));
    }

    /**
     * Tests the copy constructor with null assignments.
     * Verifies that the copy constructor handles null assignments properly.
     */
    @Test
    public void descriptorCopyConstructor_nullAssignments_success() {
        AddAssignmentCommand.AddAssignmentDescriptor original = new AddAssignmentCommand.AddAssignmentDescriptor();
        AddAssignmentCommand.AddAssignmentDescriptor copy = new AddAssignmentCommand.AddAssignmentDescriptor(original);

        assertEquals(original, copy);
        assertFalse(copy.getAssignments().isPresent());
    }

    /**
     * Tests the getAssignments method returns an unmodifiable set.
     * Verifies that attempting to modify the returned set throws an exception.
     */
    @Test
    public void descriptorGetAssignments_returnsUnmodifiableSet() {
        Assignment assignment = new AssignmentBuilder().withName("Protected-HW").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));

        Set<Assignment> assignments = descriptor.getAssignments().get();

        // Attempting to modify should throw UnsupportedOperationException
        try {
            assignments.add(new AssignmentBuilder().withName("NewHW").build());
            // If we reach here, the test should fail
            assertTrue(false, "Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected behavior
            assertTrue(true);
        }
    }

    /**
     * Tests the getAssignments method when assignments is null.
     * Verifies that Optional.empty() is returned when no assignments are set.
     */
    @Test
    public void descriptorGetAssignments_nullAssignments_returnsEmpty() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        assertFalse(descriptor.getAssignments().isPresent());
    }

    /**
     * Tests descriptor equals method with same object.
     * Verifies reflexive property of equals.
     */
    @Test
    public void descriptorEquals_sameObject_returnsTrue() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        assertTrue(descriptor.equals(descriptor));
    }

    /**
     * Tests descriptor equals method with null.
     * Verifies that equals returns false when compared to null.
     */
    @Test
    public void descriptorEquals_null_returnsFalse() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        assertFalse(descriptor.equals(null));
    }

    /**
     * Tests descriptor equals method with different type.
     * Verifies that equals returns false when compared to a different type.
     */
    @Test
    public void descriptorEquals_differentType_returnsFalse() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        assertFalse(descriptor.equals("string"));
    }

    /**
     * Tests descriptor equals method with different assignments.
     * Verifies that descriptors with different assignments are not equal.
     */
    @Test
    public void descriptorEquals_differentAssignments_returnsFalse() {
        Assignment a1 = new AssignmentBuilder().withName("HW1").build();
        Assignment a2 = new AssignmentBuilder().withName("HW2").build();

        AddAssignmentCommand.AddAssignmentDescriptor desc1 = new AddAssignmentCommand.AddAssignmentDescriptor();
        desc1.setAssignments(Set.of(a1));

        AddAssignmentCommand.AddAssignmentDescriptor desc2 = new AddAssignmentCommand.AddAssignmentDescriptor();
        desc2.setAssignments(Set.of(a2));

        assertFalse(desc1.equals(desc2));
    }

    /**
     * Tests descriptor equals with both having null assignments.
     * Verifies that two descriptors with null assignments are equal.
     */
    @Test
    public void descriptorEquals_bothNullAssignments_returnsTrue() {
        AddAssignmentCommand.AddAssignmentDescriptor desc1 = new AddAssignmentCommand.AddAssignmentDescriptor();
        AddAssignmentCommand.AddAssignmentDescriptor desc2 = new AddAssignmentCommand.AddAssignmentDescriptor();

        assertTrue(desc1.equals(desc2));
    }

    /**
     * Tests command equals with different object type.
     * Verifies that command equals returns false for non-AddAssignmentCommand objects.
     */
    @Test
    public void equals_differentType_returnsFalse() {
        Assignment assignment = new AssignmentBuilder().withName("HW1").build();
        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertFalse(command.equals("string"));
    }

    /**
     * Tests adding multiple assignments at once.
     * Verifies that multiple assignments can be added in a single command.
     */
    @Test
    public void execute_addMultipleAssignments_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String classGroup = "multi-test-class";
        Person personWithClassGroup = new PersonBuilder(personToEdit)
                .withClassGroups(classGroup)
                .build();
        model.setPerson(personToEdit, personWithClassGroup);

        Assignment assignment1 = new AssignmentBuilder()
                .withName("HW1")
                .withClassGroup(classGroup)
                .build();
        Assignment assignment2 = new AssignmentBuilder()
                .withName("HW2")
                .withClassGroup(classGroup)
                .build();

        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(assignment1, assignment2));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = new PersonBuilder(personWithClassGroup)
                .withAssignments(classGroup, "HW1", "HW2")
                .build();
        expectedModel.setPerson(personWithClassGroup, editedPerson);

        String expectedMessage = String.format(AddAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(editedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests adding assignments when some are duplicates.
     * Verifies that the command fails when any assignment is a duplicate.
     */
    @Test
    public void execute_multipleDuplicateAssignments_failure() {
        String classGroup = "dup-test-class";
        Assignment existing1 = new AssignmentBuilder()
                .withName("EXISTING1")
                .withClassGroup(classGroup)
                .build();
        Assignment existing2 = new AssignmentBuilder()
                .withName("EXISTING2")
                .withClassGroup(classGroup)
                .build();

        Person original = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person withAssignments = new PersonBuilder(original)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, "EXISTING1", "EXISTING2")
                .build();
        model.setPerson(original, withAssignments);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor = new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(Set.of(existing1, existing2));
        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_SECOND_PERSON, descriptor);

        // Should fail with duplicate message containing both assignment names
        String expectedDuplicates = Set.of(existing1, existing2).stream()
                .map(Assignment::toString)
                .sorted()
                .collect(java.util.stream.Collectors.joining(", "));
        assertCommandFailure(command, model,
                String.format(AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, expectedDuplicates));
    }
}
