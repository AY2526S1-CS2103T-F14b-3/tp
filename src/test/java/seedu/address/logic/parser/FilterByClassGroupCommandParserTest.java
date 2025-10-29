package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_MATH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASSGROUP_PHYSICS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.classgroup.ClassGroup.MESSAGE_CONSTRAINTS;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterByClassGroupCommand;
import seedu.address.model.person.StudentInClassGroupPredicate;

public class FilterByClassGroupCommandParserTest {

    private FilterByClassGroupCommandParser parser = new FilterByClassGroupCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingClassGroupPrefix_throwsParseException() {
        // no prefix
        assertParseFailure(parser, VALID_CLASSGROUP_MATH,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_otherPrefixes_throwsParseException() {
        // other prefixes present
        assertParseFailure(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + " " + PREFIX_LEVEL + "JC1",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));

        // other prefix with class group
        assertParseFailure(parser,
            " " + PREFIX_ASSIGNMENT + "HW1 " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));

        // unknown prefix
        assertParseFailure(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + " b/test",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));

        // invalid prefix format
        assertParseFailure(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + " asdfas/",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));

        // non-standard prefix format
        assertParseFailure(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + " /test",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValue_throwsParseException() {
        // spaces only
        assertParseFailure(parser, " " + PREFIX_CLASSGROUP + "    ",
            MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPreamble_throwsParseException() {
        // non-empty preamble
        assertParseFailure(parser, "some random string " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));

        // prefix with empty value followed by valid prefix-value
        assertParseFailure(parser, PREFIX_CLASSGROUP + " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterByClassGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_singleClassGroup_returnsFilterCommand() {
        // single class group
        FilterByClassGroupCommand expectedCommand = new FilterByClassGroupCommand(
            new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_MATH)));

        // no leading and trailing whitespaces
        assertParseSuccess(parser, " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH, expectedCommand);

        // multiple whitespaces
        assertParseSuccess(parser, "  " + PREFIX_CLASSGROUP + "  " + VALID_CLASSGROUP_MATH + "  ", expectedCommand);
    }

    @Test
    public void parse_multipleClassGroups_returnsFilterCommand() {
        // multiple class groups
        Set<String> classGroups = Set.of(VALID_CLASSGROUP_MATH, VALID_CLASSGROUP_PHYSICS);
        FilterByClassGroupCommand expectedCommand = new FilterByClassGroupCommand(
            new StudentInClassGroupPredicate(classGroups));

        // no leading and trailing whitespaces
        assertParseSuccess(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + "," + VALID_CLASSGROUP_PHYSICS,
            expectedCommand);

        // multiple whitespaces around comma
        assertParseSuccess(parser,
            "  " + PREFIX_CLASSGROUP + "  " + VALID_CLASSGROUP_MATH + " , "
            + VALID_CLASSGROUP_PHYSICS + "  ",
            expectedCommand);
    }

    @Test
    public void parse_duplicateClassGroups_returnsFilterCommand() {
        // duplicate class groups (should be treated as one due to Set)
        FilterByClassGroupCommand expectedCommand = new FilterByClassGroupCommand(
            new StudentInClassGroupPredicate(Set.of(VALID_CLASSGROUP_MATH)));

        assertParseSuccess(parser,
            " " + PREFIX_CLASSGROUP + VALID_CLASSGROUP_MATH + "," + VALID_CLASSGROUP_MATH,
            expectedCommand);
    }
}
