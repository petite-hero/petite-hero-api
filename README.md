# [PETITE HERO](https://github.com/petite-hero) - FPT University Capstone Project

__*Petite Hero - Children Management and Development Application*__ is an inter-specialization project for FPT University graduation thesis, realized by Software Engineering and Graphic Design students.

The project's product consists of 4 main components:
- A [Server](https://github.com/petite-hero/petite-hero-api) for requests handling
- A [Mobile Application](https://github.com/petite-hero/petite-hero-mobile) for parents
- A [Smartwatch Application](https://github.com/petite-hero/petite-hero-smartwatch) for children
- A [Web Application](https://github.com/petite-hero/petite-hero-web) for administrators

__*Official TVC*__: https://youtu.be/8nKs7YcEEaE  
__*Project Demonstration*__: https://youtu.be/i3ZhxKtDCWA  

# Contributors

__*Software Engineering Specialization*__:
- [Nguyễn Phú Hưng](https://github.com/hulk1999)
- [Võ Lam Trường](https://github.com/truongvlit)
- [Lâm Lệ Dương](https://github.com/llduong)
- [Âu Đức Tuấn](https://github.com/ibenrique2510)

__*Graphic Design Specialization*__:
- Hàn Lê Khanh
- Trần Nguyễn An Khang

# 
# Petite Hero - Server
- Language: Java
- Framework: Spring Boot
- Developed on IntelliJ

![](screenshots/overview.png)

# Functionalities
1. Authentication, OTP Verification, Password Encryption
2. CRUD for Accounts, Locations, Tasks, Achievements
3. Payment Handling with Paypal
4. Notification Handling with Firebase & Expo
5. Cron Jobs for Database Backup, Location & Task Updates 

# Installation Instructions
1. Install Apache Maven: https://maven.apache.org/download.cgi. Download binary zip archive (Windows), binary tar.gz archive (Linux)
2. Setting maven environment: https://www.tutorialspoint.com/maven/maven_environment_setup.htm
3. Go to project root, run "mvn spring-boot:run"
4. Check 127.0.0.1:8080/swagger-ui.html for API list

# System Flows
## 1. Connecting Mobile & Smartwatch Devices
![](screenshots/connect-device.png)
## 2. Location Reporting
**Normal Flow**
![](screenshots/tracking.png)
**Live Update on Mobile**
![](screenshots/emergency.png)
## 3. Cron Job for Updating Location List
![](screenshots/location-cronjob.png)
