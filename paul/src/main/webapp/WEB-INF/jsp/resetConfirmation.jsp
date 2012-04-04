<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber Configuration Reset</title>
    </head>
    <body>
        <h1>Data Grabber Configuration Reset</h1>
        <form name="form" method="POST" action="${returnTo}">
            Resetting the configuration will permanently delete any
            manual configuration changes.
            <br>
            <input type="hidden" name="reset">
            <input type="hidden" name="confirmed">
        	<button type="submit" name="deleteAll">
        		Yes - do it now</button>
        	<button type="button" onclick="window.location = '${returnTo}'">
        		No - get me out of here</button>
        	<input type="hidden" name="confirmed">
        </form>
    </body>
</html>