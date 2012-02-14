<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Ingestion Queue Expiry</title>
    </head>
    <body>
        <h1>Paul Ingestion Queue Expiry</h1>
        
        ${errorMessage}
        
        <form name="form" method="POST" action="queue">
          <c:if test="${empty computedDate}">
        	Older than :
            <input name="olderThan" type="text" value="${param.olderThan}">
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
              <input id="discard" name="mode" type="radio" value="discard"
            	     onclick="document.form.archive.checked = false; document.form.discard.checked = true;">
              <br>
            
              <button type="submit" name="expire">Submit</button>
        	  <button type="button" onclick="window.location = 'queue'">Cancel</button>
            </c:if>
        	<c:if test="${!empty computedDate}">
        	  Confirmation - expire all files captured before ${computedDate}.
        	  <br>
              <button type="submit" name="expire">
        		  Yes - do it now</button>
        	  <button type="button" onclick="window.location = 'queue'">
        	  	  No - get me out of here</button>
        	  <input type="hidden" name="confirmed">
        	  <input type="hidden" name="olderThan" value="${param.olderThan}">
        	  <input type="hidden" name="period" value="${param.period}">
        	  <input type="hidden" name="unit" value="${param.unit}">
        	  <input type="hidden" name="mode" value="${param.mode}">
        	</c:if>
        </form>
    </body>
</html>