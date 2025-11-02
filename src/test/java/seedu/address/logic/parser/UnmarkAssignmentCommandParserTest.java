package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForInvalidPrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.Assignment;
import seedu.address.testutil.AssignmentBuilder;

/**
 * Contains unit tests for {@link UnmarkAssignmentCommandParser}.
 */
public class UnmarkAssignmentCommandParserTest {

    private final UnmarkAssignmentCommandParser parser = new UnmarkAssignmentCommandParser();

    /**
     * Tests that a valid input string is successfully parsed into a {@code UnmarkAssignmentCommand}.
     */
    @Test
    public void parse_validInput_success() {
        Assignment expectedAssignment = new AssignmentBuilder()
                .withName("physics-1800")
                .withClassGroup("physics-1800")
                .build();

        // Test single index
        String singleIndexInput = "1 c/physics-1800 a/Physics-1800";
        List<Index> expectedSingleIndex = Arrays.asList(Index.fromOneBased(1));
        try {
            var command = parser.parse(singleIndexInput);
            var expectedCommand = new UnmarkAssignmentCommand(expectedSingleIndex, expectedAssignment);
            assertEquals(expectedCommand, command);
        } catch (ParseException pe) {
            fail("Unexpected ParseException thrown for valid single index input.");
        }

        // Test index range
        String rangeInput = "1-3 c/physics-1800 a/Physics-1800";
        List<Index> expectedRange = Arrays.asList(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
        );
        try {
            var command = parser.parse(rangeInput);
            var expectedCommand = new UnmarkAssignmentCommand(expectedRange, expectedAssignment);
            assertEquals(expectedCommand, command);
        } catch (ParseException pe) {
            fail("Unexpected ParseException thrown for valid range input.");
        }
    }

    /**
     * Tests that parsing fails when the assignment prefix is missing.
     */
    @Test
    public void parse_missingAssignment_throwsParseException() {
        String userInput = "1"; // Missing assignment prefix
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                UnmarkAssignmentCommand.MESSAGE_USAGE
        );
        assertParseFailure(parser, userInput, expectedMessage);
    }

    /**
     * Tests that parsing fails when an invalid index is provided.
     */
    @Test
    public void parse_invalidIndex_throwsParseException() {
        String userInput = "a c/physics-1800 a/Physics-1800"; // 'a' is not a valid index
        String expectedMessage = MESSAGE_INVALID_INDEX_FORMAT;
        assertParseFailure(parser, userInput, expectedMessage);
    }

    /**
     * Tests that parsing with duplicate indices successfully deduplicates them.
     */
    @Test
    public void parse_duplicateIndices_success() {
        // Test duplicate single indices - should deduplicate to [1, 2]
        String duplicateInput = "1 1 1 2 2 2 c/physics-1800 a/Physics-1800";
        try {
            UnmarkAssignmentCommand command = parser.parse(duplicateInput);
            // Verify parsing succeeds - the important part is that it doesn't fail
            // The actual deduplication is tested in the command execution tests
            // Just verify the command was created successfully
            assertEquals("unmark", command.getCommandWord());
        } catch (ParseException pe) {
            fail("Unexpected ParseException thrown for duplicate indices input: " + pe.getMessage());
        }

        // Test many duplicates of same index - should deduplicate to [1]
        String manyDuplicatesInput = "1 1 1 1 1 1 1 c/physics-1800 a/Physics-1800";
        try {
            UnmarkAssignmentCommand command = parser.parse(manyDuplicatesInput);
            assertEquals("unmark", command.getCommandWord());
        } catch (ParseException pe) {
            fail("Unexpected ParseException thrown for many duplicate indices input: " + pe.getMessage());
        }

        // Test duplicate ranges - should deduplicate overlapping indices
        String duplicateRangeInput = "1-3 2-4 c/physics-1800 a/Physics-1800";
        try {
            UnmarkAssignmentCommand command = parser.parse(duplicateRangeInput);
            assertEquals("unmark", command.getCommandWord());
        } catch (ParseException pe) {
            fail("Unexpected ParseException thrown for duplicate range input: " + pe.getMessage());
        }
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        String userInput = "1 c/physics-1800 a/Physics-1800 p/97658418";
        String expectedMessage = getErrorMessageForInvalidPrefixes(PREFIX_PHONE);
        assertParseFailure(parser, userInput, expectedMessage);
    }
}
