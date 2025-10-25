package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.Assignment;

/**
 * Parses input arguments and creates a new {@link seedu.address.logic.commands.MarkAssignmentCommand}.
 */
public class MarkAssignmentCommandParser implements Parser<MarkAssignmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MarkAssignmentCommand
     * and returns a MarkAssignmentCommand object for execution.
     *
     * The expected format is: INDEX c/CLASS_GROUP a/ASSIGNMENT_NAME
     *
     * @param args full user input string (arguments portion)
     * @return a MarkAssignmentCommand containing the parsed index and assignment
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public MarkAssignmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = tokenizeArguments(args);

        Index index = parseIndexFromPreamble(argMultimap);
        String classGroupName = parseClassGroupName(argMultimap);
        Assignment assignment = parseAssignmentValue(argMultimap, classGroupName);

        return new MarkAssignmentCommand(index, assignment);
    }

    /**
     * Tokenizes the raw argument string using the assignment and classgroup prefixes.
     */
    private ArgumentMultimap tokenizeArguments(String args) {
        return ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT, PREFIX_CLASSGROUP);
    }

    /**
     * Parses and returns the index from the preamble portion of the tokenized arguments.
     *
     * @throws ParseException if the index is invalid
     */
    private Index parseIndexFromPreamble(ArgumentMultimap argMultimap) throws ParseException {
        try {
            return ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MarkAssignmentCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Extracts, validates and parses the assignment value from the tokenized arguments.
     *
     * @throws ParseException if the assignment value is missing or invalid
     */
    private Assignment parseAssignmentValue(ArgumentMultimap argMultimap, String classGroupName)
            throws ParseException {
        Optional<String> assignmentValue = argMultimap.getValue(PREFIX_ASSIGNMENT);
        if (assignmentValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MarkAssignmentCommand.MESSAGE_USAGE));
        }

        return ParserUtil.parseAssignment(assignmentValue.get(), classGroupName);
    }

    /**
     * Extracts, validates and parses the classGroupName from the tokenized arguments.
     *
     * @throws ParseException if the classGroupName is missing or invalid
     */
    private String parseClassGroupName(ArgumentMultimap argMultimap)
            throws ParseException {

        // Check if class group is provided
        if (!argMultimap.getValue(PREFIX_CLASSGROUP).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MarkAssignmentCommand.MESSAGE_USAGE));
        }

        String classGroupName = argMultimap.getValue(PREFIX_CLASSGROUP).get().trim().toLowerCase();
        if (classGroupName.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MarkAssignmentCommand.MESSAGE_USAGE));
        }

        return classGroupName;
    }
}
