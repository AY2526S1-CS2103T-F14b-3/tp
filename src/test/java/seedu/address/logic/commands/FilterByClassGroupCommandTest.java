package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_MATH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_PHYSICS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.StudentInClassGroupPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterByClassGroupCommand}.
 */
public class FilterByClassGroupCommandTest {
    private static final String NON_EXISTENT_CLASSGROUP = "NonExistent-9999";
    private static final String PREDICATE_FIELD_NAME = "predicate";
    private Model model;
    private Model expectedModel;

    /**
     * Sets up the test environment before each test.
     * Initializes both models with typical address book data and adds BOB (who has class groups).
     */
    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        // Add BOB (who has class groups) to both models for testing
        model.addPerson(BOB);
        expectedModel.addPerson(BOB);
    }

    /**
     * Tests the equals method of FilterByClassGroupCommand.
     * Verifies that commands with the same predicate are equal, and different predicates are not equal.
     */
    @Test
    public void equals() {
        StudentInClassGroupPredicate firstPredicate =
                new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_MATH));
        StudentInClassGroupPredicate secondPredicate =
                new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_PHYSICS));

        FilterByClassGroupCommand filterFirstCommand = new FilterByClassGroupCommand(firstPredicate);
        FilterByClassGroupCommand filterSecondCommand = new FilterByClassGroupCommand(secondPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same values -> returns true
        FilterByClassGroupCommand filterFirstCommandCopy = new FilterByClassGroupCommand(firstPredicate);
        assertTrue(filterFirstCommand.equals(filterFirstCommandCopy));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different class group -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
    }

    /**
     * Tests execution of FilterByClassGroupCommand with a non-existent class group.
     * Verifies that no persons are found when filtering by a class group that doesn't exist.
     */
    @Test
    public void execute_nonExistentClassGroup_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        StudentInClassGroupPredicate predicate = new StudentInClassGroupPredicate(Set.of(NON_EXISTENT_CLASSGROUP));
        FilterByClassGroupCommand command = new FilterByClassGroupCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    /**
     * Tests execution of FilterByClassGroupCommand with case-insensitive matching.
     * Verifies that filtering works regardless of the case used for the class group name.
     */
    @Test
    public void execute_caseInsensitiveClassGroup_personsFound() {
        StudentInClassGroupPredicate predicate = new StudentInClassGroupPredicate(
                Set.of(VALID_CLASSGROUP_PHYSICS.toLowerCase()));
        FilterByClassGroupCommand command = new FilterByClassGroupCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        // BOB has VALID_CLASSGROUP_PHYSICS, so 1 person should be found
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(BOB), model.getFilteredPersonList());
    }

    /**
     * Tests execution of FilterByClassGroupCommand with an exact case match.
     * Verifies that filtering works with the exact case of the class group name.
     */
    @Test
    public void execute_exactCaseClassGroup_personsFound() {
        StudentInClassGroupPredicate predicate = new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_MATH));
        FilterByClassGroupCommand command = new FilterByClassGroupCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW,
                expectedModel.getFilteredPersonList().size());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    /**
     * Tests the toString method of FilterByClassGroupCommand.
     * Verifies that the string representation includes the predicate.
     */
    @Test
    public void toStringMethod() {
        StudentInClassGroupPredicate predicate = new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_MATH));
        FilterByClassGroupCommand filterCommand = new FilterByClassGroupCommand(predicate);
        String expected = FilterByClassGroupCommand.class.getCanonicalName()
                + "{" + PREDICATE_FIELD_NAME + "=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }

    /**
     * Tests execution of FilterByClassGroupCommand with multiple class groups.
     * Verifies that filtering works when searching for students in multiple class groups.
     */
    @Test
    public void execute_multipleClassGroups_personsFound() {
        Set<String> classGroups = Set.of(VALID_CLASSGROUP_MATH, VALID_CLASSGROUP_PHYSICS);
        StudentInClassGroupPredicate predicate = new StudentInClassGroupPredicate(classGroups);
        FilterByClassGroupCommand command = new FilterByClassGroupCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW,
                expectedModel.getFilteredPersonList().size());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }
}
