<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber Control Panel</title>
        <c:if test="${proxyStatus == 'TRANSITIONAL' || watcherStatus == 'TRANSITIONAL'}">
        	<meta http-equiv="refresh" content="2">
        </c:if>
    </head>
    <body>
        <h1>Data Grabber Control Panel</h1>
        <form method="post" action="control">
        	ACLS Login Proxy: status - ${proxyState}
        	<button ${proxyStatus == 'OFF' ? '' : 'disabled="disabled"'}
        		    name="proxy" value="ON">Enable</button>
        	<button ${proxyStatus == 'ON' ? '' : 'disabled="disabled"'}
        		    name="proxy" value="OFF">Disable</button>
        	<br>
        	File Watcher: status - ${watcherState}
        	<button ${watcherStatus == 'OFF' ? '' : 'disabled="disabled"'}
        		    name="watcher" value="ON">Start</button>
        	<button ${watcherStatus == 'ON' ? '' : 'disabled="disabled"'}
        		    name="watcher" value="OFF">Stop</button>
            <br>
            <c:if test="${! empty restartRequired}">
            	A restart is required to enact configuration changes:
            	<button name="restart">Restart now</button>
            </c:if>
        </form>
    </body>
</html>