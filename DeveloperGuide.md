<frontmatter>
  title: "Developer Guide"
</frontmatter>

# Developer Guide

<div id="toc">

<!-- MarkBind will generate the table of contents here -->
</div>

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

<span class="fas fa-lightbulb" aria-hidden="true"></span> <strong>Tip:</strong> The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* tuition centre tutors
* teaching a wide range of subjects and student levels
* manage students with volatile environment (may sign up or quit midway)
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Tutors often struggle to manage multiple students across different classes, especially when enrollment changes frequently. TutorTrack provides an integrated way to manage both classes and students by:

* Linking each class to its list of enrolled students
* Linking each student to their list of assignments


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                           | I want to …​                                                 | So that I can…​                                                                                   |
|----------|-----------------------------------|--------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `* * *`  | private tutor                     | add a student to my list                                     | I can keep track of who I am teaching.                                                            |
| `* * *`  | private tutor                     | delete a student from my current list                        | I don’t have clutter from students I no longer teach.                                             |
| `* * *`  | private tutor                     | add a class to a student                                     | I can keep track of which class a student belongs to. Class info includes subject and class time. |
| `* * *`  | private tutor                     | delete a class to a student                                  | I can remove a class from a student if they quit/graduate.                                        |
| `* * *`  | private tutor                     | see a list of all active students                            | I can review all my students at a glance.                                                         |
| `* * *`  | private tutor                     | add an assignment to each student in a class                 | I can assign each student in a class an assignment                                                |
| `* * *`  | private tutor                     | delete an assignment from each student in a class            | I can remove the homework or practice tasks a student has completed                               |
| `* * *`  | private tutor                     | exit the app safely with data saved                          | I can resume work later without losing progress.                                                  |
| `* * *`  | private tutor                     | find students by name                                        | I can get a student's information easily                                                          |
| `* * * ` | organised private tutor           | filter students by class                                     | I can check students enrolled in a class                                                          |
| `* *`    | private tutor                     | record notes for a tutoring session for a particular student | I can review what topics were covered and where the student struggled.                            |
| `* *`    | private tutor                     | edit a student's name/subject/contact                        | I can edit a mistake I did/update any changes in student information                              |
| `* *`    | private tutor                     | add a grade to a completed assignment                        | I can track the student’s performance across assignments.                                         |
| `* *`    | private tutor                     | search for a student by partial name or subject              | I can quickly retrieve details even if I do not remember exact spellings.                         |
| `* *`    | private tutor                     | mark an assignment as completed                              | I can differentiate between pending and finished work.                                            |
| `* *`    | private tutor                     | undo my last action                                          | I can recover from mistakes quickly                                                               |
| `* *`    | private tutor                     | tag students with labels                                     | I can group and filter them by specific keywords                                                  |
| `* *`    | private tutor                     | add recurring sessions for a student                         | I can avoid re-entering the same lesson schedule every week                                       |
| `* *`    | private tutor                     | secure my account with a password                            | I can keep personal info of my students private                                                   |
| `* *`    | busy private tutor                | filter students by day                                       | I can check which student has active classes on a particular day                                  |
| `* *`    | busy private tutor                | receive a warning before deleting a student or assignment    | I can prevent accidental loss of important information                                            |
| `* *`    | beginner user of the app          | see the app populated with sample data                       | I can play around with the data and familiarise myself with the app's features                    |
| `* *`    | beginner user of the app          | be shown where my commands went wrong                        | I can understand which commands to use properly                                                   |
| `* *`    | beginner user of the app          | see a list of commands to use on the app                     | I can try out every command and get myself familiarised with the app                              |
| `* *`    | organised private tutor           | delete/archive students without active class                 | I can reduce clutter                                                                              |
| `* *`    | organised private tutor           | add due dates to assignments                                 | I can ensure my students submit their assignments on time                                         |
| `* *`    | organised private tutor           | see the latest edits to my list                              | I can keep track of where I last left off                                                         |
| `* *`    | organised private tutor           | filter by students who haven't paid for the previous month   | I can save time on tracking finances                                                              |
| `* *`    | organised private tutor           | link multiple subjects to one student                        | I can manage students I teach in more than one subject.                                           |
| `* *`    | expert user of the app            | create shortcuts for usual commands                          | I can save time                                                                                   |
| `* *`    | expert user of the app            | export my student details to a CSV file                      | I can back up or share the data in a standard format                                              |
| `* *`    | user ready to start using the app | clear all current data on the app                            | I can start anew with my own personal data                                                        |
| `* `     | busy private tutor                | delete/archive a class                                       | I can remove the tagged class from all students                                                   |

*{More to be added}*

### Use cases

**Use case: Add a person**

**Primary Actor:** Private Tutor
**Goal:** Add a new student to track lessons and assignments.

**Preconditions**
* TutorTrack is running
* The tutor has the student’s name, phone number (SG format), and level.

**Minimal Guarantees**
* No partial/unknown student is created.

**Success Guarantees**
* New student appears in the student list with saved details.

**MSS**

1.  Tutor initiates "add student" with information about:
   * Name
   * Phone Number
   * Level
   * Class (optional)
2. System validates details and checks for duplicates.
3. System creates the student and saves data.
4. System shows a success message and highlights the new student.

Use case ends.

**Extensions**

* 1a. Missing or malformed details
  * 1a1. System shows specific validation errors and requests corrections.
  * 1a2. Tutor corrects input.

    Use case resumes at step 2

* 2a. Duplicate (same name + same phone)
  * 2a1. System rejects and shows “student already exists”.
    Use case ends.

