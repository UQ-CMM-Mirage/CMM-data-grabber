<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
        <title>Data Grabber - Facility Login</title>
    </head>
    <body>
<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
        <h1>ACLS Facility Login for "${facilityName}"</h1>
        
        <p>
        You have successfully logged into the Facility / Instrument.
        </p>
        <button type="button" onclick="window.location = '${returnTo}'">OK</button>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
    </body>
</html>