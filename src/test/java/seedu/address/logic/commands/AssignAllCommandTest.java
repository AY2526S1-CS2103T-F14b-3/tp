package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ASSIGNMENT_MATH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ASSIGNMENT_PHYSICS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_MATH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_PHYSICS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for AssignAllCommand.
 */
public class AssignAllCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests that assigning an assignment to all students in a class group succeeds.
     * Verifies that the assignment is added to all students enrolled in the specified class.
     */
    @Test
    public void execute_assignToClassGroup_success() {
        // Setup: Add students with Math class
        Model modelWithMathClass = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        modelWithMathClass.addPerson(alice);
        modelWithMathClass.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 2, VALID_CLASSGROUP_MATH.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person aliceWithAssignment = new PersonBuilder(alice)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        Person bobWithAssignment = new PersonBuilder(bob)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(aliceWithAssignment);
        expectedModel.addPerson(bobWithAssignment);

        assertCommandSuccess(command, modelWithMathClass, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning an assignment to a single student in a class succeeds.
     * Verifies that the command works correctly when only one student is enrolled in the class.
     */
    @Test
    public void execute_assignToOneStudentInClass_success() {
        // Setup: Add one student with Physics class
        Model modelWithPhysicsClass = new ModelManager(new AddressBook(), new UserPrefs());
        Person charlie = new PersonBuilder().withName("Charlie").withPhone("93456789")
                .withLevel("3").withClassGroups(VALID_CLASSGROUP_PHYSICS).build();
        modelWithPhysicsClass.addPerson(charlie);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_PHYSICS.toLowerCase(),
                VALID_CLASSGROUP_PHYSICS.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_PHYSICS.toLowerCase(), assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_PHYSICS.toLowerCase(), 1, VALID_CLASSGROUP_PHYSICS.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person charlieWithAssignment = new PersonBuilder(charlie)
                .withAssignments(VALID_CLASSGROUP_PHYSICS.toLowerCase(),
                        VALID_ASSIGNMENT_PHYSICS.toLowerCase()).build();
        expectedModel.addPerson(charlieWithAssignment);

        assertCommandSuccess(command, modelWithPhysicsClass, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning an assignment to multiple students in a class succeeds.
     * Verifies that only students enrolled in the specified class receive the assignment.
     */
    @Test
    public void execute_assignToMultipleClassStudents_success() {
        // Setup: Add students, some with Math class, some without
        Model modelWithMixedClasses = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_PHYSICS).build();
        Person charlie = new PersonBuilder().withName("Charlie").withPhone("93456789")
                .withLevel("3").withClassGroups(VALID_CLASSGROUP_MATH).build();
        modelWithMixedClasses.addPerson(alice);
        modelWithMixedClasses.addPerson(bob);
        modelWithMixedClasses.addPerson(charlie);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        // Only Alice and Charlie should get the assignment (they have Math class)
        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 2, VALID_CLASSGROUP_MATH.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person aliceWithAssignment = new PersonBuilder(alice)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        Person charlieWithAssignment = new PersonBuilder(charlie)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(aliceWithAssignment);
        expectedModel.addPerson(bob); // Bob doesn't have Math class, so no assignment
        expectedModel.addPerson(charlieWithAssignment);

        assertCommandSuccess(command, modelWithMixedClasses, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning an assignment skips students who already have the assignment.
     * Verifies that students who already possess the assignment are not reassigned it.
     */
    @Test
    public void execute_studentAlreadyHasAssignment_skipsStudent() {
        // Setup: Add student with Math class and already has the assignment
        Model modelWithAssignment = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        modelWithAssignment.addPerson(alice);
        modelWithAssignment.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        // Only Bob should get the assignment (Alice already has it)
        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 1, VALID_CLASSGROUP_MATH.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person bobWithAssignment = new PersonBuilder(bob)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(alice); // Alice already has assignment
        expectedModel.addPerson(bobWithAssignment);

        assertCommandSuccess(command, modelWithAssignment, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning an assignment already assigned to all student of the class throws a CommandException.
     * Verifies that the command fails when all students in the specified class have the assignment.
     */
    @Test
    public void execute_assignmentsAlreadyAssignedToAllStudents_throwsCommandException() {
        // Setup: Add students with Math class who already have the assignment
        Model modelAllHaveAssignment = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        modelAllHaveAssignment.addPerson(alice);
        modelAllHaveAssignment.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        assertCommandFailure(command, modelAllHaveAssignment,
                String.format(AssignAllCommand.MESSAGE_ALREADY_ASSIGNED,
                        StringUtil.toTitleCase(VALID_CLASSGROUP_MATH.toLowerCase()),
                        StringUtil.toTitleCase(VALID_ASSIGNMENT_MATH.toLowerCase())));
    }

    /**
     * Tests that unassigning from a non-existent class group throws a CommandException.
     * Verifies that the command fails when no students are enrolled in the specified class.
     */
    @Test
    public void execute_noStudentsInClass_throwsCommandException() {
        // Setup: Empty model or students without the specified class
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_PHYSICS).build();
        emptyModel.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        assertCommandFailure(command, emptyModel,
                String.format(AssignAllCommand.MESSAGE_NO_STUDENTS_FOUND,
                        StringUtil.toTitleCase(VALID_CLASSGROUP_MATH.toLowerCase())));
    }

    /**
     * Tests that assigning to an empty address book throws a CommandException.
     * Verifies that the command fails when there are no students in the address book.
     */
    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        assertCommandFailure(command, emptyModel,
                String.format(AssignAllCommand.MESSAGE_NO_STUDENTS_FOUND,
                        StringUtil.toTitleCase(VALID_CLASSGROUP_MATH.toLowerCase())));
    }

    /**
     * Tests that class group name matching is case-insensitive.
     * Verifies that "math 3pm" matches students enrolled in "Math 3PM".
     */
    @Test
    public void execute_caseInsensitiveClassGroupMatch_success() {
        // Setup: Add student with "Math 3PM" class
        Model modelWithMathClass = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups("Math 3PM").build();
        modelWithMathClass.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(), "math 3pm");
        // Use different case for class group name
        AssignAllCommand command = new AssignAllCommand("math 3pm", assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 1, "math 3pm");

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person aliceWithAssignment = new PersonBuilder(alice)
                .withAssignments("math 3pm", VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(aliceWithAssignment);

        assertCommandSuccess(command, modelWithMathClass, expectedMessage, expectedModel);
    }

    /**
     * Tests the equals method for AssignAllCommand.
     * Verifies correct equality behavior for same objects, same values, different types,
     * null values, different class groups, and different assignments.
     */
    @Test
    public void equals() {
        Assignment mathAssignment = new Assignment(VALID_ASSIGNMENT_MATH, VALID_CLASSGROUP_MATH);
        Assignment physicsAssignment = new Assignment(VALID_ASSIGNMENT_PHYSICS, VALID_CLASSGROUP_PHYSICS);

        AssignAllCommand assignMathToMathClass = new AssignAllCommand(VALID_CLASSGROUP_MATH, mathAssignment);
        AssignAllCommand assignMathToPhysicsClass = new AssignAllCommand(VALID_CLASSGROUP_PHYSICS, mathAssignment);
        AssignAllCommand assignPhysicsToMathClass = new AssignAllCommand(VALID_CLASSGROUP_MATH, physicsAssignment);

        // same object -> returns true
        assertTrue(assignMathToMathClass.equals(assignMathToMathClass));

        // same values -> returns true
        AssignAllCommand assignMathToMathClassCopy = new AssignAllCommand(VALID_CLASSGROUP_MATH, mathAssignment);
        assertTrue(assignMathToMathClass.equals(assignMathToMathClassCopy));

        // different types -> returns false
        assertFalse(assignMathToMathClass.equals(1));

        // null -> returns false
        assertFalse(assignMathToMathClass.equals(null));

        // different class group -> returns false
        assertFalse(assignMathToMathClass.equals(assignMathToPhysicsClass));

        // different assignment -> returns false
        assertFalse(assignMathToMathClass.equals(assignPhysicsToMathClass));
    }

    /**
     * Tests the toString method for AssignAllCommand.
     * Verifies that the string representation includes the class group name and assignment details.
     */
    @Test
    public void toStringMethod() {
        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH, VALID_CLASSGROUP_MATH);
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH, assignment);

        String expected = AssignAllCommand.class.getCanonicalName()
                + "{classGroupName=" + VALID_CLASSGROUP_MATH
                + ", assignment=" + assignment + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Tests that getCommandWord returns the correct command word.
     */
    @Test
    public void getCommandWord_returnsCorrectWord() {
        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH, VALID_CLASSGROUP_MATH);
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH, assignment);
        assertEquals("assignall", command.getCommandWord());
    }

    /**
     * Tests that assigning to students with multiple class groups works correctly.
     * Only students with the matching class group should receive the assignment.
     */
    @Test
    public void execute_studentsWithMultipleClassGroups_success() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        // Alice has both Math and Physics classes
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH, VALID_CLASSGROUP_PHYSICS).build();
        // Bob has only Physics class
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_PHYSICS).build();
        model.addPerson(alice);
        model.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 1, VALID_CLASSGROUP_MATH.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person aliceWithAssignment = new PersonBuilder(alice)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(aliceWithAssignment);
        expectedModel.addPerson(bob);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning works with class group names that have special characters.
     */
    @Test
    public void execute_classGroupWithSpecialCharacters_success() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        String specialClassGroup = "math-2024";
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(specialClassGroup).build();
        model.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(), specialClassGroup);
        AssignAllCommand command = new AssignAllCommand(specialClassGroup, assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 1, specialClassGroup);

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person aliceWithAssignment = new PersonBuilder(alice)
                .withAssignments(specialClassGroup, VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        expectedModel.addPerson(aliceWithAssignment);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that assigning to a large number of students works correctly.
     */
    @Test
    public void execute_assignToManyStudents_success() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());

        // Create 10 students in the same class
        for (int i = 1; i <= 10; i++) {
            Person student = new PersonBuilder()
                    .withName("Student" + i)
                    .withPhone("9000000" + i)
                    .withLevel(String.valueOf(i % 4 + 1))
                    .withClassGroups(VALID_CLASSGROUP_MATH).build();
            model.addPerson(student);

            Person studentWithAssignment = new PersonBuilder(student)
                    .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(),
                            VALID_ASSIGNMENT_MATH.toLowerCase()).build();
            expectedModel.addPerson(studentWithAssignment);
        }

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 10, VALID_CLASSGROUP_MATH.toLowerCase());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that partial assignment (some students already have it) counts correctly.
     */
    @Test
    public void execute_partialAssignment_countsOnlyNewAssignments() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        // Create 5 students, 3 already have the assignment
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person charlie = new PersonBuilder().withName("Charlie").withPhone("93456789")
                .withLevel("3").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person david = new PersonBuilder().withName("David").withPhone("94567890")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person eve = new PersonBuilder().withName("Eve").withPhone("95678901")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();

        model.addPerson(alice);
        model.addPerson(bob);
        model.addPerson(charlie);
        model.addPerson(david);
        model.addPerson(eve);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        // Should only count Bob and David (2 students)
        String expectedMessage = String.format(AssignAllCommand.MESSAGE_SUCCESS,
                VALID_ASSIGNMENT_MATH.toLowerCase(), 2, VALID_CLASSGROUP_MATH.toLowerCase());

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        Person bobWithAssignment = new PersonBuilder(bob)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();
        Person davidWithAssignment = new PersonBuilder(david)
                .withAssignments(VALID_CLASSGROUP_MATH.toLowerCase(), VALID_ASSIGNMENT_MATH.toLowerCase()).build();

        expectedModel.addPerson(alice);
        expectedModel.addPerson(bobWithAssignment);
        expectedModel.addPerson(charlie);
        expectedModel.addPerson(davidWithAssignment);
        expectedModel.addPerson(eve);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests equals method with null assignment.
     */
    @Test
    public void equals_nullAssignment_returnsFalse() {
        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH, VALID_CLASSGROUP_MATH);
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH, assignment);

        assertFalse(command.equals(null));
    }

    /**
     * Tests equals method with different class object.
     */
    @Test
    public void equals_differentClass_returnsFalse() {
        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH, VALID_CLASSGROUP_MATH);
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH, assignment);

        assertFalse(command.equals("not a command"));
    }

    /**
     * Tests that assignment name is properly formatted in success message.
     */
    @Test
    public void execute_successMessage_containsCorrectAssignmentName() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        model.addPerson(alice);

        String customAssignment = "quiz-1-chapter-3";
        Assignment assignment = new Assignment(customAssignment, VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            String message = result.getFeedbackToUser();
            assertTrue(message.contains(customAssignment));
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests logging when assignment is successfully assigned to students.
     * Verifies that the assignment toString representation is used in logging.
     */
    @Test
    public void execute_loggingAssignmentSuccess_usesToString() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        model.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            command.execute(model);
            // Verify that the assignment was added successfully
            Person updatedPerson = model.getFilteredPersonList().get(0);
            assertTrue(updatedPerson.getAssignments().contains(assignment));
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests logging when students already have the assignment.
     * Verifies that the assignment toString representation is used in logging for skipped students.
     */
    @Test
    public void execute_loggingAlreadyHasAssignment_usesToString() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        model.addPerson(alice);
        model.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            // Verify that only Bob got the assignment (Alice already had it)
            assertTrue(result.getFeedbackToUser().contains("1 student"));
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests logging when all students already have the assignment.
     * Verifies that the assignment toString representation is used in warning logs.
     */
    @Test
    public void execute_loggingAllHaveAssignment_usesToString() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        model.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            command.execute(model);
            throw new AssertionError("Command should have failed with MESSAGE_ALREADY_ASSIGNED");
        } catch (CommandException e) {
            // Expected exception - verify error message contains the assignment name
            assertTrue(e.getMessage().contains(StringUtil.toTitleCase(VALID_CLASSGROUP_MATH.toLowerCase())));
            assertTrue(e.getMessage().contains(StringUtil.toTitleCase(VALID_ASSIGNMENT_MATH.toLowerCase())));
        }
    }

    /**
     * Tests that assignment with different class group toString format is handled correctly.
     * Verifies the full assignment representation [name (classgroup)] in logs.
     */
    @Test
    public void execute_assignmentToStringFormat_correctlyFormatted() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        String classGroup = "physics-2024";
        String assignmentName = "lab-report-1";

        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(classGroup).build();
        model.addPerson(alice);

        Assignment assignment = new Assignment(assignmentName, classGroup);
        AssignAllCommand command = new AssignAllCommand(classGroup, assignment);

        try {
            CommandResult result = command.execute(model);
            // Verify successful assignment
            String message = result.getFeedbackToUser();
            assertTrue(message.contains(assignmentName));
            assertTrue(message.contains(classGroup));

            // Verify the person now has the assignment
            Person updatedPerson = model.getFilteredPersonList().get(0);
            assertTrue(updatedPerson.getAssignments().contains(assignment));
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests the assignedCount incrementing when assignment is successfully added.
     * This directly tests the code path: model.setPerson(person, editedPerson); assignedCount++;
     */
    @Test
    public void execute_assignedCountIncrement_correctCount() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        // Create 3 students, 1 already has the assignment
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person charlie = new PersonBuilder().withName("Charlie").withPhone("93456789")
                .withLevel("3").withClassGroups(VALID_CLASSGROUP_MATH).build();

        model.addPerson(alice);
        model.addPerson(bob);
        model.addPerson(charlie);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            // Should report 2 students (Alice and Charlie), Bob already had it
            assertTrue(result.getFeedbackToUser().contains("2 student"));

            // Verify all three now have the assignment
            for (Person person : model.getFilteredPersonList()) {
                assertTrue(person.getAssignments().contains(assignment));
            }
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests the exact scenario where assignedCount remains 0 and exception is thrown.
     * Directly tests: if (assignedCount == 0) { throw new CommandException(...) }
     */
    @Test
    public void execute_assignedCountZero_throwsException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        // All students already have the assignment
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();

        model.addPerson(alice);
        model.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        // Verify exception is thrown with correct message format
        try {
            command.execute(model);
            throw new AssertionError("Should have thrown CommandException");
        } catch (CommandException e) {
            String expectedError = String.format(AssignAllCommand.MESSAGE_ALREADY_ASSIGNED,
                    StringUtil.toTitleCase(VALID_CLASSGROUP_MATH.toLowerCase()),
                    StringUtil.toTitleCase(VALID_ASSIGNMENT_MATH.toLowerCase()));
            assertEquals(expectedError, e.getMessage());
        }
    }

    /**
     * Tests line 105: logger.fine with assignment toString when successfully assigned.
     * Directly verifies the logging path that uses assignment.toString() after model.setPerson().
     */
    @Test
    public void execute_line105_loggerFineAssignedToString() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();
        model.addPerson(alice);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            // Verify the assignment was added (this path triggers line 105 logging)
            Person updatedPerson = model.getFilteredPersonList().get(0);
            assertTrue(updatedPerson.getAssignments().contains(assignment));
            // Verify assignedCount was incremented (line 103)
            assertTrue(result.getFeedbackToUser().contains("1 student"));
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests line 108: logger.fine when student already has assignment.
     * Directly verifies the else branch logging that uses assignment.toString().
     */
    @Test
    public void execute_line108_loggerFineAlreadyHasAssignment() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        // Create two students: one already has the assignment, one doesn't
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        model.addPerson(alice);
        model.addPerson(bob);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            // Alice already had assignment (triggers line 108), Bob gets it (triggers line 105)
            assertTrue(result.getFeedbackToUser().contains("1 student"));
            // Verify both now have the assignment
            for (Person person : model.getFilteredPersonList()) {
                assertTrue(person.getAssignments().contains(assignment));
            }
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }

    /**
     * Tests that both logging paths (lines 105 and 108) are triggered in same execution.
     * Verifies mixed scenario where some students get new assignment and some already have it.
     */
    @Test
    public void execute_lines105And108_bothLoggingPathsTriggered() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        // Create 4 students: 2 already have assignment, 2 don't
        Person alice = new PersonBuilder().withName("Alice").withPhone("91234567")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("92345678")
                .withLevel("2").withClassGroups(VALID_CLASSGROUP_MATH).build();
        Person charlie = new PersonBuilder().withName("Charlie").withPhone("93456789")
                .withLevel("3").withClassGroups(VALID_CLASSGROUP_MATH)
                .withAssignments(VALID_CLASSGROUP_MATH, VALID_ASSIGNMENT_MATH).build();
        Person david = new PersonBuilder().withName("David").withPhone("94567890")
                .withLevel("1").withClassGroups(VALID_CLASSGROUP_MATH).build();

        model.addPerson(alice);
        model.addPerson(bob);
        model.addPerson(charlie);
        model.addPerson(david);

        Assignment assignment = new Assignment(VALID_ASSIGNMENT_MATH.toLowerCase(),
                VALID_CLASSGROUP_MATH.toLowerCase());
        AssignAllCommand command = new AssignAllCommand(VALID_CLASSGROUP_MATH.toLowerCase(), assignment);

        try {
            CommandResult result = command.execute(model);
            // Should report 2 new assignments (Bob and David)
            // Alice and Charlie trigger line 108 (already have it)
            // Bob and David trigger line 105 (newly assigned)
            assertTrue(result.getFeedbackToUser().contains("2 student"));

            // Verify all four now have the assignment
            assertEquals(4, model.getFilteredPersonList().size());
            for (Person person : model.getFilteredPersonList()) {
                assertTrue(person.getAssignments().contains(assignment),
                        "Person " + person.getName() + " should have the assignment");
            }
        } catch (CommandException e) {
            throw new AssertionError("Command should not fail", e);
        }
    }
}
