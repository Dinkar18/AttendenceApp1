package com.example.attendenceapp.ModelClass;

public class FacultyRegistrationModal {

        private String fullName, email, password, facultyID, phone;

        public FacultyRegistrationModal(String fullName, String email, String password, String facultyID, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.facultyID = facultyID;
            this.phone = phone;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFacultyID() {
            return facultyID;
        }

        public void setFacultyID(String facultyID) {
            this.facultyID = facultyID;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
