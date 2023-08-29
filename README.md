# 📚 Library Management System (LMS) By Axel Diaz

## 🎯 Objective and Description
The Library Management System (LMS) aims to streamline the operations of a library by automating the management of its book collection. Designed as a console-based application, the LMS empowers librarians to efficiently add or remove books and allows readers to browse the collection. The system adheres to specific constraints, such as utilizing a text-file format for book data, to ensure ease of use and scalability.
---
## 🌟 Key Features

- 📖 **Add New Books**: Import from text file with unique ID, title, and author.
- 🗑️ **Remove Books**: By unique ID.
- 📑 **List Books**: View entire collection.

---

## 👥 User Roles

- 📚 **Librarians**: 
  - Manage collection
  - Add/remove books
- 📖 **Readers**: 
  - View-only access to book list

---

## ⚙️ Constraints

- 💻 **Console-Based**: Runs in terminal.
- 📄 **Text File Format**: CSV-like format for book data.

---

# 🛠️ Implementation Plan

## 📚 Book Class

- 🏷️ **Attributes**: ID, title, author.
- 🛠️ **Methods**: Getters, setters, and basic algorithms.

## 📚 Library Class

- 🛠️ **Core Methods**: 
  - Add/remove books
  - List books
  - Handle text files

---

## 🖥️ Main Program

- 🎨 **UI**: Console-based menu.
- 🛠️ **Functionalities**: 
  - Initialize Library
  - Handle user inputs

---

# 🧪 Testing Plan

## 📝 Unit Testing

- 📚 **Book Class**: 
  - Initialization
  - String representation
  - Getters/setters
- 📚 **Library Class**: 
  - Add/remove books
  - List books

---

## 🔄 Integration Testing

- 🛠️ **Workflow**: Test complete workflow of adding, removing, and listing books.

