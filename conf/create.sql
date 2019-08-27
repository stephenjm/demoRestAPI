CREATE DATABASE `demoschema` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `demo_table` (
  `policyID` int(11) NOT NULL,
  `statecode` varchar(2) NOT NULL,
  `county` varchar(20) NOT NULL,
  `construction` varchar(15) NOT NULL DEFAULT 'UNKNOWN',
  `notes` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`policyID`),
  UNIQUE KEY `policyID_UNIQUE` (`policyID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Demo db table with user data';

CREATE TABLE `demo_table_geo_data` (
  `policyID` int(11) NOT NULL,
  `point_latitude` float NOT NULL,
  `point_longitude` float NOT NULL,
  PRIMARY KEY (`policyID`),
  UNIQUE KEY `policyID_UNIQUE` (`policyID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Geo location data for each';

