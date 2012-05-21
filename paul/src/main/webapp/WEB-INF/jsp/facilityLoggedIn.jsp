<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
        <title>Data Grabber - Instrument Session started</title>
    </head>
    <body>
<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
        <h1>Session started</h1>
        
        <div class="alert alert-success">
            You have successfully logged into the "${facilityName}" instrument.
        </div>
        <button class="btn btn-small" type="button" onclick="window.location = '${returnTo}'">OK</button>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
    </body>
</html>