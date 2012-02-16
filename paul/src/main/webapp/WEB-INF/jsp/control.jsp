<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Control Panel</title>
    </head>
    <body>
        <h1>Control Panel</h1>
        <form method="post" action="control">
        	Login: status - ${proxyStatus}
        	<button ${proxyStatus == 'off' ? '' : 'disabled="disabled"'}>Enable</button>
        	<button ${proxyStatus == 'on' ? '' : 'disabled="disabled"'}>Disable</button>
        	<br>
        	Grabber: status - ${grabberStatus}
        	<button ${grabberStatus == 'off' ? '' : 'disabled="disabled"'}>Start</button>
        	<button ${grabberStatus == 'on' ? '' : 'disabled="disabled"'}>Stop</button>
        </form>
    </body>
</html>