DROP DATABASE IF EXISTS dotcms2;
CREATE DATABASE dotcms2 DEFAULT CHARACTER SET = utf8 DEFAULT COLLATE = utf8_general_ci;
GRANT ALL PRIVILEGES ON dotcms2.* TO "dotcms2"@"%" IDENTIFIED BY "password";
GRANT SELECT ON mysql.proc TO "dotcms2"@"%";
FLUSH PRIVILEGES;

