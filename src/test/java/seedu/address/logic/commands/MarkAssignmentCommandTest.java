package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ASSIGNMENT_IN_PERSON;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_MARK_PERSON_SUCCESS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
 * Contains integration tests (interaction with the Model) and unit tests for {@code MarkAssignmentCommand}.
 * Tests include scenarios for successful marking, handling of invalid index, absence of the specified assignment,
 * and equality checks.
 */
public class MarkAssignmentCommandTest {

    /**
     * Tests that executing a valid MarkAssignmentCommand marks the assignment and updates the person's assignments.
     */
    @Test
    public void execute_validAssignment_success() {
        // Prepare a typical model and a person with the assignment to be marked
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

        List<Index> targetIndices = Arrays.asList(Index.fromOneBased(1));
        MarkAssignmentCommand command = new MarkAssignmentCommand(targetIndices, assignment);

        try {
            var result = command.execute(model);
            String expectedMessage = String.format(
                    MESSAGE_MARK_PERSON_SUCCESS,
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
        MarkAssignmentCommand command = new MarkAssignmentCommand(Arrays.asList(outOfBoundsIndex), assignment);

        assertCommandFailure(command, model, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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
        MarkAssignmentCommand command = new MarkAssignmentCommand(Arrays.asList(Index.fromOneBased(1)), assignment);

        assertCommandFailure(command, model, String.format(
                MESSAGE_INVALID_ASSIGNMENT_IN_PERSON, assignment.getAssignmentName())
        );
    }

    /**
     * Tests the {@code equals} method of {@code MarkAssignmentCommand}.
     */
    @Test
    public void execute_multipleValidAssignments_success() {
        // Prepare multiple persons with assignments
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        List<Person> originalPersons = model.getFilteredPersonList().subList(0, 3);

        for (Person originalPerson : originalPersons) {
            Person personWithAssignment = new PersonBuilder(originalPerson)
                    .withClassGroups(classGroup)
                    .withAssignments(classGroup, assignment.getAssignmentName())
                    .build();
            model.setPerson(originalPerson, personWithAssignment);
        }

        List<Index> targetIndices = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
        );
        MarkAssignmentCommand command = new MarkAssignmentCommand(targetIndices, assignment);

        try {
            CommandResult result = command.execute(model);
            String expectedMessage = String.format(
                    MESSAGE_MARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    originalPersons.stream()
                            .map(p -> StringUtil.toTitleCase(p.getName().fullName))
                            .collect(Collectors.joining(", ")));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    @Test
    public void execute_partialInvalidIndices_failure() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        String classGroup = "default-class";

        // Create and add assignments for first three persons
        for (int i = 0; i < 3; i++) {
            Assignment assignment = new AssignmentBuilder()
                    .withName("Physics-1800")
                    .withClassGroup(classGroup)
                    .build();
            Person person = model.getFilteredPersonList().get(i);
            Person personWithAssignment = new PersonBuilder(person)
                    .withClassGroups(classGroup)
                    .withAssignments(classGroup, assignment.getAssignmentName())
                    .build();
            model.setPerson(person, personWithAssignment);
        }

        // Now try to mark with an index that's out of bounds
        Assignment commandAssignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();
        List<Index> indices = Arrays.asList(
                Index.fromOneBased(model.getFilteredPersonList().size() + 1) // Invalid index
        );
        MarkAssignmentCommand command = new MarkAssignmentCommand(indices, commandAssignment);

        assertCommandFailure(command, model, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests that duplicate indices are handled correctly - only marks each person once.
     */
    @Test
    public void execute_duplicateIndices_marksEachPersonOnce() {
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Physics-1800")
                .withClassGroup(classGroup)
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        
        // Setup first two persons with assignments
        for (int i = 0; i < 2; i++) {
            Person originalPerson = model.getFilteredPersonList().get(i);
            Person personWithAssignment = new PersonBuilder(originalPerson)
                    .withClassGroups(classGroup)
                    .withAssignments(classGroup, assignment.getAssignmentName())
                    .build();
            model.setPerson(originalPerson, personWithAssignment);
        }

        // Create command with duplicate indices (1, 1, 1, 2, 2, 2)
        List<Index> targetIndices = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(1),
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(2),
                Index.fromOneBased(2)
        );
        MarkAssignmentCommand command = new MarkAssignmentCommand(targetIndices, assignment);

        try {
            CommandResult result = command.execute(model);
            
            // Verify both persons are marked
            Person person1 = model.getFilteredPersonList().get(0);
            Person person2 = model.getFilteredPersonList().get(1);
            
            boolean person1Marked = person1.getAssignments().stream()
                    .anyMatch(a -> a.equals(assignment) && a.isMarked());
            boolean person2Marked = person2.getAssignments().stream()
                    .anyMatch(a -> a.equals(assignment) && a.isMarked());
            
            assertTrue(person1Marked, "Person 1 should be marked");
            assertTrue(person2Marked, "Person 2 should be marked");
            
            // Verify success message contains both persons (each only once)
            String expectedMessage = String.format(
                    MESSAGE_MARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    StringUtil.toTitleCase(person1.getName().fullName) + ", " 
                            + StringUtil.toTitleCase(person2.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Tests that marking with duplicate indices of same person works correctly.
     */
    @Test
    public void execute_multipleDuplicatesSamePerson_marksOnlyOnce() {
        String classGroup = "default-class";
        Assignment assignment = new AssignmentBuilder()
                .withName("Math-2000")
                .withClassGroup(classGroup)
                .build();

        Model model = new ModelManager(new AddressBook(TypicalPersons.getTypicalAddressBook()), new UserPrefs());
        Person originalPerson = model.getFilteredPersonList().get(0);
        Person personWithAssignment = new PersonBuilder(originalPerson)
                .withClassGroups(classGroup)
                .withAssignments(classGroup, assignment.getAssignmentName())
                .build();
        model.setPerson(originalPerson, personWithAssignment);

        // Create command with many duplicate indices (1, 1, 1, 1, 1)
        List<Index> targetIndices = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(1),
                Index.fromOneBased(1),
                Index.fromOneBased(1),
                Index.fromOneBased(1)
        );
        MarkAssignmentCommand command = new MarkAssignmentCommand(targetIndices, assignment);

        try {
            CommandResult result = command.execute(model);
            
            // Verify person is marked
            Person updatedPerson = model.getFilteredPersonList().get(0);
            boolean isMarked = updatedPerson.getAssignments().stream()
                    .anyMatch(a -> a.equals(assignment) && a.isMarked());
            
            assertTrue(isMarked, "Person should be marked");
            
            // Verify success message contains person name only once
            String expectedMessage = String.format(
                    MESSAGE_MARK_PERSON_SUCCESS,
                    StringUtil.toTitleCase(assignment.getAssignmentName()),
                    StringUtil.toTitleCase(updatedPerson.getName().fullName));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    @Test
    public void equals() {
        Assignment assignment = new AssignmentBuilder().withName("Physics-1800").build();
        List<Index> indices1 = Arrays.asList(Index.fromOneBased(1));
        List<Index> indices2 = Arrays.asList(Index.fromOneBased(1));
        List<Index> indices3 = Arrays.asList(Index.fromOneBased(2));
        MarkAssignmentCommand command1 = new MarkAssignmentCommand(indices1, assignment);
        MarkAssignmentCommand command2 = new MarkAssignmentCommand(indices2, assignment);
        MarkAssignmentCommand command3 = new MarkAssignmentCommand(indices3, assignment);

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
    private void assertCommandFailure(MarkAssignmentCommand command, Model model, String expectedMessage) {
        try {
            command.execute(model);
            throw new AssertionError("Expected a CommandException to be thrown.");
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
        }
    }
}
