<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Queue Deletion</title>
    </head>
    <body>
        <h1>Paul Queue Deletion</h1>
        <form name="form" method="POST" action="${returnTo}">
            Archive files <input id="archive" name="mode" type="radio" value="archive" checked="checked"
            	onclick="document.form.archive.checked = true; document.form.discard.checked = false;">
            <br>
            Permanently delete files <input id="discard" name="mode" type="radio" value="discard"
            	onclick="document.form.archive.checked = false; document.form.discard.checked = true;">
            <br>
        	<button type="submit" name="deleteAll">
        		Yes - do it now</button>
        	<button type="button" onclick="window.location = '${returnTo}'">
        		No - get me out of here</button>
        	<input type="hidden" name="confirmed">
        </form>
    </body>
</html>