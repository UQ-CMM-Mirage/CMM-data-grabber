<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
<title>Data Grabber Queued Dataset</title>
<link href="/paul/css/ui-lightness/jquery-ui-1.8.18.custom.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript" src="/paul/js/jquery-ui-1.8.18.custom.min.js"></script>
<script>
	$(function() {
		var availableTags = [
            <c:forEach items="${userNames}" var="userName">'${userName}',</c:forEach> 
		];
		$( "#users" ).autocomplete({
			source: availableTags
		});
	});
</script>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
	<h1>Data Grabber Queued Dataset # ${entry.id}</h1>
	<div class=".ui-widget-content">
	<c:set var="entryName" value="entry"/>
	<%@ include file="/WEB-INF/jsp/datasetList.jspfrag" %>
	<button class="btn btn-small" type="button" onclick="history.back()">OK</button>
	</div>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
<script type="text/javascript" src="/paul/js/jquery-ui-1.8.18.custom.min.js"></script>
</body>
</html>