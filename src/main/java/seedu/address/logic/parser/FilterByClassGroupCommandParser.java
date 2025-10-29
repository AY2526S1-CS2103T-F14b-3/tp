package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.FilterByClassGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classgroup.ClassGroup;
import seedu.address.model.person.StudentInClassGroupPredicate;
/**
 * Parses input arguments and creates a new {@link FilterByClassGroupCommand} object.
 */
public class FilterByClassGroupCommandParser implements Parser<FilterByClassGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * {@link FilterByClassGroupCommand} and returns a FilterByClassGroupCommand
     * object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterByClassGroupCommand parse(String args) throws ParseException {
        requireNonNull(args);

        // Check for unrecognized prefixes first
        if (hasUnrecognizedPrefix(args)) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FilterByClassGroupCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASSGROUP);

        // Check for non-empty preamble and missing prefix
        if (!arePrefixesPresent(argMultimap, PREFIX_CLASSGROUP)
            || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FilterByClassGroupCommand.MESSAGE_USAGE));
        }

        // Verify only one instance of the class group prefix
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASSGROUP);

        // Split the comma-separated values and process each one
        String classGroupValue = argMultimap.getValue(PREFIX_CLASSGROUP)
            .orElseThrow(() -> new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FilterByClassGroupCommand.MESSAGE_USAGE)));

        if (classGroupValue.trim().isEmpty()) {
            throw new ParseException(ClassGroup.MESSAGE_CONSTRAINTS);
        }

        List<String> classGroupValues = Arrays.asList(classGroupValue.split(","));
        if (classGroupValues.isEmpty() || classGroupValues.get(0).trim().isEmpty()) {
            throw new ParseException(ClassGroup.MESSAGE_CONSTRAINTS);
        }

        Set<ClassGroup> classGroups = classGroupValues.stream()
            .map(String::trim)
            .map(value -> {
                try {
                    return ParserUtil.parseClassGroup(value);
                } catch (ParseException pe) {
                    throw new RuntimeException(pe);
                }
            })
            .collect(Collectors.toSet());

        if (classGroups.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FilterByClassGroupCommand.MESSAGE_USAGE));
        }

        Set<String> classGroupNames = classGroups.stream()
            .map(ClassGroup::getClassGroupName)
            .collect(Collectors.toSet());
        return new FilterByClassGroupCommand(new StudentInClassGroupPredicate(classGroupNames));
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if the raw arguments string contains any potential prefixes
     * (text followed by /) not handled by the tokenizer.
     */
    private boolean hasUnrecognizedPrefix(String args) {
        // Match any of these cases:
        // 1. Word characters followed by / that isn't c/ (e.g. test/, a/)
        // 2. A slash with no letter before it (e.g. /test)
        // 3. Any prefix that starts with something other than c/ at a word boundary
        return args.matches(".*((?:^|\\s)(?!/|c/)\\w+/|(?:^|\\s)/|\\b(?!c/)\\w+/).*");
    }
}