* 3a. Storage write fails
  * 3a1. System rolls back creation and shows a failure message.
    Use case ends.

**Use case: Delete a person**

**Primary Actor:** Private Tutor
**Goal:** Remove a student who is no longer being taught.

**Preconditions**
* At least one student is displayed (full or filtered list).

**Minimal Guarantees**
* No data corruption; list remains consistent.

**Success Guarantees**
* The target student is removed from the storage and the list.

**MSS**
1. Tutor deletes a student from the list based on the index.
2. System validates the selected entry.
3. System removes the student and related records from storage.
4. System shows a success message and updates the list.

Use case ends.

**Extensions**

* 1a. Invalid selection (index out of bounds or no item)
  * 1a1. System shows an error and keeps list unchanged. \
    Use case ends.

* 2a. Storage write fails
  * 2a1. System restores the student and shows a failure message. \
    Use case ends.

**Use case: Add Class to Student**

**Primary Actor:** Private Tutor
**Goal:** Add a class for a specific student.

**Preconditions:**
* At least one student is displayed.

**Minimal Guarantees:**
* No partial class is attached.

**Success Guarantees:**
* Student shows the new class in their class list; data is saved to storage.

**MSS:**
1. Tutor initiates “addclass” to student.
2. System validates details and checks for duplicate classes.
3. System adds the class to the student and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions:**

* 3a. Duplicate class (same class name)
  * 3a1. System shows an error and keeps the list unchanged.
    Use case ends.

* 4a. Storage write fails:
  * 4a1. System removes class from the student and shows a failure message.
    Use case ends.

**Use Case: Delete Class from Student**

**Primary Actor:** Private Tutor
**Goal:** Remove a class from a student.

**Preconditions:**
* The student exists and has at least one class.

**Minimal Guarantees:**
* No data corruption; other classes remain.

**Success Guarantees:**
* The specific class is removed and the change is saved.

**MSS:**
1. Tutor initiates “deleteclass” from student.
2. System validates the selected entry and checks whether the student has the specified class to delete.
3. System removes the class and saves data.
4. System shows success and updates the student’s details.

Use case ends.

**Extensions:**
* 2a. Missing/invalid class identification
  * 2a1. System shows an error and keeps the list unchanged.
    Use case ends

* 2b. Student not enrolled in the specified class
  * 2b1. System shows an error and keeps the list unchanged.
    Use case ends.

* 3a. Storage write fails
  * 3a1. System adds back class from the student and shows a failure message.
    Use case ends.

**Use Case: View All Active Students**

**Primary Actor:** Private Tutor
**Goal:** See the complete list of currently stored students.

**Preconditions**
* TutorTrack is running.

**Minimal Guarantees**
* System shows the current list state (even if empty).

**Success Guarantees**
* All active students are displayed.

**MSS**
1. Tutor initiates “list students”.
2. System retrieves and displays all students.

   Use case ends.

**Extensions**
* 2a. No students exist
  * 2a1. System shows “no records” message.

    Use Case Ends


*{More to be added}*

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. The product supports only **one user per local data file**; concurrent multi-user access is not expected.
5. All data is stored locally in a **human-readable text file** (e.g., JSON), allowing advanced users to edit it manually.
6. No installer is required — the program can be run directly from a single **JAR file**.
7. The system operates fully offline and has **no dependency on internet connectivity** or remote servers.
8. Platform independence is required; the software must avoid OS-specific libraries so it runs consistently across Windows, macOS, and Linux.
9. Deliverables must be lightweight: the JAR file ≤ **100 MB** and each PDF document ≤ **15 MB** to ensure easy portability and exam usability.
10. Startup time should not exceed **2 seconds** on a modern laptop (e.g., Intel i5/Apple M1 with 8 GB RAM).
11. User commands must execute within **1 second** under typical usage conditions (≤ 1000 students).
12. In the event of a failed storage write, the application must **roll back changes** to prevent data corruption.
13. Data integrity must be preserved across restarts; students and assignments saved before exit should remain consistent on relaunch.
14. On invalid inputs or corrupted files, the program is expected to **fail gracefully** with clear, informative error messages (e.g., “Invalid command format! add: n/NAME p/PARENT_PHONE l/Secondary{1..4}”), without crashing.
15. The user interface must remain usable on screens with at least **1024×768 resolution**, without scrolling needed for core features.
16. Deliverables should exclude unnecessary third-party libraries or oversized media assets, ensuring files are not bloated.


*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Student**: A core entity in TutorTrack representing an **individual learner**, with an academic level, parent contact, classes, and assignments.
* **Assignment**: A **task linked to a student**, containing a title, description, subject, due date, and completion status.
* **Class**: A **scheduled lesson** that groups students and assignments, associated with a subject and a time.
* **Level**: The **academic year** of a student, limited to **Secondary 1 – 4**.
* **PhoneNumber** : The parent’s **contact number** associated with a student, restricted to **Singapore format (+65XXXXXXXX)**.
* **Subject**: The **academic subject** linked to a class or assignment (e.g. _Physics_, _English_).
* **Duplicate student**: A student with the **same name and phone number** as an existing student.
* **Filtered list**: A **subset of the student list** shown after running commands such as `find` or `class`.
* **Command format**: The **syntax** a user must follow when entering commands (e.g., `add n/NAME p/PHONE_NUMBER l/LEVEL`).
* **Valid command format error**: An **error message** displayed when the command syntax does not follow the required format (e.g., missing parameters).
* **Storage file**: The file **`data/tutortrack.json`**, where TutorTrack **saves and loads all data**.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
     Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
