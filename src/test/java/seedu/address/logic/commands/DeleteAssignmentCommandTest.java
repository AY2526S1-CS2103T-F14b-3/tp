package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteAssignmentCommand.DeleteAssignmentDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for DeleteAssignmentCommand.
 */
public class DeleteAssignmentCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests successful deletion of a single assignment from a person.
     */
    @Test
    public void execute_deleteSingleAssignment_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // First add an assignment to delete
        Assignment assignment = new Assignment("Math Homework");
        Set<Assignment> assignmentsToAdd = new HashSet<>();
        assignmentsToAdd.add(assignment);
        Person personWithAssignment = createPersonWithAddedAssignments(personToEdit, assignmentsToAdd);
        model.setPerson(personToEdit, personWithAssignment);

        // Now delete the assignment
        Set<Assignment> assignmentsToDelete = new HashSet<>();
        assignmentsToDelete.add(assignment);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignmentsToDelete);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = createPersonWithRemovedAssignments(personWithAssignment, assignmentsToDelete);
        expectedModel.setPerson(personWithAssignment, editedPerson);

        String expectedMessage = String.format(DeleteAssignmentCommand.MESSAGE_DELETE_ASSIGNMENT_SUCCESS,
                Messages.format(editedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests successful deletion of multiple assignments from a person.
     */
    @Test
    public void execute_deleteMultipleAssignments_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Add assignments first
        Assignment assignment1 = new Assignment("Math Homework");
        Assignment assignment2 = new Assignment("Science Project");
        Set<Assignment> assignmentsToAdd = new HashSet<>();
        assignmentsToAdd.add(assignment1);
        assignmentsToAdd.add(assignment2);
        Person personWithAssignments = createPersonWithAddedAssignments(personToEdit, assignmentsToAdd);
        model.setPerson(personToEdit, personWithAssignments);

        // Delete both assignments
        Set<Assignment> assignmentsToDelete = new HashSet<>();
        assignmentsToDelete.add(assignment1);
        assignmentsToDelete.add(assignment2);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignmentsToDelete);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = createPersonWithRemovedAssignments(personWithAssignments, assignmentsToDelete);
        expectedModel.setPerson(personWithAssignments, editedPerson);

        String expectedMessage = String.format(DeleteAssignmentCommand.MESSAGE_DELETE_ASSIGNMENT_SUCCESS,
                Messages.format(editedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that command throws CommandException when index is invalid.
     */
    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Assignment assignment = new Assignment("Math Homework");

        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests that command throws CommandException when no assignment is provided.
     */
    @Test
    public void execute_noAssignmentProvided_throwsCommandException() {
        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, DeleteAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_DELETED);
    }

    /**
     * Tests that command throws CommandException when empty assignment set is provided.
     */
    @Test
    public void execute_emptyAssignmentSet_throwsCommandException() {
        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(new HashSet<>());

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, DeleteAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_DELETED);
    }

    /**
     * Tests that command throws CommandException when assignment does not exist.
     */
    @Test
    public void execute_nonExistentAssignment_throwsCommandException() {
        Assignment assignment = new Assignment("NonExistent Assignment");

        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model,
                String.format(DeleteAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_EXIST, "NonExistent Assignment"));
    }

    /**
     * Tests that command throws CommandException when trying to delete multiple assignments
     * where some don't exist.
     */
    @Test
    public void execute_partialNonExistentAssignments_throwsCommandException() throws CommandException {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Add one assignment
        Assignment existingAssignment = new Assignment("Math Homework");
        Set<Assignment> assignmentsToAdd = new HashSet<>();
        assignmentsToAdd.add(existingAssignment);
        Person personWithAssignment = createPersonWithAddedAssignments(personToEdit, assignmentsToAdd);
        model.setPerson(personToEdit, personWithAssignment);

        // Try to delete both existing and non-existent assignment
        Assignment nonExistentAssignment = new Assignment("Science Project");
        Set<Assignment> assignmentsToDelete = new HashSet<>();
        assignmentsToDelete.add(existingAssignment);
        assignmentsToDelete.add(nonExistentAssignment);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignmentsToDelete);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model,
                String.format(DeleteAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_EXIST, "Science Project"));
    }

    /**
     * Tests that deleting some but not all assignments leaves remaining assignments intact.
     */
    @Test
    public void execute_partialDeletion_remainingAssignmentsIntact() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Add three assignments
        Assignment assignment1 = new Assignment("Math Homework");
        Assignment assignment2 = new Assignment("Science Project");
        Assignment assignment3 = new Assignment("English Essay");

        Set<Assignment> assignmentsToAdd = new HashSet<>();
        assignmentsToAdd.add(assignment1);
        assignmentsToAdd.add(assignment2);
        assignmentsToAdd.add(assignment3);

        Person personWithAssignments = createPersonWithAddedAssignments(personToEdit, assignmentsToAdd);
        model.setPerson(personToEdit, personWithAssignments);

        // Delete only one assignment
        Set<Assignment> assignmentsToDelete = new HashSet<>();
        assignmentsToDelete.add(assignment1);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignmentsToDelete);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);
        command.execute(model);

        Person resultPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(resultPerson.getAssignments().contains(assignment2));
        assertTrue(resultPerson.getAssignments().contains(assignment3));
        assertFalse(resultPerson.getAssignments().contains(assignment1));
    }

    /**
     * Tests equals method with same object returns true.
     */
    @Test
    public void equals() {
        Assignment assignment1 = new Assignment("Math Homework");
        Assignment assignment2 = new Assignment("Science Project");

        Set<Assignment> set1 = new HashSet<>();
        set1.add(assignment1);

        Set<Assignment> set2 = new HashSet<>();
        set2.add(assignment2);

        DeleteAssignmentDescriptor descriptor1 = new DeleteAssignmentDescriptor();
        descriptor1.setAssignments(set1);

        DeleteAssignmentDescriptor descriptor2 = new DeleteAssignmentDescriptor();
        descriptor2.setAssignments(set2);

        DeleteAssignmentCommand command1 = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor1);
        DeleteAssignmentCommand command2 = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor1);
        DeleteAssignmentCommand command3 = new DeleteAssignmentCommand(Index.fromOneBased(2), descriptor1);
        DeleteAssignmentCommand command4 = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor2);

        // same object -> returns true
        assertTrue(command1.equals(command1));

        // same values -> returns true
        assertTrue(command1.equals(command2));

        // different index -> returns false
        assertFalse(command1.equals(command3));

        // different assignments -> returns false
        assertFalse(command1.equals(command4));

        // null -> returns false
        assertFalse(command1.equals(null));

        // different type -> returns false
        assertFalse(command1.equals(5));
    }

    /**
     * Tests toString method returns expected string format.
     */
    @Test
    public void toStringMethod() {
        Assignment assignment = new Assignment("Math Homework");
        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        DeleteAssignmentDescriptor descriptor = new DeleteAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        String expected = DeleteAssignmentCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON
                + ", deleteAssignmentDescriptor=" + descriptor + "}";

        assertEquals(expected, command.toString());
    }

    /**
     * Helper method to create a person with added assignments.
     */
    private Person createPersonWithAddedAssignments(Person person, Set<Assignment> newAssignments) {
        Set<Assignment> updatedAssignments = new HashSet<>(person.getAssignments());
        updatedAssignments.addAll(newAssignments);

        return new Person(person.getName(), person.getPhone(), person.getLevel(),
                person.getClassGroups(), updatedAssignments);
    }

    /**
     * Helper method to create a person with removed assignments.
     */
    private Person createPersonWithRemovedAssignments(Person person, Set<Assignment> assignmentsToRemove) {
        Set<Assignment> updatedAssignments = new HashSet<>(person.getAssignments());
        updatedAssignments.removeAll(assignmentsToRemove);

        return new Person(person.getName(), person.getPhone(), person.getLevel(),
                person.getClassGroups(), updatedAssignments);
    }
}
