# 🌍 TravelLens - Hidden Places & Food Spots

**TravelLens** is a Spring Boot web application where users can discover, share, and manage hidden travel destinations, local food spots, and cultural places.

The platform allows users to create posts with images, search travel content, view public posts, and manage their own shared places.

---

## 📌 Project Overview

TravelLens was developed as a final web application project using **Spring Boot, PostgreSQL, JPA, Thymeleaf, and Spring Security**.

The main goal of this project is to create a simple, useful, and user-friendly travel sharing platform where people can post hidden places and food spots that are not commonly found on normal travel websites.

---

## 👥 Team Members

| Name                 | Role                        |
| -------------------- | --------------------------- |
| **KHADKA MOHAN**     | Developer                   |
| **KHARKA KUSHAL**    | Demo Driver                 |
| **BHATTARAI LAXMAN** | Database Manager / Deployer |

---

## ✨ Main Features

### 👤 User Features

* User registration
* User login and logout
* Secure authentication using Spring Security
* Create travel or food posts
* Upload image for each post
* View all public posts
* View post details
* Search posts
* View only the logged-in user's own posts
* Edit or delete own posts

### 🛠️ Admin Features

* Admin account support
* Admin can manage posts and users
* Role-based access control

### 📝 Post Features

* Title
* Description
* Location
* Category
* Image upload
* Author information
* Created date

---

## 💻 Technologies Used

### Backend

* Java 21
* Spring Boot 3.2
* Spring Web
* Spring Data JPA
* Spring Security
* Hibernate

### Frontend

* Thymeleaf
* HTML5
* CSS3
* Bootstrap Icons

### Database

* PostgreSQL

### Image Upload

* Cloudinary image upload support
* Default local image fallback

### Build and Deployment

* Maven
* Docker
* Render
* GitHub

---

## 📁 Project Structure

```text
travellens-hidden-spots/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/travellens/
│       │       ├── controller/
│       │       ├── model/
│       │       ├── repository/
│       │       ├── service/
│       │       ├── config/
│       │       └── TravelLensApplication.java
│       │
│       └── resources/
│           ├── static/
│           │   ├── css/
│           │   └── images/
│           │
│           ├── templates/
│           │   ├── fragments/
│           │   ├── posts/
│           │   ├── auth/
│           │   └── index.html
│           │
│           └── application.properties
│
├── Dockerfile
├── render.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```
