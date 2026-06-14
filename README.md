# University Course Registration System

An Object-Oriented Java desktop application that simulates a real-world university registration and grading system.

The project has clean software architecture that is achieved by separating the User Interface, Business Logic, and Data Storage layers. It handles complex academic rules, including class capacity limits, schedule conflict prevention, and prerequisite validation.

## Key Features

### System Design
* **Layered Architecture:** Clear separation between Models, Repositories, Services, and UI components.
* **Object-Oriented Principles:** Utilizes inheritance, encapsulation, and dependencies to keep the codebase modular and testable.
* **Java Swing UI:** Uses base classes to prevent code duplication across different user dashboards and ensures data tables remain read-only.

### User Roles
* **Administrator:** Creates base courses, sets up prerequisites, opens sections for upcoming semesters, assigns lecture times (TimeSlots), and assigns professors to classes.
* **Student:** Browses the course catalog and registers for classes. The system automatically blocks registration if the class is full, if there is a schedule conflict, or if the student hasn't met the prerequisites. Students can also drop classes and view their transcript/GPA.
* **Professor:** Views their assigned teaching schedule, checks real-time class rosters, and assigns final grades to students.

## Technologies Used
* **Language:** Java
* **UI Framework:** Java Swing
* **Build Tool:** Maven
* **Testing:** JUnit 4

## How to Run

1. download this repository.
2. open the project in your preferred Java IDE (such as IntelliJ IDEA, Eclipse, or VS Code).
3. locate the `Main.java` file inside the `com.university` package.
4. run the `Main.java` file to launch the application.

## Testing

This project includes a JUnit test suite that verifies the core business logic in the Service layer, ensuring that schedule conflicts, capacity limits, and prerequisites function correctly. 

Since the project uses Maven, all unit tests can be run directly through IDE
