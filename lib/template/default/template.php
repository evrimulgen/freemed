<?php
 // $Id$
 // $Author$

// Check for refresh location
/*
if (isset($refresh)) {
	Header("Location: ".$refresh_location);
}
*/

// Check for avoiding template
if (!$GLOBALS['__freemed']['no_template_display']) {
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
<head>
	<title><?php print prepare(PACKAGENAME) . " v". VERSION . " - " .
		( !empty($page_title) ? $page_title." - " : "" ) .
		prepare(INSTALLATION); ?></title>
	<meta HTTP-EQUIV="Content-Type" 
		CONTENT="text/html; CHARSET=<?php print $__ISO_SET__; ?>">
<?php
//----- Handle refresh
if (isset($refresh)) {
?>
	<meta HTTP-EQUIV="REFRESH" CONTENT="0;URL=<?php print $refresh; ?>">
<?php
} else if (isset($GLOBALS['__freemed']['automatic_refresh'])) { // automatic refreshes
?>
	<meta HTTP-EQUIV="REFRESH" CONTENT="<?php
	print $GLOBALS['__freemed']['automatic_refresh'];
	?>;URL=<?php print basename($REQUEST_URI); ?>">
<?php
} // end handle refresh
?>
	<link REL="StyleSheet" TYPE="text/css"
		HREF="lib/template/default/stylesheet.css" />
</head>

<body BGCOLOR="#ffffff" TEXT="#555555"
 ALINK="#000000" VLINK="#000000" LINK="#000000"
 MARGINWIDTH="0" MARGINHEIGHT="0" LEFTMARGIN="0" RIGHTMARGIN="0"
 <?php
	// Check for close_on_load
	if ($GLOBALS['__freemed']['close_on_load']) {
		print "onLoad=\"window.close(); return true;\"";
	} elseif (!empty($GLOBALS['__freemed']['on_load'])) {
		print " onLoad=\"".$GLOBALS['__freemed']['on_load']."(); return true;\"";
	}
 ?>>

<!-- define main table -->

<table WIDTH="100%" CELLSPACING="0" CELLPADDING="2"
 VALIGN="MIDDLE" ALIGN="CENTER">

<?php
// To conserve space, turn off the header bar if menu bar
if ($GLOBALS['__freemed']['no_menu_bar']) {
?>

<!-- top/header bar -->

<tr>
	<td COLSPAN="2" ALIGN="LEFT" VALIGN="TOP">
		<!-- <I>Banner goes here.</I> -->
		<img SRC="lib/template/default/banner.<?php
		print IMAGE_TYPE; ?>"
		 WIDTH="300" HEIGHT="40" ALT="freemed" />
	</td>
</tr>

<?php } ?>

<tr>
	<td COLSPAN="1" VALIGN="TOP" ALIGN="RIGHT" WIDTH="250">

	<!-- menu bar -->
<?php
//----- Check to see if we skip displaying this
if (!$GLOBALS['__freemed']['no_menu_bar']) {
?>
	<table WIDTH="100%" CELLSPACING="0" CELLPADDING="0"
	 CLASS="menubar" VALIGN="TOP" ALIGN="CENTER">
	<tr><td VALIGN="TOP" ALIGN="CENTER" CLASS="menubar_title">
		<b><?php print INSTALLATION; ?></b>
		<br>
		<small><?php print PACKAGENAME." v".VERSION; ?></small>
<?php
//----- Add page title text if it exists
if (isset($page_title)) {
	print "
		<br>
		".prepare($page_title)."
	";
} // end isset page_title
?>
	</td></tr>
<?php
//----- Create user object if it doesn't exist and we're logged in
if (freemed_verify_auth() and !is_object($this_user)) {
	$this_user = CreateObject('FreeMED.User');
} // end check to see if we're logged in

//----- Generate session information portion of the bar
if (is_object($this_user)) {
	print "<tr><td VALIGN=\"TOP\" ALIGN=\"LEFT\" CLASS=\"menubar_info\">\n";
	print "<center>\n";
	print _("User")." : ".$this_user->getDescription()."\n";
	print "</center>\n";
	print "</td></tr>\n";
} // end checking if this_user exists
?>
	<tr><td VALIGN="TOP" ALIGN="LEFT" CLASS="menubar_items">

<?php
	// Include menubar items
	include_once("lib/template/default/menubar.php");

?>
	</td></tr>
	<tr><td VALIGN="BOTTOM" ALIGN="RIGHT" CLASS="menubar_items">
	<img src="lib/template/default/img/menubar_lower_right.gif" border="0"
		alt=""/></td></tr></table>

<?php } else { /* if there is *no* menu bar */ ?>

&nbsp;

<?php } /* end of checking for no menu bar */ ?>
	</td> <td COLSPAN="1" VALIGN="TOP" ALIGN="CENTER">

	<!-- body -->

		<table WIDTH="100%" CELLSPACING="0" CELLPADDING="3"
		 CLASS="mainbox">
		<tr><td VALIGN="MIDDLE" ALIGN="CENTER">
<?php
	// Actual content display
	print $display_buffer;
?>
		&nbsp;
		</td></tr></table>
	</td>
</tr>

<!-- master table end -->
</table>

<!-- copyright notice -->
<p/>
<div NAME="copyright" ALIGN="CENTER">
	&copy; 1999-<?php print date("Y"); ?> by the FreeMED Software Foundation
</div>

</body>
</html>
<?php
} else {
	// Show what we have, if that's what we're doing
	print "<html>\n".
		"<head>\n".
		"<link REL=\"StyleSheet\" TYPE=\"text/css\" ".
		"HREF=\"lib/template/default/stylesheet.css\"/>\n".
		"</head>\n".
		"<body";
	// Check for close_on_load
	if ($GLOBALS['__freemed']['close_on_load']) {
		print " onLoad=\"window.close(); return true;\"";
	} elseif (!empty($GLOBALS['__freemed']['on_load'])) {
		print " onLoad=\"".$GLOBALS['__freemed']['on_load']."(); return true;\"";
	}
	print ">\n";
	print $display_buffer;
	print "</body>\n".
		"</html>\n";
} // end checking for "no_template_display"
?>
