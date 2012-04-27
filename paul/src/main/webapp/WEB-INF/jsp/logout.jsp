<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
        <title>Data Grabber Access Control</title>
    </head>
    <body>
<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
        <h1>Data Grabber Access Control</h1>
        You are no longer logged in.
		<% session.invalidate(); %>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
    </body>
</html>