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
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for AddAssignmentCommand.
 */
public class AddAssignmentCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addSingleAssignment_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Assignment assignment = new Assignment("Math Homework");

        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(AddAssignmentCommand.MESSAGE_ADD_ASSIGNMENT_SUCCESS,
                Messages.format(createPersonWithAddedAssignments(personToEdit, assignments)));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = createPersonWithAddedAssignments(personToEdit, assignments);
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addMultipleAssignments_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Assignment assignment1 = new Assignment("Math Homework");
        Assignment assignment2 = new Assignment("Science Project");

        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment1);
        assignments.add(assignment2);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person editedPerson = createPersonWithAddedAssignments(personToEdit, assignments);
        expectedModel.setPerson(personToEdit, editedPerson);

        String expectedMessage = String.format(AddAssignmentCommand.MESSAGE_ADD_ASSIGNMENT_SUCCESS,
                Messages.format(editedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Assignment assignment = new Assignment("Math Homework");

        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        AddAssignmentCommand command = new AddAssignmentCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_noAssignmentProvided_throwsCommandException() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, AddAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_ADDED);
    }

    @Test
    public void execute_emptyAssignmentSet_throwsCommandException() {
        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(new HashSet<>());

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(command, model, AddAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_ADDED);
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() throws CommandException {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Assignment assignment = new Assignment("Math Homework");

        // First, add the assignment
        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);
        command.execute(model);

        // Try to add the same assignment again
        AddAssignmentCommand duplicateCommand = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(duplicateCommand, model,
                String.format(AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, "Math Homework"));
    }

    @Test
    public void execute_partialDuplicateAssignments_throwsCommandException() throws CommandException {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Assignment assignment1 = new Assignment("Math Homework");

        // Add first assignment
        Set<Assignment> firstSet = new HashSet<>();
        firstSet.add(assignment1);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor1 =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor1.setAssignments(firstSet);

        AddAssignmentCommand command1 = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor1);
        command1.execute(model);

        // Try to add both existing and new assignment
        Assignment assignment2 = new Assignment("Science Project");
        Set<Assignment> secondSet = new HashSet<>();
        secondSet.add(assignment1); // duplicate
        secondSet.add(assignment2); // new

        AddAssignmentCommand.AddAssignmentDescriptor descriptor2 =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor2.setAssignments(secondSet);

        AddAssignmentCommand command2 = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor2);

        assertCommandFailure(command2, model,
                String.format(AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT, "Math Homework"));
    }

    @Test
    public void equals() {
        Assignment assignment1 = new Assignment("Math Homework");
        Assignment assignment2 = new Assignment("Science Project");

        Set<Assignment> set1 = new HashSet<>();
        set1.add(assignment1);

        Set<Assignment> set2 = new HashSet<>();
        set2.add(assignment2);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor1 =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor1.setAssignments(set1);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor2 =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor2.setAssignments(set2);

        AddAssignmentCommand command1 = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor1);
        AddAssignmentCommand command2 = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor1);
        AddAssignmentCommand command3 = new AddAssignmentCommand(Index.fromOneBased(2), descriptor1);
        AddAssignmentCommand command4 = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor2);

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

    @Test
    public void toStringMethod() {
        Assignment assignment = new Assignment("Math Homework");
        Set<Assignment> assignments = new HashSet<>();
        assignments.add(assignment);

        AddAssignmentCommand.AddAssignmentDescriptor descriptor =
                new AddAssignmentCommand.AddAssignmentDescriptor();
        descriptor.setAssignments(assignments);

        AddAssignmentCommand command = new AddAssignmentCommand(INDEX_FIRST_PERSON, descriptor);

        String expected = AddAssignmentCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON
                + ", addAssignmentDescriptor=" + descriptor + "}";

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
}
