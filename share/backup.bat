@echo -------------------start------------------- %date% %time%

mysqldump -h 39.104.58.109 -u root -pDispress_8 pair | mysql -h localhost -u root -pflyint8 pair_backup
mysqldump -h 39.104.58.109 -u root -pDispress_8 pair > C:\wangzhiting\zjyk\db_pair\pair.txt

cd C:\wangzhiting\lys.tasks
svn up

cd C:\wangzhiting\zjyk\db_pair
svn ci -m "%date% %time%"

@echo ----end---- %date% %time%
@echo=
@echo=
@echo=