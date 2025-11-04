<frontmatter>
  title: "Developer Guide"
</frontmatter>

# Developer Guide

<div id="toc">

<!-- MarkBind will generate the table of contents here -->
</div>

----

## Acknowledgements

* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## Setting Up

### Setting up the project in your computer

<div markdown="span" class="alert alert-warning"><span class="fas fa-exclamation-triangle" aria-hidden="true"></span> <strong>Caution:</strong>
Follow the steps in the following guide precisely. Things will not work out if you deviate in some steps.
</div>

First, **fork** this repo, and **clone** the fork into your computer.

If you plan to use Intellij IDEA (highly recommended):

1. **Configure the JDK**: Follow the guide [_[se-edu/guides] IDEA: Configuring the JDK_](https://se-education.org/guides/tutorials/intellijJdk.html) to ensure Intellij is configured to use **JDK 17**.
1. **Import the project as a Gradle project**: Follow the guide [_[se-edu/guides] IDEA: Importing a Gradle project_](https://se-education.org/guides/tutorials/intellijImportGradleProject.html) to import the project into IDEA.<br>
   ⚠️ Note: Importing a Gradle project is slightly different from importing a normal Java project.
1. **Verify the setup**:
   1. Run the `seedu.address.Main` and try a few commands.
   1. [Run the tests](Testing.md) to ensure they all pass.

### Before writing code

1. **Configure the coding style**

   If using IDEA, follow the guide [_[se-edu/guides] IDEA: Configuring the code style_](https://se-education.org/guides/tutorials/intellijCodeStyle.html) to set up IDEA's coding style to match ours.

   <div markdown="span" class="alert alert-primary"><span class="fas fa-lightbulb" aria-hidden="true"></span> <strong>Tip:</strong>

   Optionally, you can follow the guide [_[se-edu/guides] Using Checkstyle_](https://se-education.org/guides/tutorials/checkstyle.html) to find how to use the CheckStyle within IDEA e.g., to report problems _as_ you write code.
   </div>

1. **Set up CI**

   This project comes with a GitHub Actions config files (in `.github/workflows` folder). When GitHub detects those files, it will run the CI for your project automatically at each push to the `master` branch or to any PR. No set up required.

1. **Learn the design**

   When you are ready to start coding, we recommend that you get some sense of the overall design by reading about [TutorTrack's architecture](DeveloperGuide.md#architecture).

1. **Do the tutorials**
   These tutorials will help you get acquainted with the codebase.

   * [Tracing code](https://se-education.org/guides/tutorials/ab3TracingCode.html)
   * [Adding a new command](https://se-education.org/guides/tutorials/ab3AddRemark.html)
   * [Removing fields](https://se-education.org/guides/tutorials/ab3RemovingFields.html)

--------------------------------------------------------------------------------------------------------------------

## Design

<box type="info" seamless>

**Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</box>

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

> NOTE: Certain package names used in these components are preserved from AB3 (as a brownfield project). For example: `AddressBookParser`, `Person`, `UniquePersonList`, `AddressBookStorage`.


**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/BetterModelClassDiagram.puml" width="650" />

The `Model` component,

* stores the current `AddressBook` data, i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores a `CommandHistory` object that records executed commands for command review functionality.
* stores an `AddressBookVersionManager` object that manages snapshots of past `AddressBook` states.
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be observed; for example, the UI can be bound to this list so that it automatically updates when the data in the list changes.

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-F14b-3/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* saves command history data (up to 50 commands) in text format, and can be used to initialise a CommandHistory Object in Model
* inherits from both `AddressBookStorage` `CommandHistoryStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## Implementation

This section describes some noteworthy details on how certain features are implemented.

### Undo/Redo Feature

#### Implementation Overview

The undo/redo mechanism is facilitated by `AddressBookVersionManager`, which maintains two internal stacks to track the version history of the address book:

* `versionStack` — Stores all committed states of the address book in chronological order
* `redoStack` — Stores states that have been undone and can be restored

The `AddressBookVersionManager` supports the following operations:

* `commit(ReadOnlyAddressBook)` — Saves the current address book state as a new version
* `undo()` — Reverts the address book to its previous state
* `redo()` — Restores a previously undone state

These operations are exposed in the `Model` interface as `Model#commit()`, `Model#undo()`, and `Model#redo()` respectively.

#### Design Considerations

**Aspect: Stack-based Architecture**

The implementation uses two stacks to efficiently manage version history. The behavior of each operation is as follows:

* When a command modifies the address book, `commit()` pushes the new state onto `versionStack` and clears `redoStack`
* When `undo()` is called, the current state is moved from `versionStack` to `redoStack`
* When `redo()` is called, the state is moved back from `redoStack` to `versionStack`

**Aspect: Clearing Redo History on New Commits**

When a new commit is made after an undo operation, all redo history is cleared.

* **Pros:**
    * Maintains a linear, predictable history that prevents branching timelines
    * Prevents potential state inconsistencies
    * Simpler mental model for users
* **Cons:**
    * Users cannot return to undone states after making new changes
    * May be unexpected behavior for users familiar with tree-based undo systems
* **Alternative considered:** Maintaining a tree structure to preserve all branches
    * **Reason for rejection:** This adds significant implementation complexity and could confuse users with multiple possible "future" states

**Aspect: Memory Management**

The version history is session-based and not persisted to disk.

* **Pros:**
    * Simpler implementation without serialization concerns
    * No storage space concerns for large histories
* **Cons:**
    * Undo/redo history is lost when the application closes
    * Cannot undo changes from previous sessions

--------------------------------------------------------------------------------------------------------------------

## Documentation, logging, testing, configuration, dev-ops

### Testing

#### Running tests

There are two ways to run tests.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

#### Types of tests

This project has three types of tests:

1. *Unit tests* targeting the lowest level methods/classes.<br>
   e.g. `seedu.address.commons.StringUtilTest`
1. *Integration tests* that are checking the integration of multiple code units (those code units are assumed to be working).<br>
   e.g. `seedu.address.storage.StorageManagerTest`
1. Hybrids of unit and integration tests. These test are checking multiple code units as well as how the are connected together.<br>
   e.g. `seedu.address.logic.LogicManagerTest`

### DevOps

#### Build automation

This project uses Gradle for **build automation and dependency management**. **You are recommended to read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html)**.


Given below are how to use Gradle for some important project tasks.


* **`clean`**: Deletes the files created during the previous build tasks (e.g. files in the `build` folder).<br>
  e.g. `./gradlew clean`

* **`shadowJar`**: Uses the ShadowJar plugin to create a fat JAR file in the `build/lib` folder, *if the current file is outdated*.<br>
  e.g. `./gradlew shadowJar`.

* **`run`**: Builds and runs the application.<br>
  **`runShadow`**: Builds the application as a fat JAR, and then runs it.

* **`checkstyleMain`**: Runs the code style check for the main code base.<br>
  **`checkstyleTest`**: Runs the code style check for the test code base.

* **`test`**: Runs all tests.
  * `./gradlew test` — Runs all tests
  * `./gradlew clean test` — Cleans the project and runs tests

#### Continuous integration (CI)

This project uses GitHub Actions for CI. The project comes with the necessary GitHub Actions configurations files (in the `.github/workflows` folder). No further setting up required.

##### Code coverage

As part of CI, this project uses Codecov to generate coverage reports. When CI runs, it will generate code coverage data (based on the tests run by CI) and upload that data to the CodeCov website, which in turn can provide you more info about the coverage of your tests.

However, because Codecov is known to run into intermittent problems (e.g., report upload fails) due to issues on the Codecov service side, the CI is configured to pass even if the Codecov task failed. Therefore, developers are advised to check the code coverage levels periodically and take corrective actions if the coverage level falls below desired levels.

To enable Codecov for forks of this project, follow the steps given in [this se-edu guide](https://se-education.org/guides/tutorials/codecov.html).

##### Repository-wide checks

In addition to running Gradle checks, CI includes some repository-wide checks. Unlike the Gradle checks which only cover files used in the build process, these repository-wide checks cover all files in the repository. They check for repository rules which are hard to enforce on development machines such as line ending requirements.

These checks are implemented as POSIX shell scripts, and thus can only be run on POSIX-compliant operating systems such as macOS and Linux. To run all checks locally on these operating systems, execute the following in the repository root directory:

`./config/travis/run-checks.sh`

Any warnings or errors will be printed out to the console.

**If adding new checks:**

* Checks are implemented as executable `check-*` scripts within the `.github` directory. The `run-checks.sh` script will automatically pick up and run files named as such. That is, you can add more such files if you need and the CI will do the rest.

* Check scripts should print out errors in the format `SEVERITY:FILENAME:LINE: MESSAGE`
  * SEVERITY is either ERROR or WARN.
  * FILENAME is the path to the file relative to the current directory.
  * LINE is the line of the file where the error occurred and MESSAGE is the message explaining the error.

* Check scripts must exit with a non-zero exit code if any errors occur.

#### Making a release

Here are the steps to create a new release.

1. Update the version number in [`MainApp.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java).
1. Generate a fat JAR file using Gradle (i.e., `gradlew shadowJar`).
1. Tag the repo with the version number. e.g. `v0.1`
1. [Create a new release using GitHub](https://help.github.com/articles/creating-releases/). Upload the JAR file you created.

### Logging

* We are using `java.util.logging` package for logging.
* The `LogsCenter` class is used to manage the logging levels and logging destinations.
*  The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to the specified logging level.
*  Log messages are output through the console and to a `.log` file.
*  The output logging level can be controlled using the `logLevel` setting in the configuration file (See the [Configuration guide](#configuration) section).
* **When choosing a level for a log message**, follow the conventions given in [_[se-edu/guides] Java: Logging conventions_](https://se-education.org/guides/conventions/java/logging.html).

### Documentation

**Setting up and maintaining the project website:**

* We use [**MarkBind**](https://markbind.org/) to manage documentation.
* The `docs/` folder contains the source files for the documentation website.
* To learn how to set it up and maintain the project website, follow the guide [[se-edu/guides] Working with Forked MarkBind sites](https://se-education.org/guides/tutorials/markbind-forked-sites.html).

**Style guidance:**

* Follow the [**_Google developer documentation style guide_**](https://developers.google.com/style).
* Also relevant is the [_se-edu/guides **Markdown coding standard**_](https://se-education.org/guides/conventions/markdown.html).


**Converting to PDF**

* See the guide [_se-edu/guides **Saving web documents as PDF files**_](https://se-education.org/guides/tutorials/savingPdf.html).

### Configuration

Certain properties of the application can be controlled (e.g user preferences file location, logging level) through the configuration file (default: `config.json`).

--------------------------------------------------------------------------------------------------------------------

## Appendix: Requirements

### Product scope

**Target user profile**:

* secondary school tutors
* teaching a wide range of subjects in the secondary school curriculum **(Secondary 1 to 5)**
* manage students with a volatile environment (may sign up or quit midway)
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Tutors often struggle to manage multiple students across different classes, especially when enrollment changes frequently. TutorTrack provides an integrated way to manage both classes and students by:

* Linking each class to its list of enrolled students
* Linking each student to their list of assignments


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                           | I want to …​                                                | So that I can…​                                                                                   |
|----------|-----------------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `* * *`  | private tutor                     | add a student to my list                                    | I can keep track of who I am teaching.                                                            |
| `* * *`  | private tutor                     | delete a student from my current list                       | I don’t have clutter from students I no longer teach.                                             |
| `* * *`  | private tutor                     | add a class to a student                                    | I can keep track of which class a student belongs to. Class info includes subject and class time. |
| `* * *`  | private tutor                     | delete a class to a student                                 | I can remove a class from a student if they quit/graduate.                                        |
| `* * *`  | private tutor                     | see a list of all active students                           | I can review all my students at a glance.                                                         |
| `* * *`  | private tutor                     | add an assignment to each student in a class                | I can assign each student in a class an assignment                                                |
| `* * *`  | private tutor                     | delete an assignment from each student in a class           | I can remove the homework or practice tasks a student has completed                               |
| `* * *`  | private tutor                     | exit the app safely with data saved                         | I can resume work later without losing progress.                                                  |
| `* * *`  | private tutor                     | find students by name                                       | I can get a student's information easily                                                          |
| `* * * ` | organised private tutor           | filter students by class                                    | I can check students enrolled in a class                                                          |
| `* *`    | private tutor                     | record notes for a tutoring session for a particular student | I can review what topics were covered and where the student struggled.                            |
| `* *`    | private tutor                     | edit a student's name/subject/phone                   | I can edit a mistake I did/update any changes in student information                              |
| `* *`    | private tutor                     | add a grade to a completed assignment                       | I can track the student’s performance across assignments.                                         |
| `* *`    | private tutor                     | mark an assignment as completed                             | I can differentiate between pending and finished work.                                            |
| `* *`    | private tutor                     | undo my last action                                         | I can recover from mistakes quickly                                                               |


### Use cases

**Use case: Add a student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Add a new student to track classes and assignments.

**Preconditions**
* TutorTrack is running
* The tutor has the student’s name, phone number, and level.

**Minimal Guarantees**
* No partial/unknown student is created; application state remains consistent.

**Success Guarantees**
* The new student is added to the model and persisted to storage

**MSS**
1. Tutor initiates `add` with the following student information:
   * Name
   * Phone Number
   * Level
   * Class (optional)
2. System validates details and checks for duplicates.
3. System adds the student and related records to storage.
4. System shows a success message and updates the list.

Use case ends.

**Extensions**

* 1a. Malformed or incomplete input (missing required prefixes, non-empty preamble, duplicate prefixes, or invalid prefixes)
  * 1a1. Tutor provides input that does not match the `add` format.
  * 1a2. System rejects the input and shows an error message. \
  Use case ends.

* 2a. Duplicate student (same name and phone as an existing student)
  * 2a1. System shows an error message indicating that a duplicate student already exists.\
  Use case ends.

* 3a. Storage write fails after model change
  * 3a1. System restores prior consistent state and shows a storage failure message. \
  Use case ends.

* 4a. Duplicate name only (same name, different phone)
  * 4a1. System shows a warning that a student with the same name already exists.\
  Use case ends.

* 4b. Duplicate phone only (same phone, different name)
  * 4b1. System shows a warning that a student with the same phone already exists. \
  Use case ends.

**Use case: Delete a student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Remove a student who is no longer being taught.

**Preconditions**
* TutorTrack is running.
* A filtered or full student list is displayed with stable indexes.

**Minimal Guarantees**
* No data corruption; application state remains consistent.

**Success Guarantees**
* The target student is removed from the storage and the list.

**MSS**
1. Tutor initiates `delete` to a student in the list.
2. System validates the selected entry.
3. System removes the student and related records from storage.
4. System shows a success message and updates the list.

Use case ends.

**Extensions**

* 1a. Malformed index (missing, non-integer, or wrong format)
    * 1a1. Tutor provides a malformed or missing index.
    * 1a2. System rejects the input and shows an error message.
      Use case ends.

* 1b. No students displayed (empty filtered list)
    * 1b1. Tutor issues deletes a student while the filtered list is empty.
    * 1b2. System rejects the input and shows an error message.
      Use case ends.

* 2a. Index out of range (no student at that index in the current filtered list)
    * 2a1. Tutor provides an index greater than the displayed list size.
    * 2a2. System rejects the input and shows an error message.
      Use case ends.

* 3a. Storage write fails
  * 3a1. System restores prior consistent state and shows a storage failure message. \
    Use case ends.



**Use Case: Edit Student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Edit an existing student's details (name, phone, level).

**Preconditions**
* The target student is displayed and the provided index identifies a valid student.

**Minimal Guarantees**
* No partial edits; application state remains consistent.

**Success Guarantees**
* The student's details are updated in the model and persisted to storage.

**MSS**
1. Tutor initiates `edit` with an index and one or more editable fields (e.g., `n/`, `p/`, `l/`).
2. System validates the index and parses supplied fields.
3. System creates the edited student and checks for exact duplicate students in the address book.
4. System checks for duplicate name-only or phone-only and proceeds with edit.
5. System updates the model, persists changes, and shows a success message.

Use case ends.

**Extensions**
* 1a. Missing or invalid index / no editable fields provided
    * 1a1. System shows a validation error (invalid index / nothing to edit) and aborts.
    * 1a2. Tutor corrects input. \
      Use case resumes at step 1.

* 3a. Edited student would duplicate an existing student (same name and phone)
    * 3a1. System shows a duplicate student error and aborts. \
      Use case ends.

* 4a. Edited name or phone collides with existing students (name-only or phone-only)
    * 4a1. System performs the edit, persists it, and shows a success message that includes a warning that a student with name/phone already exists.

* 5a. Storage write fails after updating model
    * 5a1. System rolls back changes where possible, shows a failure message and aborts. \
      Use case ends.


**Use case: Add Class to Student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Add a class for a specific student.

**Preconditions:**
* At least one student is displayed.

**Minimal Guarantees:**
* No partial class is attached.

**Success Guarantees:**
* Student shows the new class in their class list; data is saved to storage.

**MSS:**
1. Tutor initiates `addclass` to a student in a class.
2. System validates details and checks for duplicate classes.
3. System adds the class to the student and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions:**
* 2a. Duplicate class (same class name)
  * 2a1. System shows an error and keeps the list unchanged. \
    Use case ends.

* 3a. Storage write fails:
  * 3a1. System removes class from the student and shows a failure message. \
    Use case ends.

**Use Case: Delete Class from Student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Remove a class from a student.

**Preconditions:**
* The student exists and has at least one class.

**Minimal Guarantees:**
* No data corruption; other classes remain.

**Success Guarantees:**
* The specific class is removed and the change is saved.

**MSS:**
1. Tutor initiates `deleteclass` for a student.
2. System validates the selected entry and checks whether the student has the specified class to delete.
3. System removes the class and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions:**
* 1a. Missing/invalid class details
    * 1a1. System shows specific validation errors and requests corrections.
    * 1a2. Tutor corrects input.

      Use case resumes at step 2.

* 2a. Student not enrolled in the specified class
  * 2a1. System shows an error and keeps the list unchanged. \
    Use case ends.

* 3a. Storage write fails
  * 3a1. System adds back class from the student and shows a failure message. \
    Use case ends.

**Use Case: Assign Assignment to Student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Assign an assignment to a student in a class.

**Preconditions**
* The student exists and is enrolled in the specified class.

**Minimal Guarantees**
* No data corruption; other assignments remain.

**Success Guarantees**
* Assignment appears in the student’s assignment list and data is saved.

**MSS**
1. Tutor initiates `assign` to a student in a class.
2. System validates details and checks for duplicate assignments.
3. System adds the assignment to the student and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions**
* 1a. Missing/invalid assignment details
  * 1a1. System shows specific validation errors and requests corrections.
  * 1a2. Tutor corrects input.

    Use case resumes at step 2.

* 2a. Duplicate assignment (same assignment belonging to same class)
  * 2a1. System shows an error and keeps the list unchanged. \
    Use case ends.

* 3a. Storage write fails
  * 3a1. System removes assignment from the student and shows a failure message. \
    Use case ends.

**Use Case: Delete Assignment from Student**

**Primary Actor:** Secondary School Tutor \
**Goal:** Remove an assignment from a student.

**Preconditions**
* The student exists and has the specified assignment.

**Minimal Guarantees**
* No data corruption; other assignments remain.

**Success Guarantees**
* The specific assignment is removed and the change is saved.

**MSS**
1. Tutor initiates `unssign` for a student in a class.
2. System validates the selected entry and checks whether the student has the specified assignment to delete.
3. System removes the assignment and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions**
* 1a. Missing/invalid assignment details
  * 1a1. System shows specific validation errors and requests corrections.
  * 1a2. Tutor corrects input.

    Use case resumes at step 2.

* 2a. Student does not have the specified assignment
  * 2a1. System shows an error and keeps the list unchanged. \
    Use case ends.

* 3a. Storage write fails
  * 3a1. System rolls back changes where possible and shows a failure message. \
    Use case ends.


**Use Case: Assign Assignment to All Students in Class**
**Primary Actor:** Secondary School Tutor \
**Goal:** Assign an assignment to every student in a specified class group.

**Preconditions**
* At least one student exists in the specified class group.

**Minimal Guarantees**
* No data corruption; other classes and assignments remain unchanged.

**Success Guarantees**
* The assignment is added to all students in the class who did not already have it, and data is saved.

**MSS**
1. Tutor initiates `assignall` with `c/CLASS` and `a/ASSIGNMENT`.
2. System locates all students who belong to the specified class group.
3. System skips students who already have that assignment, for others it adds the assignment.
4. System updates the model and persists changes.
5. System reports the number of students assigned.

Use case ends.

**Extensions**
* 1a. Missing or empty `c/CLASS` or `a/ASSIGNMENT`
    * 1a1. System shows a specific error (class not provided / assignment not added) and aborts.
    * 1a2. Tutor provides missing input. \
      Use case resumes at step 1.

* 2a. No students found in the specified class group
    * 2a1. System shows a "class does not exist" error and aborts. \
      Use case ends.

* 2b. No students were assigned because all already had the assignment
    * 2b1. System shows an "already assigned" error and aborts. \
      Use case ends.

* 4a. Storage write fails during updates
    * 4a1. System rolls back changes where possible, shows a failure message and aborts. \
      Use case ends.

**Use Case: Mark Assignment for Student(s)**
**Primary Actor:** Secondary School Tutor \
**Goal:** Mark an assignment as completed for one or more students.

**Preconditions**
* The target students are displayed (filtered or full list).
* Each target student exists and is enrolled in the specified class and has the specified assignment.

**Minimal Guarantees**
* No data corruption; other assignments and students remain unchanged.

**Success Guarantees**
* The specified assignment is marked as completed for the selected student(s) and data is saved.

**MSS**
1. Tutor initiates `mark` with one or more indices (or ranges), a `c/CLASS` and `a/ASSIGNMENT`.
2. System validates indices and that each referenced student exists in the displayed list.
3. System checks that each student has the specified assignment.
4. System marks the assignment for students that are not already marked, updates model and persists changes.
5. System shows a success message listing students whose assignment was marked.

Use case ends.

**Extensions**
* 1a. Malformed indices or preamble missing
    * 1a1. System shows an error message.
    * 1a2. Tutor corrects input. \
      Use case resumes at step 1.

* 3a. One or more students do not have the requested assignment
    * 3a1. System shows an error naming those students and aborts without marking. \
      Use case ends.

* 3b. All targeted students already have the assignment marked
    * 3b1. System shows an "already marked" error and aborts. \
      Use case ends.

* 4a. Storage write fails while persisting changes
    * 4a1. System rolls back changes where possible, shows a failure message and aborts. \
      Use case ends.

**Use Case: View All Active Students**

**Primary Actor:** Secondary School Tutor \
**Goal:** See the complete list of currently stored students.

**Preconditions**
* TutorTrack is running.

**Minimal Guarantees**
* System shows the current list state (even if empty).

**Success Guarantees**
* All active students are displayed.

**MSS**
1. Tutor initiates `list` to view all students in TutorTrack.
2. System retrieves and displays all students.

   Use case ends.

**Extensions**
* 2a. No students exist
  * 2a1. System shows “Listed all students” message.
  Use case ends.


### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. The product supports only **one user per local data file**; concurrent multi-user access is not expected.
3. All data is stored locally in a **human-readable text file** (e.g., JSON), allowing advanced users to edit it manually.
4. No installer is required — the program can be run directly from a single **JAR file**.
5. The system operates fully offline and has **no dependency on internet connectivity** or remote servers.
6. Platform independence is required; the software must avoid OS-specific libraries so it runs consistently across Windows, macOS, and Linux.
7. Deliverables must be lightweight: the JAR file ≤ **100 MB** and each PDF document ≤ **15 MB** to ensure easy portability and exam usability.
8. Startup time should not exceed **2 seconds** on a modern laptop (e.g., Intel i5/Apple M1 with 8 GB RAM).
9. User commands must execute within **1 second** under typical usage conditions (≤ 1000 students).
10. In the event of a failed storage write, the application must **roll back changes** to prevent data corruption.
11. Data integrity must be preserved across restarts; students and assignments saved before exit should remain consistent on relaunch.
12. On invalid inputs or corrupted files, the program is expected to **fail gracefully** with clear, informative error messages (e.g., “Invalid command format! add: n/NAME p/PHONE l/LEVEL \[c/CLASS]...”), without crashing.
13. The user interface must remain usable on screens with at least **1024×768 resolution**, without scrolling needed for core features.
14. Deliverables should exclude unnecessary third-party libraries or oversized media assets, ensuring files are not bloated.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Student**: A core entity in TutorTrack representing an **individual learner**, with a name, academic level, phone number, classes, and assignments.
* **Assignment**: A **task linked to a student**, containing a name and completion status.
* **Class Group**: A **scheduled lesson** that groups students and assignments.
* **Level**: The **academic year** of a student, limited to **Secondary 1 – 5**.
* **Phone Number** : The student’s **contact number**, restricted to **at least 3 digits**.
* **Duplicate student**: A student with the **same name and phone number** as an existing student.
* **Filtered list**: A **subset of the student list** shown after running commands such as `find` or `filter`.
* **Index**: A **1-based position** of a student in the currently displayed list.
* **Status message**: A **text message** displayed in the app’s status bar to inform the user about the result of their last action.
* **Command format**: The **syntax** a user must follow when entering commands (e.g., `add n/NAME p/PHONE l/LEVEL`).
* **Valid command format error**: An **error message** displayed when the command syntax does not follow the required format (e.g., missing parameters).
* **Model**: holds the app data and exposes the API the rest of the app uses (e.g., filtered lists, commit/undo). Explains state ownership and responsibilities.
* **Logic**: parses user input and executes commands; mediates between UI and Model. Clarifies command flow and error handling.
* **UI**: presentation layer and user interaction (views, fxml, command box, result display). Useful to explain observable lists and bindings.
* **Storage**: persistence layer (read/write JSON, file paths, rollback on failure). Helps set expectations about durability and failure modes.
* **Storage file**: The file **`data/tutortrack.json`**, where TutorTrack **saves and loads all data**.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for Manual Testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample students. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
     Expected: The most recent window size and location is retained.


### Adding a student

1. Adding a student while all students are being shown

    1. Test case: `add n/John Doe p/12345678 l/3`<br>
       Expected: Student with specified name, phone number and level is added to the list. Details of the added student shown in the status message.

    1. Test case: `add n/John Doe p/12345678`<br>
       Expected: No student is added. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect add commands to try: `add`, `add x`, `...` (where x is not a class, level or phone number)<br>
       Expected: Similar to previous.

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command. Multiple students in the list.

   1. Test case: `delete 1`<br>
      Expected: First student is deleted from the list. Details of the deleted student shown in the status message.

   1. Test case: `delete 0`<br>
      Expected: No student is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Editing a student

1. Editing a student with all students being shown

    1. Prerequisites: List all students using the `list` command. Multiple students in the list. First student's name is not **"John Doe"**.

    1. Test case: `edit 1 n/John Doe`<br>
       Expected: First student's name is updated to **"John Doe"**. Details of the edited student shown in the status message.

    1. Test case: `edit 0`<br>
       Expected: No student is edited. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect edit commands to try: `edit`, `edit x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

### Adding a class

1. Adding a class to a student

    1. Prerequisites: List all students using the `list` command. First student does not have the class **"Physics-1800"**.

    1. Test case: `addclass 1 c/Physics-1800`<br>
       Expected: Class **"Physics-1800"** is added to the first student in the list. Details of the first student shown in the status message.

    1. Test case: `addclass 1`<br>
       Expected: No class is added. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect addclass commands to try: `addclass`, `addclass x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

### Deleting a class

1. Deleting a class from a student

    1. Prerequisites: List all students using the `list` command. First student has the class **"Physics-1800"**.

    1. Test case: `deleteclass 1 c/Physics-1800`<br>
       Expected: Class **"Physics-1800"** is deleted from the first student in the list. Details of the first student shown in the status message.

    1. Test case: `deleteclass 1`<br>
       Expected: No class is deleted. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect deleteclass commands to try: `deleteclass`, `deleteclass x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

### Adding an assignment from a student

1. Adding an assignment to a student

    1. Prerequisites: List all students using the `list` command. First student has the class **"Physics-1800"** and does not have the assignment **"ScienceTopic2"** in that class.

    1. Test case: `assign 1 c/Physics-1800 a/ScienceTopic2`<br>
       Expected: Assignment **"ScienceTopic2"** is added to the first student in the list. Details of the first student shown in the status message.

    1. Test case: `assign 1`<br>
       Expected: No assignment is added. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect assign commands to try: `assign`, `assign 1 c/x a/y`, `...` (where x is a class that the first student does not have)<br>
       Expected: Similar to previous.

### Deleting an assignment from a student

1. Deleting an assignment from a student

    1. Prerequisites: List all students using the `list` command. First student has the class **"Physics-1800"** and has the assignment **"ScienceTopic2"** in that class.

    1. Test case: `unassign 1 c/Physics-1800 a/ScienceTopic2`<br>
       Expected: Assignment **"ScienceTopic2"** is deleted from the first student in the list. Details of the first student shown in the status message.

    1. Test case: `unassign 1`<br>
       Expected: No assignment is deleted. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect unassign commands to try: `unassign`, `unassign 1 c/x a/y`, `...` (where x is a class that the first student does not have)<br>
       Expected: Similar to previous.

### Adding an assignment to an entire class

1. Adding an assignment to all students in a class

    1. Prerequisites: List all students using the `list` command. Multiple students in the list have the class **"Physics-1800"** and do not have the assignment **"ScienceTopic3"** in that class.

    1. Test case: `assignall c/Physics-1800 a/ScienceTopic2`<br>
       Expected: Assignment **"ScienceTopic3"** is added to all students in the list who have class **"Physics-1800"**. Success message shown with the number of students the assignment was added to.

    1. Test case: `assignall `<br>
       Expected: No assignment is added to any student. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect assignall commands to try: `assignall c/`, `assignall c/x a/y`, `...` (where all students in class x already have assignment y)<br>
       Expected: Similar to previous.

### Deleting an assignment from an entire class

1. Deleting an assignment from all students in a class

    1. Prerequisites: List all students using the `list` command. Multiple students in the list have the class **"Physics-1800"** and have the assignment **"ScienceTopic3"** in that class.

    1. Test case: `unassignall c/Physics-1800 a/ScienceTopic2`<br>
       Expected: Assignment **"ScienceTopic3"** is deleted from all students in the list who have class **"Physics-1800"**. Success message shown with the number of students the assignment was deleted from.

    1. Test case: `unassignall `<br>
       Expected: No assignment is deleted from any student. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect unassignall commands to try: `unassignall c/`, `unassignall c/x a/y`, `...` (where all students in class x do not have assignment y)<br>
       Expected: Similar to previous.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Effort**

A large portion of the team's development time was spent designing and implementing the `Assignment` model. The main difficulty was deciding how `Assignment` should relate to `ClassGroup`.

After considering several alternatives (for example, making `Assignment` a standalone entity shared across classes, or making it owned entirely by `ClassGroup`), the team settled on an association relationship where each `Assignment` must be tagged to a `ClassGroup`. Concretely, an `Assignment` cannot exist in isolation in the system — it is always associated with a particular `ClassGroup`.

Rationale and consequences:

- Rationale: In our target context (tutees and their classes), assignments are almost always issued within the context of a class. Tagging an assignment to a class models this real-world workflow directly and avoids ambiguity when multiple classes use similar assignment names.
- Implementation impact: Commands and storage were adjusted so that assignments are referenced together with their class (e.g., `ass1 (physics-1800)`), validation checks ensure a student belongs to the class before assigning/unassigning, and uniqueness/collision handling is scoped to a class.
- Trade-offs: This creates a tighter coupling between `Assignment` and `ClassGroup`, and makes some commands slightly more verbose. It also required extra design and test effort to get the semantics right, especially around assign/unassign operations and persistence/migration of existing data.

Two notable feature enhancements that consumed additional effort are described below.

### Multi-range support for `mark` / `unmark`

To improve productivity for tutors, we extended the `mark` and `unmark` commands to accept multiple indexes and index ranges in one command (for example: `mark 1-5 7 9-10 c/Math-2000 a/MathHW1`). This lets tutors mark or unmark many students in a single command instead of issuing repeated single-index commands (unlike AB3 which accepted only a single index per command).

Design and implementation notes:

- Parser: We updated the `MarkAssignmentCommandParser` / `UnmarkAssignmentCommandParser` to accept and normalize a sequence of tokens that represent either a single positive index or a `START-END` range. Ranges are expanded into individual indexes while preserving order and de-duplicating indexes where appropriate.
- Validation: We perform strict validation on indexes and ranges (e.g., positive integers, `START <= END`, indexes within the current displayed list bounds) and surface clear error messages if the input is malformed.
- Efficiency and correctness: The command implementation iterates the resolved list of target indexes and applies the per-student mark/unmark logic (including class membership checks and assignment existence checks). Where sensible, shared logic was reused from the single-index `mark`/`unmark` implementation to keep behaviour consistent and reduce duplicated code.
- Recommended workflow: Because indexes refer to positions in the currently displayed list, we recommend using `filter c/CLASS` first to limit the list to a single class, then use an index range such as `1-10` to affect an entire class quickly. This both matches tutor workflows and avoids ambiguous index targets.
- Testing: We added unit and integration tests for a range of cases: single indexes, multiple single indexes, single ranges, combinations of ranges and single indexes, overlapping ranges, out-of-bound indexes, and invalid formats.

### `assignall` / `unassignall` commands

We added `assignall` and `unassignall` to support class-level assignment management: `assignall c/CLASS a/ASSIGNMENT` assigns a particular assignment to every student in the specified class (skipping students who already have the assignment), and `unassignall` removes an assignment from every student in a class.

Design and implementation notes:

- Rationale: Tutors commonly assign work to an entire class rather than one student at a time. `assignall` and `unassignall` implement this real-world workflow directly and reduce repetitive operations for the user.
- Code reuse: The commands reuse core validation and logic from the existing `assign`/`unassign` implementations (for example: class membership checks, duplicate assignment checks, and model updates). This reduced implementation effort and ensured consistent behaviour across the per-student and per-class APIs.
- Edge cases: Commands intentionally skip students who already meet the target state (e.g., already have the assignment for `assignall`, or do not have the assignment for `unassignall`). When no student is affected, user-facing errors are produced to indicate that the operation made no changes.
- Testing: We added tests to cover the normal path, the path where some students are affected and some are not, the path where no students in the class exist, and the path where none of the students needed updating (which should surface the appropriate error message).

Overall, the decision to associate `Assignment` with `ClassGroup` and to add these command-level conveniences increased initial design and test effort, but produced a clearer user model and a more efficient workflow for tutors — which aligns better with our target users' mental model and daily use patterns.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancements**

Team size: 5

1. **Make accepted `PHONE` formats more flexible and accurate:**
Currently, we accept phone numbers that contain only numbers and are at least 3 digits long.
We plan to make phone numbers at least 8 digits long and accept formats with
spaces, dashes, plus signs or brackets (e.g., "9123 4567", "912-34567", "+65 91234567", "(1800)9100-0019").
2. **Improve `NAME` input validation to detect invalid prefix:**
Currently, we only throw an error message when the user inputs invalid prefixes behind `n/`
prefix with a space between them, e.g., `add n/John Doe a/hw1`, but allow invalid
prefixes when there is no space, e.g., `add n/John Doea/hw1`.
We plan to allow such cases, but display an additional warning message to inform the user of a possible invalid prefix, if this was unintentional, to avoid confusion.
3. **Extend `filter` to support additional fields and substring matching:**
Currently, the `filter` command only supports filtering by class.
We plan to extend `filter` so it accepts additional prefixes (`l/LEVEL`, `n/NAME`, `a/ASSIGNMENT`), performed by case-insensitive substring matching (e.g., `filter c/Math-100` matches `Math-1000`).
4. **Improve `mark` / `unmark` error and partial-failure reporting to list affected students:**
Currently, `mark` / `unmark` returns a generic error when an assignment is not found for some targets (e.g., `Assignment 'hw1' of specified class not found for specified student(s)`) without displaying which students failed and why.
We plan to change the success message to list per-student outcomes (success or explicit reason for skip/failure) in a format consistent with existing success messages.
5. **Account for whitespace for duplicate checking of `Name`, `Assignment`, `ClassGroup`**:
Currently, users can input similar names that include whitespaces (e.g., `addclass 1 c/Math2000` and `addclass 1 c/Math 2000`) for these fields.
We plan to account for whitespace by still allowing these inputs, but giving a warning message:  `"Warning: A similar class c/Math 2000 already exists."`
6. **Allow edits of `ClassGroup` and `Assignment` in `edit` command**:
Currently, editing a student’s `ClassGroup`s or `Assignment`s requires per-student `deleteclass` / `addclass`, or `unassign` / `assign` commands, respectively.
We plan to enhance the existing `edit` command to allow renaming of `ClassGroup` or an `Assignment` (within a specific `ClassGroup`) across all students who have that class or assignment.
