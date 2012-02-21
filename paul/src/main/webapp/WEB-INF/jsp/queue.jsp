<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Ingestion Queue Admin</title>
    </head>
    <body>
        <h1>Paul Ingestion Queue Admin</h1>
        <c:if test="${!empty queue}">
            <form method="POST" action="queue">
        	    <button type="submit" name="deleteAll">Delete/Archive all entries</button>
        	    <button type="submit" name="expire">Expire entries</button>
            </form>
            <ul>
                <c:forEach items="${queue}" var="entry">
                    <li><a href="queue/${entry.id}">Entry # ${entry.id}</a> - 
                        facility ${entry.facilityName}, 
                        user : ${entry.userName}, 
                        captured: ${entry.captureTimestamp}
                        <form method="POST" action="queue/${entry.id}">
                        	<button type="submit" name="mode" value="discard">Delete</button>
                        	<button type="submit" name="mode" value="archive">Archive</button>
                        	<input type="hidden" name="confirmed">
                        	<input type="hidden" name="delete">
                        </form>
                    </li>
                 </c:forEach>
            </ul>
        </c:if>
        <c:if test="${empty queue}">
            Queue is empty
        </c:if>
        
    </body>
</html>