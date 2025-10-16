package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.assignment.Assignment;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_LEVEL = "2";

    private Name name;
    private Phone phone;
    private Level level;
    private Set<String> classGroups;
    private Set<Assignment> assignments;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        level = new Level(DEFAULT_LEVEL);
        classGroups = new HashSet<>();
        assignments = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        level = personToCopy.getLevel();
        classGroups = new HashSet<>(personToCopy.getClassGroups());
        assignments = new HashSet<>(personToCopy.getAssignments());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code classGroups} into a {@code Set<String>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withClassGroups(String ... classGroups) {
        this.classGroups = SampleDataUtil.getClassGroup(classGroups);
        return this;
    }

    /**
     * Parses the {@code assignments} into a {@code Set<Assignment>} and set it to the {@code Person} that we are
     * building.
     */
    public PersonBuilder withAssignments(String ... assignments) {
        this.assignments = SampleDataUtil.getAssignmentSet(assignments);
        return this;
    }

    /**
     * Sets the {@code Set<Assignment>} directly to the {@code Person} that we are building.
     *
     * @param assignments The set of assignments to assign to the person.
     * @return This PersonBuilder instance for method chaining.
     */
    public PersonBuilder withAssignments(Set<Assignment> assignments) {
        this.assignments = new HashSet<>(assignments);
        return this;
    }

    /**
     * Sets the {@code Level} of the {@code Person} that we are building.
     */
    public PersonBuilder withLevel(String level) {
        this.level = new Level(level);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    public Person build() {
        return new Person(name, phone, level, classGroups, assignments);
    }

}