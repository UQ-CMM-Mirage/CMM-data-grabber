<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Control Panel</title>
        <c:if test="${proxyStatus == 'TRANSITIONAL' || grabberStatus == 'TRANSITIONAL'}">
        	<meta http-equiv="refresh" content="2">
        </c:if>
    </head>
    <body>
        <h1>Control Panel</h1>
        <form method="post" action="control">
        	Login: status - ${proxyState}
        	<button ${proxyStatus == 'OFF' ? '' : 'disabled="disabled"'}
        		    name="proxy" value="ON">Enable</button>
        	<button ${proxyStatus == 'ON' ? '' : 'disabled="disabled"'}
        		    name="proxy" value="OFF">Disable</button>
        	<br>
        	Grabber: status - ${grabberState}
        	<button ${grabberStatus == 'OFF' ? '' : 'disabled="disabled"'}
        		    name="grabber" value="ON">Start</button>
        	<button ${grabberStatus == 'ON' ? '' : 'disabled="disabled"'}
        		    name="grabber" value="OFF">Stop</button>
        </form>
    </body>
</html>