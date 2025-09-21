Collaborative Workflow & Task Division
This section is updated to reflect the new requirements. The core pull -> work -> commit -> push cycle is still your guide.
Guiding Principle: ALWAYS PULL FIRST!
Before starting work, always pull the latest changes to ensure you're up-to-date and to prevent merge conflicts.
Refined Task Division 📝
The work is split to minimize overlap and allow parallel development.
Ranzel's Responsibilities (Main Window & Core Logic) 🧑‍💻
Your focus is on the primary application window and managing the contact list itself.
Contact.java Model: Create the data model with Name, Phone, and Email properties.
ContactManager.java - Main View:
Set up the TableView with the three columns (Name, Phone, Email).
Create and layout all the buttons: Add, Edit, Delete, Clear All, and Show Totals.
Button Functionality:
Implement the logic for the Delete button.
Implement the logic for the Clear All button. This will need to call the confirmation dialog that Lian creates.
Implement the logic for the Show Totals button, which will call the info dialog Lian creates.
Integrate the Add/Edit buttons to open the ContactForm dialog that Lian builds.
Lian's Responsibilities (Forms & Dialogs) 🤝
Your focus is on all secondary windows and user feedback pop-ups.
ContactForm.java Dialog:
Build the complete UI for the add/edit contact form (layout, labels, text fields).
Input Validation Logic:
Implement the validation for the form. Instead of "negative/zero amounts" (which applies to numbers), your validation should check for things relevant to contacts:
Error if a field is empty.
Error if the email format is invalid (e.g., doesn't contain an "@" symbol).
Reusable Dialog Methods:
Create helper methods that Ranzel can also use for the dialogs:
An Error Dialog method for your form validation.
A Confirmation Dialog method for the "Clear All" feature.
An Info Dialog method that takes a message, which will be used for showing the total number of contacts.

Example Workflow with New Tasks
Ranzel sets up the project, creates the initial Contact.java model, commits, and pushes.
Ranzel tells Lian: "Project is set up and the Contact model is pushed."
Lian pulls the project. She can immediately start working on ContactForm.java and the generic helper methods for the dialogs, as they don't depend on the main window yet.
Simultaneously, Ranzel works in ContactManager.java to build the TableView and lay out all the new buttons (Clear All, Show Totals, etc.).
Lian finishes the contact form and the reusable dialog methods. She commits with a message like "Feat: Implement ContactForm and all dialog helpers" and pushes her work.
Lian tells Ranzel: "The form and all the dialogs are ready to be used. You can pull them now."
Ranzel pulls Lian's changes. Now he has everything he needs to connect the pieces:
He wires his "Clear All" button to call Lian's confirmation dialog method.
He wires his "Show Totals" button to get the size of the contact list and show it using Lian's info dialog method.
He wires the "Add" and "Edit" buttons to open an instance of Lian's ContactForm.
This workflow allows you both to work in parallel on separate parts of the application before integrating them at the end.

