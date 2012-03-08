<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber Ingestion Queue Entry</title>
    </head>
    <body>
        <h1>Data Grabber Ingestion Queue Entry # ${entry.id}</h1>
        <ul>
             <li>Facility : ${entry.facilityName}</li>
             <li>User : ${entry.userName}</li>
             <li>Account : ${entry.accountName}</li>
             <li>Email : ${entry.emailAddress}</li>
             <li>Captured: ${entry.captureTimestamp}</li>
             <li>Session uuid: ${entry.sessionUuid}</li>
             <li>Session start time: ${entry.sessionStartTimestamp}</li>
             <li>Dataset uuid: ${entry.recordUuid}</li>
             <li>Dataset base filename: ${entry.sourceFilePathnameBase}</li>
             <li>Datafiles:
             	<ul>
             	    <c:forEach items="${entry.datafiles}" var="datafile">
             	    	<li>Filename: ${datafile.sourceFilePathname}
             	    		<ul>
             	    		    <li>File id: ${datafile.id}</li>
             	    			<li>Captured filename: ${datafile.capturedFilePathname}</li>
             	    			<li>File modified: ${datafile.fileWriteTimestamp}</li>
             	    			<li>File captured: ${datafile.captureTimestamp}</li>
             	    			<li>Mime type: ${datafile.mimeType}</li>
             	    		</ul>
             	    	</li>
             	    </c:forEach>
             	</ul>
             </li>
             <li>Metadata filename: ${entry.metadataFilePathname}
        </ul>
    </body>
</html>