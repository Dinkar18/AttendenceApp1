Attendance Management App

Overview

The Attendance Management App is an Android application that enables students and faculty members to efficiently manage attendance records. The app is built using Android Studio with Firebase Firestore as the backend.

Features

User Authentication: Sign up and sign in with Firebase Authentication.

Role-Based Access: Separate functionalities for students and faculty.

Attendance Tracking: Students can mark attendance, and faculty can view records.

Firestore Database Integration: Secure and scalable data storage.

Real-time Updates: Instant syncing of attendance records.

Secure Data Handling: Firestore security rules to restrict unauthorized access.

Tech Stack

Frontend: Java (Android Studio)

Backend: Firebase Firestore

Authentication: Firebase Authentication

UI Components: XML, Material Design

Installation & Setup

Prerequisites

Install Android Studio (latest version)

Create a Firebase Project in Firebase Console

Enable Firebase Authentication (Email & Password Sign-In)

Enable Firestore Database and set rules

Steps to Setup

Clone this repository:

git clone https://github.com/your-repository/Attendance-Management-App.git
cd Attendance-Management-App

Open the project in Android Studio.

Connect the app to Firebase:

Go to Tools > Firebase.

Select Authentication and follow the setup steps.

Update google-services.json in the app/ folder.

Run the app on an Android emulator or physical device.

Firestore Database Structure

Firestore Root
│── users
│   ├── {UID_1}
│   │   ├── fullName: "John Doe"
│   │   ├── role: "student"
│   │   ├── studentID: "S123"
│   ├── {UID_2}
│   │   ├── fullName: "Dr. Smith"
│   │   ├── role: "faculty"
│   │   ├── facultyID: "F456"
│── attendance
│   ├── {UID_1}
│   │   ├── date: "2024-03-27"
│   │   ├── status: "Present"

Firestore Security Rules

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
    match /attendance/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}

Future Enhancements

Admin Panel: A web dashboard for managing attendance records.

QR Code Attendance: Students scan QR codes for automatic attendance marking.

Push Notifications: Alerts for attendance status.

Offline Mode: Store data locally and sync when online.

Contributing

Feel free to contribute by submitting pull requests. Ensure to follow coding standards and include proper documentation.
