<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Ingestion Queue</title>
    </head>
    <body>
        <h1>Paul Ingestion Queue</h1>
        <ul>
             <c:forEach items="${queue}" var="entry">
                <li>Queue entry ${entry.id} :
                    <ul>
                    	<li>User / account : ${entry.userName} / ${entry.accountName}</li>
                    	<li>Session : ${entry.sessionId} 
                    	    started ${entry.sessionStartTimestamp}</li>
                    	<li>Original file : ${entry.sourceFilePathname} 
                    	    written ${entry.fileWriteTimestamp}</li>
                    	<li>Queue file : ${entry.capturedFilePathname}
                    	    captured ${entry.captureTimestamp}</li>
                    </ul>
                </li>
             </c:forEach>
        </ul>
    </body>
</html>