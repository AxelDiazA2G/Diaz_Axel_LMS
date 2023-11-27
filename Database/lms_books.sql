-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: lms
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `barcode` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `dueDate` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'To Kill a Mockingbird','Harper Lee',0,NULL),(2,'1984','George Orwell',0,NULL),(3,'The Great Gatsby','F. Scott Fitzgerald',0,NULL),(4,'Pride and Prejudice','Jane Austen',0,NULL),(5,'Brave New World','Aldous Huxley',0,NULL),(6,'The Catcher in the Rye','J.D. Salinger',0,NULL),(7,'Animal Farm','George Orwell',0,NULL),(8,'Fahrenheit 451','Ray Bradbury',0,NULL),(9,'The Lord of the Rings','J.R.R. Tolkien',0,NULL),(10,'The Hobbit','J.R.R. Tolkien',0,NULL),(11,'The Harry Potter Series','J.K. Rowling',0,NULL),(12,'The Chronicles of Narnia','C.S. Lewis',0,NULL),(13,'One Hundred Years of Solitude','Gabriel García Márquez',0,NULL),(14,'To Kill a Mockingbird','Harper Lee',0,NULL),(15,'1984','George Orwell',0,NULL),(16,'The Great Gatsby','F. Scott Fitzgerald',0,NULL),(17,'Pride and Prejudice','Jane Austen',0,NULL),(18,'Brave New World','Aldous Huxley',0,NULL),(19,'The Catcher in the Rye','J.D. Salinger',0,NULL),(20,'Animal Farm','George Orwell',0,NULL),(21,'Fahrenheit 451','Ray Bradbury',0,NULL),(22,'The Lord of the Rings','J.R.R. Tolkien',0,NULL);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-22 23:52:03
