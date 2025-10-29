package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;

import java.util.Set;
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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASSGROUP);
        if (!arePrefixesPresent(argMultimap, PREFIX_CLASSGROUP) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FilterByClassGroupCommand.MESSAGE_USAGE));
        }

        Set<ClassGroup> classGroups = ParserUtil
            .parseClassGroups(argMultimap.getAllValues(PREFIX_CLASSGROUP));

        if (classGroups.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FilterByClassGroupCommand.MESSAGE_USAGE));
        }

        Set<String> classGroupNames = classGroups.stream()
            .map(ClassGroup::getClassGroupName)
            .collect(java.util.stream.Collectors.toSet());
        return new FilterByClassGroupCommand(new StudentInClassGroupPredicate(classGroupNames));
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
