package com.example.attendenceapp.ModelClass;

public class StudentRegistrationModal {


        public String fullName, email, password, studentID, department, year, phone;

        public StudentRegistrationModal(String fullName, String email, String password, String studentID, String department, String year, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.studentID = studentID;
            this.department = department;
            this.year = year;
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

        public String getStudentID() {
            return studentID;
        }

        public void setStudentID(String studentID) {
            this.studentID = studentID;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

