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
		$( "#users" ).autocomplete({source: availableTags});
	});
</script>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container-fluid">
	<h1>Dataset Comparison # ${oldEntry.id}</h1>
		<div class="row">
			<div class="span6">
				<c:set var="entryName" value="oldEntry" />
				<%@ include file="/WEB-INF/jsp/datasetList.jspfrag"%>
			</div>
			<div class="span6">
				<c:set var="entryName" value="newEntry" />
				<%@ include file="/WEB-INF/jsp/datasetList.jspfrag"%>
			</div>
			<button class="btn btn-small" type="button" onclick="history.back()">OK</button>
		</div>
		<div class="row">
			<form action="/paul/datasets/${oldEntry.id}" method=post>
				<input type="hidden" name="hash" value="${newEntry.combinedDatafileHash}">
				<input type="hidden" name="returnTo" value="${returnTo}">
				<button type="submit" name="regrabNew" value="no">Update Existing Dataset</button>
				<button type="submit" name="regrabNew" value="yes">Create New Dataset</button>
			</form>
		</div>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
<script type="text/javascript" src="/paul/js/jquery-ui-1.8.18.custom.min.js"></script>
</body>
</html>