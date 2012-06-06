<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Queue Expiry</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Queue Expiry</h1>

		${message}

		<form name="form" method="POST" action="manageDatasets">
			<c:if test="${empty computedDate}">
        	Older than :
            <input name="olderThan" type="text"
					value="${param.olderThan}">
				<br>
            Age :
            <input name="period" type="text" value="${param.period}">
            Unit :
            <input name="unit" type="text" value="${param.unit}">
				<br>
              Archive expired files
              <input id="archive" name="mode" type="radio"
					value="archive" checked="checked"
					onclick="document.form.archive.checked = true; document.form.discard.checked = false;">
				<br>
              Permanently delete expired files 
              <input id="discard" name="mode" type="radio"
					value="discard"
					onclick="document.form.archive.checked = false; document.form.discard.checked = true;">
				<br>

				<button type="submit" name="action" value="expire">Submit</button>
				<button type="button" onclick="window.location = '${returnTo}'">Cancel</button>
			</c:if>
			<c:if test="${!empty computedDate}">
        	  Confirmation - expire all files captured before ${computedDate}.
        	  <br>
				<button type="submit" name="action" value="expire">Yes - do it now</button>
				<button type="button" onclick="window.location = '${returnTo}'">
					No - get me out of here</button>
				<input type="hidden" name="confirmed">
				<input type="hidden" name="olderThan" value="${param.olderThan}">
				<input type="hidden" name="period" value="${param.period}">
				<input type="hidden" name="unit" value="${param.unit}">
				<input type="hidden" name="mode" value="${param.mode}">
			</c:if>
		    <input type="hidden" name="slice" value="${slice}">
		    <input type="hidden" name="facilityName" value="${facilityName}">
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>