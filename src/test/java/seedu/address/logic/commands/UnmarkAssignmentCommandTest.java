package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;
import seedu.address.testutil.AssignmentBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code UnmarkAssignmentCommand}.
 * Tests include scenarios for successful unmarking, handling of invalid index, absence of the specified assignment,
 * and equality checks.
 */
public class UnmarkAssignmentCommandTest {

    /**
     * Tests that executing a valid UnmarkAssignmentCommand unmarks the assignment and updates the person's assignments.
     */
    @Test
    public void execute_validAssignment_success() {
        // Prepare a typical model and a person with the assignment to be unmarked
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();
        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        // Replace the original person with the modified one containing the assignment
        model.setPerson(originalPerson, personWithAssignment);

        Index targetIndex = Index.fromOneBased(1);

        MarkAssignmentCommand markCommand = new MarkAssignmentCommand(targetIndex, assignment);
        try {
            markCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup for unmarking failed.", ce);
        }

        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);
        try {
            var result = command.execute(model);
            String expectedMessage = String.format(seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    StringUtil.toTitleCase(personWithAssignment.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Tests that execution fails when the target index is invalid.
     */
    @Test
    public void execute_invalidPersonIndex_failure() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(outOfBoundsIndex, assignment);

        assertCommandFailure(command, model, seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests that execution fails when the specified assignment is not present in the person's assignment set.
     */
    @Test
    public void execute_assignmentNotPresent_failure() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        // Retrieve first person and ensure the assignment "Physics-1800" is not present
        Person person = model.getFilteredPersonList().get(0);
        String[] remainingAssignments = person.getAssignments().stream()
                .map(a -> a.getAssignmentName())
                .filter(name -> !name.equals("Physics-1800"))
                .toArray(String[]::new);
        Person updatedPerson = new PersonBuilder(person).withAssignmentsUsingDefaultClass(remainingAssignments).build();
        model.setPerson(person, updatedPerson);

        Assignment assignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup("default-class")
                .build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);

        assertCommandFailure(command, model, seedu.address.logic.Messages.MESSAGE_INVALID_ASSIGNMENT_IN_PERSON);
    }

    /**
     * Tests the {@code equals} method of {@code UnmarkAssignmentCommand}.
     */
    @Test
    public void equals() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command1 = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);
        UnmarkAssignmentCommand command2 = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);
        UnmarkAssignmentCommand command3 = new UnmarkAssignmentCommand(Index.fromOneBased(2), assignment);

        // same object -> returns true
        assertEquals(command1, command1);

        // same values -> returns true
        assertEquals(command1, command2);

