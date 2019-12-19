create database contestApiLocal;
use contestApiLocal;
CREATE TABLE `contests` (
   `contestName` varchar(150),
   `contestStart` int,
   `contestEnd` int,
   `contestUrl` varchar(150) unique,
   `contestPlatform` varchar(60),
   `hasBeenNotified` tinyint
 );
 CREATE TABLE `lasthour` (
   `lastUnix` int
 );
 
# update lastHour set lastUnix = 1565515455 -100000;
INSERT INTO `lastHour` (`lastUnix`) VALUES (1566357975);

DELIMITER //

create procedure insertContest
(in _contestName varchar(150), in _contestStart int, in _contestEnd int, in _contestUrl varchar(150), in _contestPlatform varchar(60))
begin
INSERT INTO contests VALUES 
(_contestName, _contestStart, _contestEnd, _contestUrl, _contestPlatform, 0)
on duplicate key update contestName=_contestName;
end //

create procedure getTimeSinceLastUpdate()
begin
SELECT UNIX_TIMESTAMP(UTC_TIMESTAMP())-(SELECT * FROM lastHour) as secondsDiff;
end //

create procedure resetAfterUpdated()
begin
update lastHour set lastUnix =  UNIX_TIMESTAMP(UTC_TIMESTAMP());
end //

create procedure getNewContests()
begin
select count(*) as newContests, contestPlatform from contests where hasBeenNotified=0 group by contestPlatform;
update contests set hasBeenNotified = 1;
end //

delimiter ;