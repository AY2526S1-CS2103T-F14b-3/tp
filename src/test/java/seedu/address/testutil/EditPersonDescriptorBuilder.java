package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPhone(person.getPhone());
        descriptor.setLevel(person.getLevel());
        descriptor.setClassGroups(person.getClassGroups());
        descriptor.setAssignments(person.getAssignments());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Level} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withLevel(String level) {
        descriptor.setLevel(new Level(level));
        return this;
    }

    /**
     * Parses the {@code classGroups} into a {@code Set<String>} and set it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withClassGroups(String... classGroups) {
        Set<String> classes = Stream.of(classGroups).collect(Collectors.toSet());
        descriptor.setClassGroups(classes);
        return this;
    }

    /**
     * Sets the {@code Set<Assignment>} to the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAssignments(Set<Assignment> assignments) {
        descriptor.setAssignments(assignments);
        return this;
    }

    /**
     * Parses the {@code assignments} into a {@code Set<Assignment>} and set it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withAssignments(String... assignments) {
        Set<Assignment> assignmentSet = Stream.of(assignments)
                .map(Assignment::new)
                .collect(Collectors.toSet());
        descriptor.setAssignments(assignmentSet);
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
