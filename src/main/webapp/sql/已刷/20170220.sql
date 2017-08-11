ALTER TABLE `tab_crew_contact`
DROP COLUMN `hotel`,
DROP COLUMN `roomNumber`,
DROP COLUMN `extension`,
DROP COLUMN `checkInDate`,
DROP COLUMN `checkoutDate`;

ALTER TABLE tab_inhotel_info DROP COLUMN mealType;