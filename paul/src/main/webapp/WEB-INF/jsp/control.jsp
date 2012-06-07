<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Control Panel</title>
<c:if
	test="${proxyStatus == 'TRANSITIONAL' || watcherStatus == 'TRANSITIONAL'}">
	<meta http-equiv="refresh" content="2">
</c:if>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<h1>Data Grabber Control Panel</h1>
		<div class="well form-inline">
			<form method="post" action="control">
				<label class="control-label">File Watching: status - ${watcherState}
			    	<c:choose>
			    		<c:when test="${watcherStatus == 'OFF'}">
							<button class="btn btn-primary" name="watcher" value="ON">Start</button>
							<button class="btn btn-primary disabled" 
									disabled="disabled" name="watcher" value="OFF">Stop</button>
						</c:when>
			  	 	 	<c:when test="${watcherStatus == 'ON'}">
							<button class="btn btn-primary disabled" 
									disabled="disabled" name="watcher" value="ON">Start</button>
							<button class="btn btn-primary" name="watcher" value="OFF">Stop</button>
						</c:when>
					</c:choose>
        		</label>
        	</form>
        	<form method="post" action="manageDatasets">
        		<input type="hidden"name="action" value="deleteAll">
            	<button class="btn" name="slice" value="held">Delete All Held Datasets</button>
            	<button class="btn" name="slice" value="ingestible">Delete Ingestible Datasets</button>
            	<button class="btn" name="slice" value="all">Delete All Datasets</button>
			</form>
        	<form method="post" action="manageDatasets">
        		<input type="hidden"name="action" value="archiveAll">
            	<button class="btn" name="slice" value="held">Archive All Held Datasets</button>
            	<button class="btn" name="slice" value="ingestible">Archive Ingestible Datasets</button>
            	<button class="btn" name="slice" value="all">Delete All Datasets</button>
			</form>
			<form method="post" action="manageDatasets">
        		<input type="hidden"name="action" value="expire">
            	<button class="btn" name="slice" value="held">Expire All Held Datasets</button>
            	<button class="btn" name="slice" value="ingestible">Expire Ingestible Datasets</button>
            	<button class="btn" name="slice" value="all">Expire All Datasets</button>
			</form>
			<c:if test="${! empty restartRequired}">
            	A restart is required to enact configuration changes:
            	<button name="restart">Restart now</button> <em>Not implemented yet</em>
			</c:if>
			<button class="btn btn-small" type="button" onclick="window.location = 'admin'">OK</button>
		</div>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>