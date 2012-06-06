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

		<c:if test="${! empty message}">
			<div class="alert">${message}</div>
		</c:if>

		<c:set var="qual"
			value="${slice == 'ALL' ? 'All' : slice == 'HELD' ? 'All Held' : 'All Ingestible' }" />
		<c:set var="qualf"
			value="${empty facilityName ? '' : 'for facility'}
	    	          ${empty facilityName ? '' : facilityName}" />
	    <c:set var="expire"
	    	value="${mode == 'archive' ? 'Archive' : 'Permanently Delete' }" />
		<form name="form" method="POST" action="manageDatasets">
			<fieldset>
				<c:choose>
					<c:when test="${empty computedDate}">
						<div class="control-group">
              				<label class="control-label"> Datasets before expiry date</label>
            				<div class="controls">
            				    <input class="input" name="olderThan" type="text"
										value="${param.olderThan}"> Use ISO date/time syntax
							</div>
						</div>
						<div class="control-group">
              				<label class="control-label"> Datasets older than age</label>
            				<div class="controls">
            					<input class="input" name="age" type="text" value="${param.age}">
            					e.g. "1 day" or "3 weeks" or "99 years"
            				</div>
            			</div>
						<div class="control-group">
              				<label class="control-label">Archive by</label>
              				<div class="controls">
              					<label class="radio">
              						<input id="discard" name="mode" type="radio"
											value="discard"
											onclick="document.form.archive.checked = false; document.form.discard.checked = true;">
              						Permanently deleting expired files 
              					</label>
              					<label class="radio">
              						<input id="archive" name="mode" type="radio"
											value="archive" checked="checked"
											onclick="document.form.archive.checked = true; document.form.discard.checked = false;">
              						Archiving expired files
              					</label>
              				</div>
              			</div>
						<div class="form-actions">
							<button class="btn btn-primary" type="submit" 
									name="action" value="expire">Submit</button>
							<button class="btn" type="button" 
									onclick="window.location = '${returnTo}'">Cancel</button>
						</div>
					</c:when>
					<c:otherwise>
        	  			<div class="alert alert-info">
        	  				Confirmation is required - Do you <em>really</em> want 
        	  				to ${expire} ${qual} Datasets ${qualf} 
        	  				captured/updated before ${computedDate}?
        	  			</div>
        	  			<div class="form-actions">
							<button class="btn btn-danger" type="submit" name="action" value="expire">Yes -
								do it now</button>
							<button class="btn" type="button" onclick="window.location = '${returnTo}'">
								No - get me out of here</button>
							<input type="hidden" name="confirmed">
							<input type="hidden" name="olderThan" value="${param.olderThan}">
							<input type="hidden" name="age" value="${param.age}">
							<input type="hidden" name="mode" value="${param.mode}">
						</div>
					</c:otherwise>
				</c:choose>
				<input type="hidden" name="slice" value="${slice}">
				<input type="hidden" name="facilityName" value="${facilityName}">
			</fieldset>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>