        // different index -> returns false
        if (command1.equals(command3)) {
            throw new AssertionError("Commands with different indices should not be equal");
        }
    }

    /**
     * Helper method to assert that a command execution fails with the expected message.
     *
     * @param command the command to execute
     * @param model the model on which the command is executed
     * @param expectedMessage the expected error message
     */
    private void assertCommandFailure(UnmarkAssignmentCommand command, Model model, String expectedMessage) {
        try {
            command.execute(model);
            throw new AssertionError("Expected a CommandException to be thrown.");
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
        }
    }

    /**
     * Tests that unmarking an assignment that is already unmarked throws an exception.
     */
    @Test
    public void execute_assignmentAlreadyUnmarked_throwsCommandException() {
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();
        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithUnmarkedAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        model.setPerson(originalPerson, personWithUnmarkedAssignment);

        Index targetIndex = Index.fromOneBased(1);
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);

        assertCommandFailure(command, model, seedu.address.logic.Messages.ALREADY_UNMARKED);
    }

    /**
     * Tests that unmarking multiple times fails after the first unmark.
     */
    @Test
    public void execute_unmarkTwice_secondUnmarkFails() {
        String classGroup = "test-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Math-2000")
                .withClassGroup(classGroup)
                .build();
        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        model.setPerson(originalPerson, personWithAssignment);

        Index targetIndex = Index.fromOneBased(1);

        // First mark the assignment
        MarkAssignmentCommand markCommand = new MarkAssignmentCommand(targetIndex, assignment);
        try {
            markCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup marking failed.", ce);
        }

        // First unmark should succeed
        UnmarkAssignmentCommand unmarkCommand = new UnmarkAssignmentCommand(targetIndex, assignment);
        try {
            unmarkCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("First unmark should not fail.", ce);
        }

        // Second unmark should fail
        UnmarkAssignmentCommand secondUnmarkCommand = new UnmarkAssignmentCommand(targetIndex, assignment);
        assertCommandFailure(secondUnmarkCommand, model, seedu.address.logic.Messages.ALREADY_UNMARKED);
    }

    /**
     * Tests the getCommandWord method.
     */
    @Test
    public void getCommandWord_returnsCorrectWord() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);
        assertEquals("unmark", command.getCommandWord());
    }

    /**
     * Tests the toString method.
     */
    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);

        String expected = UnmarkAssignmentCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex
                + ", assignment=" + assignment + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Tests equals with same assignment but different index.
     */
    @Test
    public void equals_sameAssignmentDifferentIndex_returnsFalse() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command1 = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);
        UnmarkAssignmentCommand command2 = new UnmarkAssignmentCommand(Index.fromOneBased(2), assignment);

        assertFalse(command1.equals(command2));
    }

    /**
     * Tests equals with different assignment same index.
     * Note: The equals method only compares targetIndex, not assignment.
     */
    @Test
    public void equals_differentAssignmentSameIndex_returnsTrue() {
        Assignment assignment1 = new AssignmentBuilder().withName("Physics-1800").build();
        Assignment assignment2 = new AssignmentBuilder().withName("Math-2000").build();
        UnmarkAssignmentCommand command1 = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment1);
        UnmarkAssignmentCommand command2 = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment2);

        // The equals implementation only compares index, not assignment
        assertTrue(command1.equals(command2));
    }

    /**
     * Tests equals with null.
     */
    @Test
    public void equals_null_returnsFalse() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);

        assertFalse(command.equals(null));
    }

    /**
     * Tests equals with different class type.
     */
    @Test
    public void equals_differentType_returnsFalse() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(Index.fromOneBased(1), assignment);

        assertFalse(command.equals("not a command"));
    }

    /**
     * Tests unmarking with a person who has multiple assignments.
     */
    @Test
    public void execute_personWithMultipleAssignments_success() {
        String classGroup = "default-class";
        Assignment assignment1 = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();
        Assignment assignment2 = new AssignmentBuilder()
                .withName("Math-2000")
                .withClassGroup(classGroup)
                .build();

        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithAssignments = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment1.getAssignmentName(), assignment2.getAssignmentName())
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        model.setPerson(originalPerson, personWithAssignments);

        Index targetIndex = Index.fromOneBased(1);

        // Mark both assignments
        MarkAssignmentCommand markCommand1 = new MarkAssignmentCommand(targetIndex, assignment1);
        MarkAssignmentCommand markCommand2 = new MarkAssignmentCommand(targetIndex, assignment2);
        try {
            markCommand1.execute(model);
            markCommand2.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup for unmarking failed.", ce);
        }

        // Unmark only the first assignment
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment1);
        try {
            var result = command.execute(model);
            String expectedMessage = String.format(seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment1.getAssignmentName()),
                    StringUtil.toTitleCase(personWithAssignments.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Tests that unmarking at the boundary index (first person) works.
     */
    @Test
    public void execute_firstPersonIndex_success() {
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Chemistry-1500")
                .withClassGroup(classGroup)
                .build();
        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        model.setPerson(originalPerson, personWithAssignment);

        Index targetIndex = Index.fromOneBased(1);

        // Mark the assignment first
        MarkAssignmentCommand markCommand = new MarkAssignmentCommand(targetIndex, assignment);
        try {
            markCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup for unmarking failed.", ce);
        }

        // Unmark should succeed
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);
        try {
            var result = command.execute(model);
            String expectedMessage = String.format(seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    StringUtil.toTitleCase(personWithAssignment.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Tests that unmarking at the boundary index (last person) works.
     */
    @Test
    public void execute_lastPersonIndex_success() {
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Biology-1200")
                .withClassGroup(classGroup)
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        int lastIndex = model.getFilteredPersonList().size();
        Person originalPerson = model.getFilteredPersonList().get(lastIndex - 1);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();
        model.setPerson(originalPerson, personWithAssignment);

        Index targetIndex = Index.fromOneBased(lastIndex);

        // Mark the assignment first
        MarkAssignmentCommand markCommand = new MarkAssignmentCommand(targetIndex, assignment);
        try {
            markCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup for unmarking failed.", ce);
        }

        // Unmark should succeed
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);
        try {
            var result = command.execute(model);
            String expectedMessage = String.format(seedu.address.logic.Messages.MESSAGE_UNMARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    StringUtil.toTitleCase(personWithAssignment.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Tests that the success message contains title-cased names.
     */
    @Test
    public void execute_successMessage_containsTitleCasedNames() {
        String classGroup = "default-class";
        String lowercaseAssignmentName = "homework-assignment-1";
        Assignment assignment = new AssignmentBuilder()
                .withName(lowercaseAssignmentName)
                .withClassGroup(classGroup)
                .build();

        Person originalPerson = TypicalPersons.getTypicalAddressBook().getPersonList().get(0);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, lowercaseAssignmentName)
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        model.setPerson(originalPerson, personWithAssignment);

        Index targetIndex = Index.fromOneBased(1);

        // Mark first
        MarkAssignmentCommand markCommand = new MarkAssignmentCommand(targetIndex, assignment);
        try {
            markCommand.execute(model);
        } catch (CommandException ce) {
            throw new AssertionError("Setup for unmarking failed.", ce);
        }

        // Unmark and verify message contains title-cased names
        UnmarkAssignmentCommand command = new UnmarkAssignmentCommand(targetIndex, assignment);
        try {
            var result = command.execute(model);
            String message = result.getFeedbackToUser();
            assertTrue(message.contains(StringUtil.toTitleCase(lowercaseAssignmentName)));
            assertTrue(message.contains(StringUtil.toTitleCase(personWithAssignment.getName().fullName)));
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }
}
