# ECommerce Application

This is a backend API for an ECommerce application built using Spring Boot, Spring Security - featuring JWT authentication and session management, MySQL database integration for data storage, and deployment on AWS EC2. The application is fully tested via Postman and supports operations related to users, products, orders, carts, payments, and more.

## Features

- **User Management**: 
  - Users can register, log in, and manage their profiles.
  - Three user roles: **User**, **Seller**, and **Admin**.
  - Role-based authorization is enforced.

- **Product Management**: 
  - Sellers can add and manage products.
  - Products are categorized for easy browsing.

- **Cart and Orders**: 
  - Users can add products to the cart, place orders.
  - The cart functionality supports adding multiple cart items, quantities, and price calculations.
  - Once the order is successfully placed by the user, the original stock of the product is updated in the database.

- **Payments**: 
  - Simulated payment functionality for order processing.
  - Payment API can be easily integrated for implementing full fledged payment services.

- **Spring Security with JWT Authentication**: 
  - JWT-based authentication.
  - JWT sessions are managed using cookies for improved security.

## Project Structure

### Entities:
1. **User**: Represents application users.
2. **User Address**: Stores user shipping and billing addresses.
3. **User Role**: Defines user roles (`User`, `Seller`, `Admin`).
4. **Products**: Contains product details like name, price, and category.
5. **Category**: Classifies products into categories.
6. **Order**: Represents customer orders.
7. **Cart**: Stores user-selected items before purchase.
8. **Cart Item**: Represents each individual item in the cart.
9. **Payment**: Handles order payments.

### Layers:
- **Controllers**: Handle incoming HTTP requests and map them to services.
- **Services**: Contain the business logic for each entity.
- **Repositories**: Interface with the MySQL database using Spring Data JPA.

### Database:
- **MySQL** is used as the database to store all application data. Entities are mapped to corresponding tables.

## Custom Exceptions and Global Exception Handling
- **Custom Exceptions**: 
  - `APIException` for general API-related errors.
  - `ResourceNotFoundException` when requested resources are not found.
- **GlobalExceptionHandler**: Catches and handles exceptions globally for a unified error response structure.

## Testing
- The application was tested extensively using **Postman**, covering all major functionalities like user management, product handling, cart operations, and order placement.

## Deployment
- The application has been successfully deployed on **AWS EC2** and is running in a cloud environment.

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven
- MySQL
- IntelliJ Idea (or any of your fav IDEs will work fine)
- AWS EC2 (for deployment)
- Postman (for testing)

Please reachout to me if you have any suggestions or corrections, Happy Learning :)
