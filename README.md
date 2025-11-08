Pilgrim Pass – Android Mobile Application

Pilgrim Pass is a mobile application designed to help manage large crowds during major religious events such as the public worship of the Sacred Tooth Relic in Sri Lanka. The app allows pilgrims to book time slots, receive a digital QR entry pass, and access navigation features, while administrators can scan QR codes and monitor visitor numbers.

📌 Real-World Problem

During previous exhibitions of the Sacred Tooth Relic, thousands of devotees arrived suddenly without any booking system. Since organizers had no way to know how many people were planning to visit, large unexpected crowds formed. Some devotees managed to enter, but many waited for hours or were unable to visit due to overcrowding. The lack of visitor prediction caused long queues, conflicts, safety concerns, and mismanagement.

Pilgrim Pass solves this by letting users book their visit in advance. This helps organizers see the number of expected visitors for each date and time slot so they can plan crowd control, staffing, and security in a more organized way.

📌 Features
For Pilgrims

User registration and login

Date and time slot booking

Automatic QR pass generation

Google Maps navigation

Weather information

Profile management

Temple image gallery

For Admins

QR code scanning and verification

Admin dashboard

View all bookings

View basic statistics

🎯 Purpose of the App

The app was created to eliminate overcrowding, long queues, and confusion during important religious events. By knowing how many people will visit in each time slot, organizers can ensure a safer and more peaceful environment for all pilgrims.

🧩 Technologies Used
Frontend

Android Studio

Java

XML for UI layouts

Backend and Logic

Local in-app logic (current version)

Designed to support future Firebase integration

APIs and Integrations

ZXing BarcodeEncoder for QR generation

Google Maps API

Weather API

Camera API for scanning

Android runtime permissions

Build Tools

Gradle

GitHub for version control

📂 Project Structure
Pilgrim-Pass-App-Android-Studio/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/      # Java source code
│   │   │   ├── res/       # Layouts and resources
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
🚀 How to Run the Project

Clone the repository
git clone https://github.com/sithu0302/Pilgrim-Pass-App-Android-Studio.git
Open the project using Android Studio

Allow Gradle to sync

Connect a phone or start an emulator

Press Run to launch the app
✅ Future Improvements

Firebase user authentication

Online booking management

Encrypted QR data

Multi-language support

Push notifications

Detailed analytics for admins
