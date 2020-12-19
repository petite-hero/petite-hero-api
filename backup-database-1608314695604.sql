-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: 35.241.105.150    Database: petite_hero
-- ------------------------------------------------------
-- Server version	8.0.18-google

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '9a3228e8-11e4-11eb-9cf2-42010aaa0038:1-1051487';

--
-- Current Database: `petite_hero`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `petite_hero` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `petite_hero`;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `username` varchar(11) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('0000000000','6133b24117fd0c1c79e3786f45134650fa00999e99ed820d59816173e5d4c4dc','Parent'),('0332987559','1ef89f7379f52f7986a8a8b941b6531ca18e2ffc81f4181311ebed1e3ccc9137','Parent'),('0847641376','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('0932165478','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('0938194701','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('0971143025','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('0987654321','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('1234567888','d9d08f5b131c75b9190c9aedb006fbc558b28a749edc3267749a585deae88767','Parent'),('1234567890','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Parent'),('Admin','5c826e3ece6df5408efe77551bfe703186a65dc43d0c4f3e12bcf4f2334295e1','Admin');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `child`
--

DROP TABLE IF EXISTS `child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `child` (
  `child_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` bigint(20) DEFAULT NULL,
  `gender` bit(1) DEFAULT NULL,
  `is_disabled` bit(1) DEFAULT NULL,
  `language` bit(1) DEFAULT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `photo` longtext,
  `push_token` longtext,
  `yob` int(11) DEFAULT NULL,
  `tracking_active` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `android_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `child`
--

LOCK TABLES `child` WRITE;
/*!40000 ALTER TABLE `child` DISABLE KEYS */;
INSERT INTO `child` VALUES (1,1603966756789,_binary '',_binary '\0',_binary '\0','Hưng Nguyễn','6-1607089465025-Avatar Updated.png','string',2015,_binary '\0','Hưng Nguyễn','string','string'),(2,1603969961280,_binary '',_binary '',_binary '\0','a','6-1607089465025-Avatar Updated.png',NULL,2014,_binary '\0','a a','unknown Android SDK built for x86','7c1bd58249fafbc1'),(3,1603969984579,_binary '',_binary '\0',_binary '\0','Kẹo','6-1607089465025-Avatar Updated.png','cHKeBxNPTIuUJgkvdlboTC:APA91bFfftEocvCakPp_tzFAhatXlnae7JnyT9_gGaIU0S3ZAnzAMOorkhdTn_GNyg-Gz0drPyPf8YyyF5s9VPrn6iRplWEBXN5Hfvp0oMeRrc4hPyn57XYVJzRlQVJ8U5zYCdD3Ythi',2016,_binary '\0','Hải Nguyễn','unknown Android SDK built for x86','a63cf38ea9381ce8'),(4,1603969984579,_binary '',_binary '\0',_binary '\0','Tứng Jr','4-1605011797211-Avatar Updated.png',NULL,2010,_binary '\0','Tứng Âu','unknown Android SDK built for x86','7c1bd58249fafbc1'),(5,1604296789683,_binary '',_binary '\0',_binary '\0','Dương Dương','6-1607089465025-Avatar Updated.png',NULL,2013,_binary '\0','Lệ Dương','unknown Android SDK built for x86','7c1bd58249fafbc1'),(6,1605182146234,_binary '',_binary '\0',_binary '','Lam Trường Jr','6-1607089465025-Avatar Updated.png',NULL,2015,_binary '\0','Lam Trường','unknown Android SDK built for x86','7c1bd58249fafbc1'),(27,1605689974559,_binary '\0',_binary '',_binary '\0','tset','6-1607089465025-Avatar Updated.png',NULL,1999,_binary '','test','unknown Android SDK built for x86','7c1bd58249fafbc1'),(28,1605788232363,_binary '\0',_binary '',_binary '\0',NULL,'5-1605011782584-Avatar Updated.png',NULL,2015,_binary '\0','srgr','unknown Android SDK built for x86','7c1bd58249fafbc1'),(29,1605882374519,_binary '\0',_binary '\0',_binary '\0','nick','29-1605882374834-Avatar Added.png',NULL,1999,_binary '\0','name','unknown Android SDK built for x86','7c1bd58249fafbc1'),(33,1606652507682,_binary '',_binary '',_binary '\0','','33-1606657007271-Avatar Updated.png',NULL,1999,_binary '\0','y','unknown Android SDK built for x86','7c1bd58249fafbc1'),(34,1606741895253,_binary '\0',_binary '\0',_binary '\0','My son','34-1607089491184-Avatar Updated.png',NULL,1999,_binary '\0','Hà Lan','unknown Android SDK built for x86','7c1bd58249fafbc1'),(35,1608185286764,_binary '\0',_binary '',_binary '\0','Bin','35-1608185287071-Avatar Added.png','eAmT_Cj6TtiPyaj5bXbFZ7:APA91bHYTpD__Ofp1UoNE5X3LroiFDee2ZrBURTL5ooGE13671k3g0JMKJBvcB1O_7UXpyzPhiSiphSTQqmPkgDhDjwAo8d5uPJmI4JbZ9mP_iFJV9Qe0U9156lWM7B_Y8vYjyplU8Fd',2014,_binary '\0','Tuấn',NULL,NULL),(36,1608186156294,_binary '\0',_binary '',_binary '\0','','36-1608186156584-Avatar Added.png','eAmT_Cj6TtiPyaj5bXbFZ7:APA91bHYTpD__Ofp1UoNE5X3LroiFDee2ZrBURTL5ooGE13671k3g0JMKJBvcB1O_7UXpyzPhiSiphSTQqmPkgDhDjwAo8d5uPJmI4JbZ9mP_iFJV9Qe0U9156lWM7B_Y8vYjyplU8Fd',2011,_binary '\0','Tuấn',NULL,NULL),(37,1608188057519,_binary '\0',_binary '',_binary '\0','Bin','37-1608188057785-Avatar Added.png','eAmT_Cj6TtiPyaj5bXbFZ7:APA91bHYTpD__Ofp1UoNE5X3LroiFDee2ZrBURTL5ooGE13671k3g0JMKJBvcB1O_7UXpyzPhiSiphSTQqmPkgDhDjwAo8d5uPJmI4JbZ9mP_iFJV9Qe0U9156lWM7B_Y8vYjyplU8Fd',2011,_binary '\0','Tuấn',NULL,NULL),(38,1608190199037,_binary '\0',_binary '',_binary '\0','Chết chắc','38-1608190199252-Avatar Added.png',NULL,2015,_binary '\0','Tuấn toi',NULL,NULL),(39,1608202652097,_binary '\0',_binary '',_binary '\0','shjsj',NULL,NULL,2015,_binary '\0','ndndj',NULL,NULL),(40,1608221421160,_binary '\0',_binary '',_binary '\0','địt',NULL,NULL,2015,_binary '\0','fftkkf',NULL,NULL),(41,1608221483542,_binary '\0',_binary '',_binary '\0','',NULL,NULL,2015,_binary '\0','test',NULL,NULL),(42,1608221538999,_binary '\0',_binary '',_binary '\0','',NULL,NULL,2015,_binary '\0','qqq',NULL,NULL),(43,1608221741924,_binary '\0',_binary '',_binary '\0','','43-1608221760297-Avatar Updated.png',NULL,2015,_binary '\0','test',NULL,NULL),(44,1608221883681,_binary '\0',_binary '',_binary '\0','',NULL,NULL,2014,_binary '\0','testtt',NULL,NULL),(45,1608222066239,_binary '\0',_binary '',_binary '\0','','45-1608222066521-Avatar Added.png',NULL,2014,_binary '\0','tessss',NULL,NULL),(46,1608261331485,_binary '\0',_binary '',_binary '\0','','46-1608261331801-Avatar Added.png',NULL,2014,_binary '\0','aaaaaa',NULL,NULL),(47,1608261815772,_binary '\0',_binary '',_binary '\0','',NULL,NULL,2015,_binary '\0','avc',NULL,NULL),(48,1608261865861,_binary '\0',_binary '',_binary '\0','','48-1608261866135-Avatar Added.png',NULL,2015,_binary '\0','aaabxb',NULL,NULL),(49,1608262428522,_binary '\0',_binary '',_binary '\0','','49-1608262428773-Avatar Added.png',NULL,2015,_binary '\0','hhgh',NULL,NULL),(50,1608262990388,_binary '\0',_binary '',_binary '\0','4444','50-1608262990723-Avatar Added.png',NULL,2015,_binary '\0','hghg',NULL,NULL),(51,1608263431205,_binary '\0',_binary '',_binary '\0','','51-1608263431477-Avatar Added.png',NULL,2015,_binary '\0','test1',NULL,NULL),(52,1608264030180,_binary '\0',_binary '',_binary '\0','','52-1608264030589-Avatar Added.png',NULL,2015,_binary '\0','hahaha',NULL,NULL),(53,1608278938114,_binary '',_binary '',_binary '\0','vjvj',NULL,NULL,2015,_binary '\0','chcu',NULL,NULL),(54,1608279469592,_binary '\0',_binary '',_binary '\0','vjvivi',NULL,NULL,2015,_binary '\0','vjvi',NULL,NULL),(55,1608279604870,_binary '\0',_binary '',_binary '\0','bdjdj',NULL,NULL,2015,_binary '\0','Hưng',NULL,NULL),(56,1608281735146,_binary '\0',_binary '',_binary '\0',' jvk',NULL,NULL,2015,_binary '\0','vjvu',NULL,NULL),(57,1608282100723,_binary '\0',_binary '',_binary '\0','nxnxn',NULL,NULL,2015,_binary '\0','jjsj',NULL,NULL),(58,1608283586380,_binary '\0',_binary '',_binary '\0','vjvu',NULL,NULL,2015,_binary '\0',' v y',NULL,NULL),(59,1608286229190,_binary '\0',_binary '',_binary '\0','nxnj',NULL,NULL,2015,_binary '\0','bdhx',NULL,NULL),(60,1608286237168,_binary '\0',_binary '',_binary '\0','nxnj',NULL,'string 2',2015,_binary '\0','bdhx','string','string'),(61,1608298227179,_binary '\0',_binary '',_binary '\0','Tuấn toi','61-1608298227422-Avatar Added.png',NULL,2015,_binary '\0','Tuấn',NULL,NULL),(62,1608298420438,_binary '\0',_binary '\0',_binary '\0','Bin','62-1608298420737-Avatar Added.png','eAmT_Cj6TtiPyaj5bXbFZ7:APA91bHYTpD__Ofp1UoNE5X3LroiFDee2ZrBURTL5ooGE13671k3g0JMKJBvcB1O_7UXpyzPhiSiphSTQqmPkgDhDjwAo8d5uPJmI4JbZ9mP_iFJV9Qe0U9156lWM7B_Y8vYjyplU8Fd',2015,_binary '\0','Tuấn An','ZC01 ZC01',NULL),(63,1608299991455,_binary '\0',_binary '\0',_binary '\0','Tuấn An','63-1608299991715-Avatar Added.png','eAmT_Cj6TtiPyaj5bXbFZ7:APA91bHYTpD__Ofp1UoNE5X3LroiFDee2ZrBURTL5ooGE13671k3g0JMKJBvcB1O_7UXpyzPhiSiphSTQqmPkgDhDjwAo8d5uPJmI4JbZ9mP_iFJV9Qe0U9156lWM7B_Y8vYjyplU8Fd',2015,_binary '\0','Tuấn','ZC01 ZC01',NULL);
/*!40000 ALTER TABLE `child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_history`
--

DROP TABLE IF EXISTS `location_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location_history` (
  `location_history_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `provider` varchar(10) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `child_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`location_history_id`),
  KEY `FKebsev0d01eenf8ylprtg4axhn` (`child_id`),
  CONSTRAINT `FKebsev0d01eenf8ylprtg4axhn` FOREIGN KEY (`child_id`) REFERENCES `child` (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_history`
--

LOCK TABLES `location_history` WRITE;
/*!40000 ALTER TABLE `location_history` DISABLE KEYS */;
INSERT INTO `location_history` VALUES (1,10.834675,106.66261166666666,'gps',_binary '\0',1608292861269,3),(2,10.842763333333334,106.656485,'gps',_binary '',1608293146211,3),(3,10.842763333333334,106.656485,'gps',_binary '',1608293151728,3),(4,10.842630000000002,106.65629666666668,'gps',_binary '\0',1608293156877,3),(5,10.842636666666666,106.65593666666668,'gps',_binary '\0',1608293164308,3),(6,10.842645000000001,106.65559833333333,'gps',_binary '\0',1608293169670,3),(7,10.842651666666665,106.655315,'gps',_binary '\0',1608293174912,3),(8,10.84266,106.65505,'gps',_binary '\0',1608293179824,3),(9,10.842651666666665,106.65476666666667,'gps',_binary '\0',1608293184902,3),(10,10.84262,106.65450000000001,'gps',_binary '\0',1608293190104,3),(11,10.842645000000001,106.65421999999998,'gps',_binary '\0',1608293195389,3),(12,10.843181666666664,106.65219166666665,'gps',_binary '\0',1608293234692,3),(13,10.843081666666668,106.65196166666668,'gps',_binary '\0',1608293240076,3),(14,10.842819999999998,106.65182000000001,'gps',_binary '\0',1608293245559,3),(15,10.842763333333334,106.656485,'gps',_binary '',1608293251071,3),(16,10.842763333333334,106.656485,'gps',_binary '',1608293256079,3),(17,10.842763333333334,106.656485,'gps',_binary '',1608293261080,3),(18,10.842763333333334,106.656485,'gps',_binary '',1608293266079,3),(19,10.842763333333334,106.656485,'gps',_binary '',1608294331631,3),(20,10.842763333333334,106.656485,'gps',_binary '',1608294337167,3),(21,10.842763333333334,106.656485,'gps',_binary '',1608294342167,3),(22,10.842763333333334,106.656485,'gps',_binary '',1608294372509,3),(23,10.842763333333334,106.656485,'gps',_binary '',1608294378170,3),(24,10.842763333333334,106.656485,'gps',_binary '',1608294383171,3),(25,10.842763333333334,106.656485,'gps',_binary '',1608294388171,3),(26,10.842763333333334,106.656485,'gps',_binary '',1608294393171,3),(27,10.842763333333334,106.656485,'gps',_binary '',1608294398171,3),(28,10.842763333333334,106.656485,'gps',_binary '',1608294403172,3),(29,10.842763333333334,106.656485,'gps',_binary '',1608294408173,3),(30,10.842763333333334,106.656485,'gps',_binary '',1608294413173,3),(31,10.842763333333334,106.656485,'gps',_binary '',1608294418173,3),(32,10.842630000000002,106.65624000000001,'gps',_binary '\0',1608294423487,3),(33,10.842636666666666,106.65593666666668,'gps',_binary '\0',1608294429011,3),(34,10.842641666666669,106.65571,'gps',_binary '\0',1608294434069,3),(35,10.842651666666665,106.65537166666667,'gps',_binary '\0',1608294439181,3),(36,10.842658333333334,106.65508833333334,'gps',_binary '\0',1608294444305,3),(37,10.842653333333333,106.65482333333333,'gps',_binary '\0',1608294450118,3),(38,10.84262,106.65450000000001,'gps',_binary '\0',1608294455307,3),(39,10.842645000000001,106.65421999999998,'gps',_binary '\0',1608294460495,3),(40,10.842671666666668,106.65393833333334,'gps',_binary '\0',1608294466137,3),(41,10.842725,106.65364833333334,'gps',_binary '\0',1608294471548,3),(42,10.842753333333333,106.65352499999999,'gps',_binary '\0',1608294473850,3),(43,10.842855000000002,106.65323333333332,'gps',_binary '\0',1608294479433,3),(44,10.842956666666664,106.65294666666665,'gps',_binary '\0',1608294485038,3),(45,10.843041666666666,106.65266833333332,'gps',_binary '\0',1608294490305,3),(46,10.843121666666665,106.65239166666667,'gps',_binary '\0',1608294495526,3),(47,10.843198333333332,106.65213666666665,'gps',_binary '\0',1608294500431,3),(48,10.84313,106.65198833333334,'gps',_binary '\0',1608294505366,3),(49,10.842763333333334,106.656485,'gps',_binary '',1608294510367,3),(50,10.842763333333334,106.656485,'gps',_binary '',1608294515367,3),(51,10.842763333333334,106.656485,'gps',_binary '',1608294520368,3),(52,10.842763333333334,106.656485,'gps',_binary '',1608294525369,3),(53,10.842763333333334,106.656485,'gps',_binary '',1608294530369,3),(54,10.842763333333334,106.656485,'gps',_binary '',1608294535371,3),(55,10.842763333333334,106.656485,'gps',_binary '',1608294540372,3),(56,10.842763333333334,106.656485,'gps',_binary '',1608294545374,3),(57,10.842763333333334,106.656485,'gps',_binary '',1608294550374,3),(58,10.842763333333334,106.656485,'gps',_binary '',1608294555374,3),(59,10.842763333333334,106.656485,'gps',_binary '',1608294560374,3),(60,10.842630000000002,106.65635333333333,'gps',_binary '\0',1608294565447,3),(61,10.842633333333334,106.65605,'gps',_binary '\0',1608294570929,3),(62,10.842639999999998,106.65576666666666,'gps',_binary '\0',1608294576006,3),(63,10.842646666666667,106.65554166666668,'gps',_binary '\0',1608294581069,3),(64,10.842654999999999,106.65520166666666,'gps',_binary '\0',1608294586161,3),(65,10.842656666666668,106.65493666666667,'gps',_binary '\0',1608294591973,3),(66,10.842639999999998,106.65460999999999,'gps',_binary '\0',1608294596901,3),(67,10.842635,106.65433333333334,'gps',_binary '\0',1608294602150,3),(68,10.842656666666668,106.65410833333333,'gps',_binary '\0',1608294607209,3),(69,10.842701666666668,106.65375833333334,'gps',_binary '\0',1608294612635,3),(70,10.842778333333335,106.65344666666667,'gps',_binary '\0',1608294618520,3),(71,10.842875,106.65318166666665,'gps',_binary '\0',1608294623601,3),(72,10.842960000000001,106.65294,'gps',_binary '\0',1608294629319,3),(73,10.842960000000001,106.65294,'gps',_binary '\0',1608294762579,3),(74,10.842960000000001,106.65294,'gps',_binary '\0',1608294768329,3),(75,10.842960000000001,106.65294,'gps',_binary '\0',1608294773329,3),(76,10.842960000000001,106.65294,'gps',_binary '\0',1608294778329,3),(77,10.842960000000001,106.65294,'gps',_binary '\0',1608298200633,3),(78,10.842763333333334,106.656485,'gps',_binary '',1608298585638,3),(79,10.842763333333334,106.656485,'gps',_binary '',1608298591370,3),(80,10.842763333333334,106.656485,'gps',_binary '',1608298596375,3),(81,10.842630000000002,106.65641,'gps',_binary '\0',1608298601322,3),(82,10.84266,106.65505,'gps',_binary '\0',1608298606325,3),(83,10.84268,106.65387000000001,'gps',_binary '\0',1608298611255,3),(84,10.843041666666666,106.65266833333332,'gps',_binary '\0',1608298616320,3),(85,10.842229999999999,106.65147999999999,'gps',_binary '\0',1608298982160,3),(86,10.842229999999999,106.65147999999999,'gps',_binary '\0',1608298987939,3),(87,10.842229999999999,106.65147999999999,'gps',_binary '\0',1608298992940,3),(88,10.842229999999999,106.65147999999999,'gps',_binary '\0',1608298997940,3),(89,10.842229999999999,106.65147999999999,'gps',_binary '\0',1608299002940,3),(90,10.8464939,106.6778601,'network',_binary '',1608301097105,63),(91,10.8464937,106.6778622,'network',_binary '',1608301117204,63),(92,10.842763333333334,106.656485,'gps',_binary '',1608301130947,3),(93,10.842763333333334,106.656485,'gps',_binary '',1608301136161,3),(94,10.8464936,106.677862,'network',_binary '',1608301137369,63),(95,10.842763333333334,106.656485,'gps',_binary '',1608301141162,3),(96,10.842763333333334,106.656485,'gps',_binary '',1608301146162,3),(97,10.842630000000002,106.65641,'gps',_binary '\0',1608301151322,3),(98,10.84266,106.65505,'gps',_binary '\0',1608301156299,3),(99,10.8464958,106.6778582,'network',_binary '',1608301157268,63),(100,10.84268,106.65387000000001,'gps',_binary '\0',1608301161221,3),(101,10.84306,106.65261000000001,'gps',_binary '\0',1608301166260,3),(102,10.84248,106.65163,'gps',_binary '\0',1608301171771,3),(103,10.8464941,106.677859,'network',_binary '',1608301177310,63),(104,10.8464933,106.677862,'network',_binary '',1608301197471,63),(105,10.8464939,106.6778588,'network',_binary '',1608301217266,63),(106,10.8464919,106.6778478,'network',_binary '',1608301236941,63),(107,10.8464927,106.6778502,'network',_binary '',1608301257537,63),(108,10.8464925,106.6778528,'network',_binary '',1608301278409,63),(109,10.8464918,106.6778406,'network',_binary '',1608301298471,63),(110,10.8464893,106.6778511,'network',_binary '',1608301318470,63),(111,10.8464934,106.6778544,'network',_binary '',1608301338571,63),(112,10.8464905,106.6778573,'network',_binary '',1608301358500,63),(113,10.8464876,106.6778542,'network',_binary '',1608301378770,63),(114,10.8464901,106.6778528,'network',_binary '',1608301400123,63),(115,10.8464899,106.6778558,'network',_binary '',1608301420208,63),(116,10.8464916,106.6778603,'network',_binary '',1608301440149,63),(117,10.8464911,106.677861,'network',_binary '',1608301461420,63),(118,10.8464884,106.6778644,'network',_binary '',1608301481289,63),(119,10.8464927,106.6778502,'network',_binary '',1608301501430,63),(120,10.8464928,106.6778549,'network',_binary '',1608301521373,63),(121,10.8464927,106.6778597,'network',_binary '',1608301541449,63),(122,10.8464903,106.6778508,'network',_binary '',1608301561457,63),(123,10.8464926,106.6778619,'network',_binary '',1608301581423,63),(124,10.8464926,106.6778627,'network',_binary '',1608301601575,63),(125,10.8464895,106.6778422,'network',_binary '',1608301621402,63),(126,10.8464924,106.677855,'network',_binary '',1608301642471,63),(127,10.846493,106.6778567,'network',_binary '',1608301662532,63),(128,10.8464922,106.6778662,'network',_binary '',1608301683177,63),(129,10.8464929,106.6778656,'network',_binary '',1608301703217,63),(130,10.8464939,106.6778571,'network',_binary '',1608301722609,63),(131,10.8464946,106.6778574,'network',_binary '',1608301742534,63),(132,10.8464938,106.6778461,'network',_binary '',1608301762604,63),(133,10.8464934,106.6778597,'network',_binary '',1608301782549,63),(134,10.8464923,106.6778574,'network',_binary '',1608301802677,63),(135,10.8464926,106.6778505,'network',_binary '',1608301823892,63),(136,10.846494,106.6778499,'network',_binary '',1608301843747,63),(137,10.8464947,106.6778338,'network',_binary '',1608301863765,63),(138,10.8464909,106.6778549,'network',_binary '',1608301883873,63),(139,10.846492,106.6778587,'network',_binary '',1608301903821,63),(140,10.8464936,106.677862,'network',_binary '',1608301923833,63),(141,10.8464934,106.6778582,'network',_binary '',1608301943889,63),(142,10.8464941,106.677859,'network',_binary '',1608301963903,63),(143,10.8464938,106.6778606,'network',_binary '',1608301983861,63),(144,10.8464939,106.6778593,'network',_binary '',1608302004936,63),(145,10.8464935,106.6778628,'network',_binary '',1608302024944,63),(146,10.8464934,106.6778609,'network',_binary '',1608302045059,63),(147,10.8464937,106.6778538,'network',_binary '',1608302064952,63),(148,10.8464927,106.6778502,'network',_binary '',1608302084931,63),(149,10.8464939,106.6778507,'network',_binary '',1608302104942,63),(150,10.8464938,106.6778615,'network',_binary '',1608302124996,63),(151,10.8464937,106.6778514,'network',_binary '',1608302145100,63),(152,10.8464937,106.6778559,'network',_binary '',1608302164939,63),(153,10.8464939,106.6778588,'network',_binary '',1608302187327,63),(154,10.8464931,106.677865,'network',_binary '',1608302207357,63),(155,10.8464931,106.677865,'network',_binary '',1608302227272,63),(156,10.8464938,106.6778593,'network',_binary '',1608302247484,63),(157,10.8464939,106.6778609,'network',_binary '',1608302267298,63),(158,10.8464928,106.6778475,'network',_binary '',1608302287360,63),(159,10.8464917,106.6778621,'network',_binary '',1608302307391,63),(160,10.8464925,106.6778653,'network',_binary '',1608302327353,63),(161,10.8464933,106.677865,'network',_binary '',1608302347303,63),(162,10.8464926,106.6778656,'network',_binary '',1608302368353,63),(163,10.8464929,106.6778493,'network',_binary '',1608302388452,63),(164,10.8464877,106.6778525,'network',_binary '',1608302407973,63),(165,10.8464961,106.6778575,'network',_binary '',1608302428426,63),(166,10.8464956,106.6778571,'network',_binary '',1608302448401,63),(167,10.8464925,106.6778653,'network',_binary '',1608302468437,63),(168,10.8464928,106.6778651,'network',_binary '',1608302488485,63),(169,10.8464955,106.6778581,'network',_binary '',1608302508484,63),(170,10.8464962,106.6778581,'network',_binary '',1608302528543,63),(171,10.8464945,106.677862,'network',_binary '',1608302549741,63),(172,10.8464947,106.6778602,'network',_binary '',1608302569814,63),(173,10.8464939,106.6778563,'network',_binary '',1608302589874,63),(174,10.8464941,106.6778625,'network',_binary '',1608302609752,63),(175,10.8464937,106.6778617,'network',_binary '',1608302629736,63),(176,10.8464939,106.6778613,'network',_binary '',1608302649770,63),(177,10.8464944,106.6778619,'network',_binary '',1608302669812,63),(178,10.8464944,106.6778619,'network',_binary '',1608302689815,63),(179,10.8464935,106.6778642,'network',_binary '',1608302709926,63),(180,10.8464932,106.6778643,'network',_binary '',1608302730854,63),(181,10.8464938,106.6778631,'network',_binary '',1608302750873,63),(182,10.8464937,106.6778617,'network',_binary '',1608302770857,63),(183,10.8464935,106.67786,'network',_binary '',1608302790937,63),(184,10.8464933,106.6778485,'network',_binary '',1608302810949,63),(185,10.8464938,106.6778631,'network',_binary '',1608302831012,63),(186,10.846495,106.6778625,'network',_binary '',1608302850943,63),(187,10.8464944,106.6778576,'network',_binary '',1608302870923,63),(188,10.8464936,106.6778555,'network',_binary '',1608302890998,63),(189,10.846511,106.6778889,'network',_binary '',1608302912037,63),(190,10.846511,106.6778889,'network',_binary '',1608302932034,63),(191,10.846517,106.6778979,'network',_binary '',1608302952078,63),(192,10.846519,106.6778902,'network',_binary '',1608302972172,63),(193,10.8465194,106.6778949,'network',_binary '',1608302992073,63),(194,10.8464839,106.6778485,'network',_binary '',1608303012056,63),(195,10.8464917,106.6778526,'network',_binary '',1608303032093,63),(196,10.8464865,106.6778384,'network',_binary '',1608303052280,63),(197,10.8464865,106.6778384,'network',_binary '',1608303072061,63),(198,10.8464917,106.6778526,'network',_binary '',1608303093240,63),(199,10.8465119,106.6778876,'network',_binary '',1608303113257,63),(200,10.8465119,106.6778876,'network',_binary '',1608303133195,63),(201,10.8465228,106.6778909,'network',_binary '',1608303153147,63),(202,10.8465228,106.6778909,'network',_binary '',1608303173250,63),(203,10.8465199,106.6778989,'network',_binary '',1608303193224,63),(204,10.8465316,106.6778996,'network',_binary '',1608303213290,63),(205,10.846529,106.6778985,'network',_binary '',1608303233319,63),(206,10.8465111,106.6778889,'network',_binary '',1608303253274,63),(207,10.8465138,106.6778839,'network',_binary '',1608303273242,63),(208,10.8465285,106.677893,'network',_binary '',1608303293578,63),(209,10.8465107,106.6778672,'network',_binary '',1608303313372,63),(210,10.846519,106.6778902,'network',_binary '',1608303333450,63),(211,10.846514,106.6778884,'network',_binary '',1608303353405,63),(212,10.846519,106.6778929,'network',_binary '',1608303374427,63),(213,10.8465143,106.6778797,'network',_binary '',1608303394480,63),(214,10.846515,106.6778898,'network',_binary '',1608303414476,63),(215,10.8465194,106.6778949,'network',_binary '',1608303434352,63),(216,10.846511,106.6778881,'network',_binary '',1608303454614,63),(217,10.8465194,106.6778949,'network',_binary '',1608303474433,63),(218,10.8465226,106.6778911,'network',_binary '',1608303494493,63),(219,10.8465138,106.6778839,'network',_binary '',1608303514530,63),(220,10.8465138,106.6778839,'network',_binary '',1608303534527,63),(221,10.8465138,106.6778839,'network',_binary '',1608303555490,63),(222,10.8465061,106.6778823,'network',_binary '',1608303575631,63),(223,10.8465058,106.6778817,'network',_binary '',1608303595554,63),(224,10.8465111,106.6778889,'network',_binary '',1608303615543,63),(225,10.8465061,106.6778823,'network',_binary '',1608303635577,63),(226,10.846511,106.6778881,'network',_binary '',1608303655773,63),(227,10.8465036,106.677865,'network',_binary '',1608303675745,63),(228,10.8465098,106.6778852,'network',_binary '',1608303695861,63),(229,10.8465101,106.6778851,'network',_binary '',1608303715730,63),(230,10.8465122,106.6778955,'network',_binary '',1608303736764,63),(231,10.8465147,106.6778849,'network',_binary '',1608303756826,63),(232,10.846523,106.6778961,'network',_binary '',1608303776760,63),(233,10.8465191,106.6778851,'network',_binary '',1608303796871,63),(234,10.8465335,106.6779163,'network',_binary '',1608303816889,63),(235,10.8465263,106.6778839,'network',_binary '',1608303836828,63),(236,10.8465526,106.6778648,'network',_binary '',1608303856847,63),(237,10.8465505,106.6778543,'network',_binary '',1608303876756,63),(238,10.8465512,106.6779195,'network',_binary '',1608303896776,63),(239,10.8465101,106.6777435,'network',_binary '',1608303917831,63),(240,10.8465225,106.6778476,'network',_binary '',1608303937987,63),(241,10.8465222,106.6778589,'network',_binary '',1608303957934,63),(242,10.8465205,106.6778503,'network',_binary '',1608303978012,63),(243,10.8465042,106.6777879,'network',_binary '',1608303997952,63),(244,10.8465309,106.6778863,'network',_binary '',1608304017995,63),(245,10.84653,106.6778752,'network',_binary '',1608304037957,63),(246,10.8465375,106.6778906,'network',_binary '',1608304058027,63),(247,10.8465344,106.6778834,'network',_binary '',1608304078077,63),(248,10.8465256,106.6779033,'network',_binary '',1608304099093,63),(249,10.8465437,106.6779258,'network',_binary '',1608304119012,63),(250,10.8465374,106.6779199,'network',_binary '',1608304138996,63);
/*!40000 ALTER TABLE `location_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent`
--

DROP TABLE IF EXISTS `parent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parent` (
  `parent_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `gender` bit(1) DEFAULT NULL,
  `is_disabled` bit(1) DEFAULT NULL,
  `language` bit(1) DEFAULT NULL,
  `photo` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `push_token` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `authy_id` int(11) DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_verify` bit(1) DEFAULT NULL,
  `device_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`parent_id`),
  KEY `FKp2s98y335l8jgwxyphavq8vrf` (`phone_number`),
  CONSTRAINT `FKp2s98y335l8jgwxyphavq8vrf` FOREIGN KEY (`phone_number`) REFERENCES `account` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent`
--

LOCK TABLES `parent` WRITE;
/*!40000 ALTER TABLE `parent` DISABLE KEYS */;
INSERT INTO `parent` VALUES (1,'duongllse130118@fpt.edu.vn',_binary '',_binary '\0',_binary '\0','0938194701-1605891608346-Avatar Updated.png','ExponentPushToken[xMYCjyHC9CtcELd06X31Ez]','0938194701',NULL,'Lâm Lệ Dương',_binary '','string'),(2,'hungnguyen@gmail.com',_binary '',_binary '\0',_binary '\0','3-1605011754612-Avatar Updated.png','ExponentPushToken[zitB8bBMSW0197wJQLaQdN]','0987654321',NULL,'Hưng Nguyễn',_binary '',NULL),(3,'tuanad@mail.com',_binary '',_binary '\0',_binary '\0','0971143025-1605891636253-Avatar Updated.png','ExponentPushToken[fb2huZB_0ILDYU00-7Fv97]','0971143025',NULL,'Tứng Âu',_binary '',NULL),(4,'duongllse130118@fpt.edu.vn',_binary '',_binary '\0',_binary '',NULL,NULL,'0932165478',NULL,'Dương Dương',_binary '',NULL),(5,'truongvl@gmail.com',_binary '',_binary '\0',_binary '\0','1234567890-1605891630631-Avatar Updated.png','ExponentPushToken[xMYCjyHC9CtcELd06X31Ez]','1234567890',NULL,'Trường Võ',_binary '',NULL),(7,'vodien@gmail.com',_binary '\0',_binary '',_binary '\0',NULL,NULL,'0000000000',NULL,'Vô Diện',_binary '',NULL),(8,'noemail@gmail.com',_binary '',_binary '',_binary '\0',NULL,NULL,'1234567888',NULL,'No Face',_binary '',NULL),(20,'truongvlse130651@fpt.edu.vn',_binary '',_binary '\0',_binary '\0','0332987559-1607249107052-Avatar Updated.png','ExponentPushToken[xMYCjyHC9CtcELd06X31Ez]','0332987559',311309484,'Truong Vo',_binary '',NULL),(21,'hungphu123@gmail.com',NULL,_binary '\0',_binary '',NULL,'ExponentPushToken[tNKAXlKMwm_ESvmv1M5bbC]','0847641376',311605620,'Hưng Phú',_binary '',NULL);
/*!40000 ALTER TABLE `parent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent_child`
--

DROP TABLE IF EXISTS `parent_child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parent_child` (
  `parent_child_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `child_id` bigint(20) DEFAULT NULL,
  `collaborator_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `is_collaborator_confirm` bit(1) DEFAULT NULL,
  PRIMARY KEY (`parent_child_id`),
  KEY `FKd7a7imthtnklb80f4twdqh8t7` (`child_id`),
  KEY `FKkbqrk2dyqxsn696ornxousd4r` (`collaborator_id`),
  KEY `FKk838kxqf1i44ohdp3w384gus5` (`parent_id`),
  CONSTRAINT `FKd7a7imthtnklb80f4twdqh8t7` FOREIGN KEY (`child_id`) REFERENCES `child` (`child_id`),
  CONSTRAINT `FKk838kxqf1i44ohdp3w384gus5` FOREIGN KEY (`parent_id`) REFERENCES `parent` (`parent_id`),
  CONSTRAINT `FKkbqrk2dyqxsn696ornxousd4r` FOREIGN KEY (`collaborator_id`) REFERENCES `parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent_child`
--

LOCK TABLES `parent_child` WRITE;
/*!40000 ALTER TABLE `parent_child` DISABLE KEYS */;
INSERT INTO `parent_child` VALUES (1,1,NULL,1,NULL),(2,2,NULL,2,NULL),(3,3,20,2,_binary '\0'),(4,4,1,3,_binary ''),(6,1,5,1,_binary '\0'),(7,5,NULL,1,NULL),(9,6,NULL,5,NULL),(53,5,5,1,_binary '\0'),(54,1,NULL,1,NULL),(55,5,NULL,1,NULL),(56,33,NULL,1,NULL),(57,34,NULL,5,NULL),(59,27,NULL,5,NULL),(62,35,NULL,2,NULL),(63,36,NULL,2,NULL),(64,37,NULL,2,NULL),(65,38,NULL,2,NULL),(66,39,NULL,2,NULL),(67,40,NULL,2,NULL),(68,41,NULL,2,NULL),(69,42,NULL,2,NULL),(70,43,NULL,2,NULL),(71,44,NULL,2,NULL),(72,45,NULL,2,NULL),(73,46,NULL,2,NULL),(74,47,NULL,2,NULL),(75,48,NULL,2,NULL),(76,49,NULL,2,NULL),(77,50,NULL,2,NULL),(78,51,NULL,2,NULL),(79,52,NULL,2,NULL),(80,53,NULL,2,NULL),(81,54,NULL,2,NULL),(82,55,NULL,2,NULL),(83,56,NULL,2,NULL),(84,57,NULL,2,NULL),(85,58,NULL,2,NULL),(86,59,NULL,2,NULL),(87,60,NULL,2,NULL),(88,61,NULL,2,NULL),(89,62,NULL,2,NULL),(90,63,NULL,2,NULL);
/*!40000 ALTER TABLE `parent_child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent_payment`
--

DROP TABLE IF EXISTS `parent_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parent_payment` (
  `parent_payment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `link` varchar(500) DEFAULT NULL,
  `payer_id` varchar(200) DEFAULT NULL,
  `payment_id` varchar(200) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `create_date` bigint(20) DEFAULT NULL,
  `pay_date` bigint(20) DEFAULT NULL,
  `subscription_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`parent_payment_id`),
  KEY `FKmegr1603ku2msuu0a373n05eh` (`subscription_id`),
  CONSTRAINT `FKmegr1603ku2msuu0a373n05eh` FOREIGN KEY (`subscription_id`) REFERENCES `subscription` (`subscription_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent_payment`
--

LOCK TABLES `parent_payment` WRITE;
/*!40000 ALTER TABLE `parent_payment` DISABLE KEYS */;
INSERT INTO `parent_payment` VALUES (10,99000,'Testing buying Petite Hero Premium','https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-3WJ76142LJ5840727',NULL,NULL,'CANCELLED',1607595797197,NULL,1),(11,99000,'Testing buying Petite Hero Premium','https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-0AJ871201B5938051','8PNVJMM5KP2XC','PAYID-L7I7OSY3RH25700UR4841450','SUCCESS',1607595848473,1607595889444,1),(12,79000,'Bought Petite Hero Gold','https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-1AR657148H707674W','8PNVJMM5KP2XC','PAYID-L7LRKDQ53L93550PR3578209','SUCCESS',1607931146536,1607932871091,22),(13,79000,'Bought Petite Hero Gold','https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-7J6238602M968264K','8PNVJMM5KP2XC','PAYID-L7LRPVQ1Y3241821P5197736','SUCCESS',1607931860350,1607931901968,24);
/*!40000 ALTER TABLE `parent_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quest`
--

DROP TABLE IF EXISTS `quest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quest` (
  `quest_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `assignee_id` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `reward` int(11) DEFAULT NULL,
  `submit_date` bigint(20) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`quest_id`),
  KEY `FKh8l0p49lsdebmbmenp4rmw1tb` (`assignee_id`),
  KEY `FKa1h4o5wk70635evo7v11eote1` (`creator_id`),
  CONSTRAINT `FKa1h4o5wk70635evo7v11eote1` FOREIGN KEY (`creator_id`) REFERENCES `parent` (`parent_id`),
  CONSTRAINT `FKh8l0p49lsdebmbmenp4rmw1tb` FOREIGN KEY (`assignee_id`) REFERENCES `child` (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quest`
--

LOCK TABLES `quest` WRITE;
/*!40000 ALTER TABLE `quest` DISABLE KEYS */;
/*!40000 ALTER TABLE `quest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `safezone`
--

DROP TABLE IF EXISTS `safezone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `safezone` (
  `safezone_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` bigint(20) DEFAULT NULL,
  `from_time` time DEFAULT NULL,
  `is_disabled` bit(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `repeat_on` varchar(7) DEFAULT NULL,
  `to_time` time DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `child_id` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `lata` double DEFAULT NULL,
  `latb` double DEFAULT NULL,
  `latc` double DEFAULT NULL,
  `latd` double DEFAULT NULL,
  `lnga` double DEFAULT NULL,
  `lngb` double DEFAULT NULL,
  `lngc` double DEFAULT NULL,
  `lngd` double DEFAULT NULL,
  PRIMARY KEY (`safezone_id`),
  KEY `FKm8c3nhkojdobqgjcxq4elcam2` (`child_id`),
  KEY `FKsqyk6ha9bk2u7kkeudkw61h4o` (`creator_id`),
  CONSTRAINT `FKm8c3nhkojdobqgjcxq4elcam2` FOREIGN KEY (`child_id`) REFERENCES `child` (`child_id`),
  CONSTRAINT `FKsqyk6ha9bk2u7kkeudkw61h4o` FOREIGN KEY (`creator_id`) REFERENCES `parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `safezone`
--

LOCK TABLES `safezone` WRITE;
/*!40000 ALTER TABLE `safezone` DISABLE KEYS */;
INSERT INTO `safezone` VALUES (13,1604854800000,'00:49:55',_binary '\0',10.8414846,106.8100464,'Đại học FPT TP.HCM',NULL,'04:49:08','None',1,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,1604422800000,'03:50:12',_binary '\0',10.794687107536983,106.72174075618386,'Landmark 81 skyview','1100101','11:50:15','None',1,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,1604941200000,'17:54:19',_binary '\0',10.841484398394488,106.81004611775279,'Đại học FPT TP.HCM',NULL,'21:55:49','Education',1,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,1605891600000,'08:21:55',_binary '\0',10.838941266423294,106.8066561408341,'Đại học FPT TP.HCM',NULL,'21:21:59','None',1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,1605978000000,'04:39:36',_binary '\0',10.841484398394488,106.81004611775279,'Đại học FPT TP.HCM',NULL,'16:39:37','None',1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(61,1606410000000,'19:30:00',_binary '\0',10.8414846,106.8100464,'Đại học FPT TP.HCM',NULL,'21:30:00','Education',2,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(62,1606410000000,'17:00:00',_binary '',10.8414846,106.8100464,'Đại học FPT TP.HCM',NULL,'17:30:00','None',2,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(63,1606410000000,'21:40:00',_binary '\0',10.804542243179846,106.79170550778508,'Căn hộ Sky 9','0000010','21:50:00','None',2,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(64,1606410000000,'22:43:00',_binary '\0',10.804542243179846,106.79170550778508,'Căn hộ Sky 9','0000010','23:43:00','None',2,2,1,2,3,4,5,6,7,8),(76,1607619600000,'00:56:00',_binary '\0',10.8414846,106.8100464,'Đại học FPT TP.HCM','0000000','05:56:00','None',3,2,10.84380061900141,10.840850171950978,10.83917011644409,10.84246534774119,106.80940859019756,106.8131985515356,106.81154396384954,106.80810336023569),(77,1607533200000,'01:10:00',_binary '\0',10.8414846,106.8100464,'Đại học FPT TP.HCM','0000000','06:10:00','Education',3,2,10.842417600569716,10.84384671950567,10.840738541742407,10.839189544815316,106.80814694613218,106.8096399307251,106.81306209415196,106.811513453722),(78,1607706000000,'07:00:00',_binary '\0',10.36606429945024,106.35450979694724,'Thien Ho Duong Primary School','0000000','16:00:00','Education',3,2,10.367004093673877,10.367016626181295,10.365579342193094,10.365562522171636,106.35424710810186,106.354747004807,106.35489217936994,106.35429438203573),(79,1607878800000,'15:30:00',_binary '\0',10.8414846,106.8100464,'Đại học FPT TP.HCM','0000000','17:30:00','None',3,2,10.8430846,10.8430846,10.8398846,10.8398846,106.8090464,106.81104640000001,106.81104640000001,106.8090464),(80,1607878800000,'21:32:00',_binary '\0',10.81995065570594,106.7909930460155,'Võ Chí Công & Phú Hữu','0000000','23:32:00','Home',3,2,10.82155065570594,10.82155065570594,10.81835065570594,10.81835065570594,106.7899930460155,106.7919930460155,106.7919930460155,106.7899930460155),(81,1607792400000,'16:41:00',_binary '',10.846210512277402,106.67782662436366,'Gò Vấp','0000000','18:41:00','Home',3,2,10.84729007779733,10.846799442000668,10.845687111689726,10.845932759888186,106.67795084416868,106.67826734483242,106.6778951883316,106.67740099132061),(82,1607792400000,'19:46:00',_binary '',10.838034803627444,106.67119385674596,'Lotte Mart','0000000','20:46:00','None',3,2,10.839634803627444,10.839634803627444,10.836434803627444,10.836434803627444,106.67019385674595,106.67219385674596,106.67219385674596,106.67019385674595),(83,1607792400000,'19:49:00',_binary '\0',10.845913428456397,106.61296745762229,'An Sương','0000000','20:49:00','None',3,2,10.847513428456397,10.847513428456397,10.844313428456397,10.844313428456397,106.61196745762228,106.6139674576223,106.6139674576223,106.61196745762228),(84,1607792400000,'13:51:00',_binary '\0',10.841484485336025,106.81004611775279,'Đại học FPT TP.HCM','0000000','14:51:00','Education',3,2,10.84214132540725,10.842872021616877,10.84091965267923,10.839550780572443,106.80867768824099,106.81052573025227,106.8127016723156,106.8114970251918),(85,1607965200000,'14:16:00',_binary '',10.8414846,106.8100464,'Đại học FPT TP.HCM','1111111','17:25:00','Education',3,2,10.843458157890161,10.843636632864367,10.843898088630628,10.840713186202501,106.80952493101357,106.80965434759855,106.81310769170524,106.813130825758),(86,1607965200000,'05:00:00',_binary '\0',10.84215492631861,106.81020906195045,'Đại học FPT TP.HCM','1010101','22:00:00','Education',4,3,10.843257620384344,10.844420340900193,10.841280557055539,10.839814215671877,106.80736675858498,106.80891338735819,106.81251391768456,106.8109468370676),(87,1607965200000,'21:55:00',_binary '\0',10.782803267720517,106.69586515054107,'Turtle Lake','0000000','23:57:00','Home',3,2,10.782865359012433,10.782935841239151,10.782279433694121,10.78233740049936,106.69556926935911,106.69613488018513,106.6962780430913,106.69545091688633),(88,1608310800000,'17:12:00',_binary '',10.834675310184704,106.66261212900281,'Nguyen Thi Minh Khai Primary School','1111100','20:12:00','Education',3,2,10.835178053345984,10.83501307458654,10.83401035741644,10.834013979717685,106.66264448314904,106.66332609951496,106.66305184364319,106.66223477572203),(89,1608310800000,'00:00:00',_binary '',10.843457001120598,106.65580702945589,'Floral Park Village Go Vap','0000000','00:00:00','Home',3,2,10.843233252927844,10.843230618608102,10.843049179780136,10.843049179780136,106.65581960231066,106.65595807135105,106.65594566613434,106.65582865476608),(90,1608310800000,'00:00:00',_binary '',10.841484219903096,106.81004611775279,'Đại học FPT TP.HCM','0000000','00:00:00','Home',3,2,10.841884219903097,10.841884219903097,10.841084219903095,10.841084219903095,106.8097961177528,106.81029611775278,106.81029611775278,106.8097961177528),(91,1608310800000,'19:00:00',_binary '',10.842809618991918,106.65653759613633,'Floral Park Village Go Vap','0000000','23:00:00','None',3,2,10.84284534909622,10.842845019805837,10.842664239325174,10.842666873649893,106.65643885731697,106.6565575450659,106.65654547512531,106.65643081068993),(92,1608224400000,'19:00:00',_binary '',10.84276944719422,106.65651278570294,'Nhà','1111111','23:59:00','Home',3,2,10.842849629871305,10.842850947032849,10.842651396991796,10.842652055573009,106.6564254462719,106.6565441340208,106.65655117481946,106.65642276406288),(93,1608224400000,'19:00:00',_binary '',10.842757592492706,106.65648898109794,'Nhà','1111111','23:59:00','Home',3,2,10.842843044063462,10.84284403193465,10.84265073841057,10.842648762666926,106.65642980486155,106.65654279291631,106.6565488278866,106.65642477571964),(94,1608224400000,'07:00:00',_binary '',10.834675703304868,106.66261212900281,'Trường','1111100','16:00:00','Education',3,2,10.834879049858749,10.834790139072574,10.834474670218142,10.834574118682038,106.66249830275774,106.6628101095557,106.662726290524,106.6624104604125),(95,1608310800000,'07:00:00',_binary '',10.834675703304868,106.66261212900281,'Trường học','1111100','16:00:00','Education',3,2,10.834878061961264,10.834798700853128,10.834482573408541,10.834569837788573,106.66249997913839,106.66278865188359,106.66272226721047,106.66242521256208),(96,1608224400000,'19:00:00',_binary '',10.842747713598737,106.65648328140378,'Nhà','1111111','23:59:00','Home',3,2,10.842846007677014,10.842842056192275,10.842653043444818,10.842647774795088,106.65642913430929,106.65654547512531,106.65654480457306,106.65642343461514),(97,1608224400000,'07:00:00',_binary '',10.834675703304868,106.66261212900281,'Trường','1111100','16:00:00','Education',3,2,10.834873122473828,10.834803969641035,10.83448883010079,10.834563910397534,106.6625003144145,106.66280876845121,106.66273467242716,106.66241280734538),(98,1608224400000,'19:00:00',_binary '\0',10.842753970454172,106.6564859636128,'Nhà','1111111','23:00:00','Home',3,2,10.842846666257792,10.84284798341935,10.84266753223108,10.842665885778134,106.65642913430929,106.65653642266989,106.65653575211763,106.65642846375704),(99,1608224400000,'07:00:00',_binary '',10.83483014467554,106.66259301826358,'Trường','1110000','16:00:00','Education',3,2,10.834907369585098,10.834819446704998,10.83454678682274,10.83457444798153,106.66250232607125,106.66276719421147,106.6626924276352,106.66241280734538),(100,1608224400000,'07:00:00',_binary '\0',10.845173254213996,106.66910642758012,'Vo Thi Sau Primary School','1110000','16:00:00','Education',3,2,10.845870195352767,10.845448707615153,10.84495773950934,10.845335432684402,106.66934631764887,106.6696685180068,106.66917834430933,106.66878540068863);
/*!40000 ALTER TABLE `safezone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription` (
  `subscription_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expired_date` bigint(20) DEFAULT NULL,
  `subscription_type_id` bigint(20) DEFAULT NULL,
  `is_disabled` bit(1) DEFAULT NULL,
  `start_date` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`subscription_id`),
  KEY `FK8mltpw5ur3yf5nw2d7t4rnxc6` (`subscription_type_id`),
  KEY `FK2wun3cokdg9cw2e59fdrlgjmc` (`parent_id`),
  CONSTRAINT `FK2wun3cokdg9cw2e59fdrlgjmc` FOREIGN KEY (`parent_id`) REFERENCES `parent` (`parent_id`),
  CONSTRAINT `FK8mltpw5ur3yf5nw2d7t4rnxc6` FOREIGN KEY (`subscription_type_id`) REFERENCES `subscription_type` (`subscription_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription`
--

LOCK TABLES `subscription` WRITE;
/*!40000 ALTER TABLE `subscription` DISABLE KEYS */;
INSERT INTO `subscription` VALUES (1,1610187890076,4,_binary '\0',1605826800000,1),(2,1608079365000,4,_binary '',1605826800000,2),(3,1659092673132,3,_binary '\0',1605826800000,3),(4,1656916607896,4,_binary '\0',1605826800000,4),(5,1607774068141,4,_binary '\0',1605826800000,5),(7,1607941379648,3,_binary '\0',1605826800000,7),(20,1609839230589,4,_binary '\0',1605826800000,20),(22,1610524871091,3,_binary '',1607932871091,2),(23,1610523257868,1,_binary '\0',1607931257868,21),(24,1610524871598,3,_binary '\0',1607931901969,2);
/*!40000 ALTER TABLE `subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_type`
--

DROP TABLE IF EXISTS `subscription_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_type` (
  `subscription_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `max_children` int(11) DEFAULT NULL,
  `max_collaborator` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `duration_day` int(11) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `applied_date` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`subscription_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_type`
--

LOCK TABLES `subscription_type` WRITE;
/*!40000 ALTER TABLE `subscription_type` DISABLE KEYS */;
INSERT INTO `subscription_type` VALUES (1,'This is free trial subscription',2,2,'Free Trial',0,30,_binary '\0',1605002400000),(2,'This is petite hero standard subscription',2,2,'Petite Hero Standard',69000,30,_binary '\0',1605002400000),(3,'This is petite hero gold subscription',3,2,'Petite Hero Gold',79000,30,_binary '\0',1605002400000),(4,'This is petite hero premium subscription',5,4,'Petite Hero Premium',99000,30,_binary '\0',1605002400000),(65,'This is description for Petite Hero Intern pack',5,5,'Petite Hero Intern',20000,30,_binary '\0',1607097113570);
/*!40000 ALTER TABLE `subscription_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assign_date` bigint(20) DEFAULT NULL,
  `created_date` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `proof_photo` longtext,
  `status` varchar(20) DEFAULT NULL,
  `submit_date` bigint(20) DEFAULT NULL,
  `assignee_id` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `from_time` time DEFAULT NULL,
  `to_time` time DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  KEY `FKciiif65p0e64aye7fpxq9sn43` (`assignee_id`),
  KEY `FKccnpbn3l0u1o1mi7jpucruatw` (`creator_id`),
  CONSTRAINT `FKccnpbn3l0u1o1mi7jpucruatw` FOREIGN KEY (`creator_id`) REFERENCES `parent` (`parent_id`),
  CONSTRAINT `FKciiif65p0e64aye7fpxq9sn43` FOREIGN KEY (`assignee_id`) REFERENCES `child` (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=410 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (403,1608138000000,1608186391245,'Học thuộc bài Văn',_binary '\0','Học bài',NULL,'FAILED',NULL,36,2,'13:35:58','14:00:58','Education'),(404,1608138000000,1608189477260,'dọn dẹp phòng cho sạch',_binary '\0','dọn phòng',NULL,'ASSIGNED',NULL,37,2,'15:00:41','16:00:41','Housework'),(405,1608138000000,1608189534596,'dọn phòng sạch',_binary '\0','dọn phòng 2',NULL,'ASSIGNED',NULL,37,2,'14:20:31','15:00:31','Housework'),(406,1608224400000,1608263368350,'test',_binary '','test',NULL,'ASSIGNED',NULL,3,2,'10:50:10','11:00:10','Housework'),(407,1608224400000,1608298920056,'aa',_binary '\0','Làm bài tập Toán','407-1608298949760-Child_Submitted.png','HANDED',1608298949762,62,2,'20:41:42','21:11:42','Education'),(408,1608224400000,1608301544985,'dọn phòng sạch sẽ',_binary '\0','dọn phòng',NULL,'ASSIGNED',NULL,3,2,'21:25:20','21:55:20','Housework'),(409,1608224400000,1608301619111,'dọn phòng sạch sẽ',_binary '\0','dọn phòng','409-1608301642962-Child_Submitted.png','DONE',1608301642968,63,2,'21:26:42','21:56:42','Housework');
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-19  1:05:08
