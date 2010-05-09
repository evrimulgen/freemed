# $Id$
#
# Authors:
#      Jeff Buchbinder <jeff@freemedsoftware.org>
#
# FreeMED Electronic Medical Record and Practice Management System
# Copyright (C) 1999-2010 FreeMED Software Foundation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# File: Daily Cash Receipt Journal

DROP PROCEDURE IF EXISTS report_DailyCashReceiptJournal_en_US;
DELIMITER //

# Function: report_DailyCashReceiptJournal_en_US
#
#	Daily Cash Receipt Journal
#

CREATE PROCEDURE report_DailyCashReceiptJournal_en_US (IN cDate DATE)
BEGIN
	SET @sql = CONCAT(
		"( SELECT ",
			"CONCAT(pt.ptlname,', ',pt.ptfname,' ',pt.ptmname) AS patientname, ",
			"0 AS 'Trans Num' ,",
			"pr.payrecdt as 'Trans Date' ,",
			"0 AS 'Void Amount',",
			"pr.payrecamt AS 'Amount Received', ",
			"pr.payrecdescrip AS Reason, ",
			"CONCAT(prec.procdt,' - ',IFNULL(prec.procdtend,'')) AS 'Service Dates', ",
			"IFNULL(i.insconame,'Not Assigned To Third Party') AS 'Source', ",
			"CASE ",
				"WHEN (pr.payreccat = 0 AND pr.payreclink!=0) THEN 'Third Party Pmt' ",
				"WHEN (pr.payreccat = 0 AND pr.payreclink=0) THEN 'CASH' ",
				"WHEN pr.payreccat = 11 THEN 'Copay' END AS 'Type' ",
		"FROM payrec pr ",
			"LEFT OUTER JOIN coverage c ON c.id = pr.payreclink  ",
			"LEFT OUTER JOIN insco i ON i.id = c.covinsco ",
			"LEFT OUTER JOIN patient pt ON pt.id = pr.payrecpatient ",
			"LEFT OUTER JOIN procrec prec ON prec.id=pr.payrecproc ",
		"WHERE "
			"pr.payreccat IN ( 0, 11 ) AND ",
			"pr.payrecdt='",cDate,"') "
	);
	PREPARE s FROM @sql ;
	EXECUTE s ;
	DEALLOCATE PREPARE s ;

END
//
DELIMITER ;

#	Add indices

DELETE FROM `reporting` WHERE report_sp = 'report_DailyCashReceiptJournal_en_US';
INSERT INTO `reporting` (
		report_name,
		report_uuid,
		report_locale,
		report_desc,
		report_type,
		report_category,
		report_sp,
		report_param_count,
		report_param_names,
		report_param_types,
		report_param_optional,
		report_formatting
	) VALUES (
		'Daily Cash Receipt Journal',
		'9e2b8711-8ac6-416d-bfb5-e12d075ee4a0',
		'en_US',
		'Daily Cash Receipt Journal',
		'jasper',
		'sub_report',
		'report_DailyCashReceiptJournal_en_US',
		1,
		'Date',
		'Date',
		'0',
		'report_DailyCashReceiptJournal_en_US'
	);

