/*
 * Auto generated by Youtis.
 * Youtis is a tool for generating DDL automatically.
 * You can get more information from https://github.com/Valinaa/youtis-spring-boot.
 *
 * Server Type: MySQL
 *
 */

CREATE TABLE IF NOT EXISTS `model_first`(
	`VIP` TINYTEXT NOT NULL DEFAULT 'Valinaa' COMMENT '姓名',
	`id` INT NULL DEFAULT NULL,
	`time` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY(`VIP`, `id`)
) COMMENT '测试表';