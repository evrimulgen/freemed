<?php
 // $Id$
 // desc: administrative import module for older databases
 // lic : GPL, v2

$page_name = "import.php";
include ("lib/freemed.php");

freemed::connect ();
$this_user = CreateObject('FreeMED.User');

if (!freemed::user_flag(USER_ADMIN)) {
	$display_buffer .= "$page_name :: You do not have access to this module";
	template_display();
//------HIPAA Logging
$user_to_log=$_SESSION['authdata']['user'];
if((LOGLEVEL<1)||LOG_HIPAA){syslog(LOG_INFO,"import.php|user $user_to_log attempt to access import failed, user does not have ADMIN privileges");}	
}

//------HIPAA Logging
$user_to_log=$_SESSION['authdata']['user'];
if((LOGLEVEL<1)||LOG_HIPAA){syslog(LOG_INFO,"import.php|user $user_to_log import access GLOBAL ACCESS");}	



switch ($action) {
 case "import":
  $page_title = __("Import Database");
  $display_buffer .= "
   <P>
   ".__("Importing Database")." \"$db\" ... 
  ";
  if (freemed_import_stock_data ($db)) { $display_buffer .= __("done");  }
   else                                { $display_buffer .= __("ERROR"); }
  $display_buffer .= "
   <P>
    <CENTER>
     <A HREF=\"$page_name\"
     >".__("Import Another Database")."</A> <B>|</B>
     <A HREF=\"admin.php\"
     >".__("Return to Administration Menu")."</A>
    </CENTER>
   <P>
  ";
  break;
 default:
  $page_title = __("Import Database");
  $display_buffer .= "
   <FORM ACTION=\"$page_name\" METHOD=POST>
    <INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"import\">
    <P>
    ".__("Select Database to Import")." : 
    <SELECT NAME=\"db\">
     <OPTION VALUE=\"authorizations\"
                                   >Authorizations (authorizations)
     <OPTION VALUE=\"room\"        >Booking Locations (room)
     <OPTION VALUE=\"roomequip\"   >Booking Locations Equipment (roomequip)
     <OPTION VALUE=\"callin\"      >Call-In Patients (callin)
     <OPTION VALUE=\"cpt\"         >CPT/Procedural Codes (cpt)
     <OPTION VALUE=\"cptmod\"      >CPT Modifiers (cptmod)
     <OPTION VALUE=\"patrecdata\"  >Custom Records (patrecdata)
     <OPTION VALUE=\"patrectemplate\"
                                   >Custom Record Templates (patrectemplate)
     <OPTION VALUE=\"degrees\"     >Degrees (degrees)
     <OPTION VALUE=\"diagfamily\"  >Diagnosis Families (diagfamily)
     <OPTION VALUE=\"eoc\"         >Episodes of Care (eoc)
     <OPTION VALUE=\"infaxlut\"    >Fax Sender Lookup Table (infaxlut)
     <OPTION VALUE=\"fixedform\"   >Fixed-Length Forms (fixedform)
     <OPTION VALUE=\"frmlry\"      >Formulary/Drugs (frmlry)
     <OPTION VALUE=\"icd9\"        >ICD/Diagnosis Codes (icd9)
     <OPTION VALUE=\"infaxes\"     >Incoming Faxes (infaxes)
     <OPTION VALUE=\"insco\"       >Insurance Companies (insco)
     <OPTION VALUE=\"inscogroyp\"  >Insurance Company Groups (inscogroup)
     <OPTION VALUE=\"insmod\"      >Insurance Company Modifiers (insmod)
     <OPTION VALUE=\"intservtype\" >Internal Service Types (intservtype)
     <OPTION VALUE=\"log\"         >Log File (log)
     <OPTION VALUE=\"oldreports\"  >Old Reports (oldreports)
     <OPTION VALUE=\"patimg\"      >Patient Images (patimg)
     <OPTION VALUE=\"patient\"     >Patient Record (patient)
     <OPTION VALUE=\"payrec\"      >Payment/Ledget Records (payrec)
     <OPTION VALUE=\"phyavailmap\" >Physician Availability Map (phyavailmap)
     <OPTION VALUE=\"physician\"   >Physicians/Providers (physician)
     <OPTION VALUE=\"phygroup\"    >Physician/Provider Group (phygroup)
     <OPTION VALUE=\"phystatus\"   >Physician/Provider Status (phystatus)
     <OPTION VALUE=\"facility\"    >Place of Service (facility) 
     <OPTION VALUE=\"rx\"          >Prescriptions (rx)
     <OPTION VALUE=\"printer\"     >Printers (printer)
     <OPTION VALUE=\"procedure\"   >Procedures (procedure)
     <OPTION VALUE=\"pnotes\"      >Progress Notes (pnotes)
     <OPTION VALUE=\"queries\"     >Query Maker (queries)
     <OPTION VALUE=\"scheduler\"   >Scheduler (scheduler)
     <OPTION VALUE=\"simplereport\">Simple Reports (simplereport)
     <OPTION VALUE=\"specialties\" >Specialties (specialties)
     <OPTION VALUE=\"tos\"         >Type of Service (tos)
     <OPTION VALUE=\"user\"        >User Database (user)
    </SELECT>
    <P>
    <CENTER>
     <input class=\"button\" TYPE=\"SUBMIT\" VALUE=\"".__("Import")."\"/>
    </CENTER>
    <P>
    <CENTER>
     <A HREF=\"admin.php\"
     >".__("Return to Administration Menu")."</A>
    </CENTER>
    <P>
   </FORM>
  ";
  break;
} // end action switch

template_display();

?>
