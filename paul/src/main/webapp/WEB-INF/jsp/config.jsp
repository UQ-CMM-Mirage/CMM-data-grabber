<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Configuration</title>
    </head>
    <body>
        <h1>Paul Configuration</h1>
        <ul>
          <li>Proxy host - ${configuration.proxyHost} : ${configuration.proxyPort}</li>
          <li>Server host - ${configuration.serverHost} : ${configuration.serverPort}</li>
          <li>Base file URL - ${configuration.baseFileUrl}</li>
          <li>Capture directory - ${configuration.captureDirectory}</li>
          <li>Clients use 'project' - ${configuration.useProject}</li>
          <li>Feed parameters
            <ul>
              <li>Feed URL - ${configuration.feedUrl}</li>
              <li>Feed Id - ${configuration.feedId}</li>
              <li>Feed Title - ${configuration.feedTitle}</li>
              <li>Feed Author - ${configuration.feedAuthor}</li>
              <li>Feed Author email - ${configuration.feedAuthorEmail}</li>
            </ul>
          </li>
          <li>Facilities:
            <ul>
              <c:forEach items="${configuration.facilities}" var="facility">
                <li>${facility.facilityId} - ${facility.facilityName}
                  <ul>
                    <li>DNS Name / IP address - ${facility.address}</li>
                    <li>Drive name - ${facility.driveName}</li>
                    <li>Folder name - ${facility.folderName}</li>
                    <li>Access name - ${facility.accessName}</li>
                    <li>Access password - ${facility.accessPassword}</li>
                    <li>File settling time - ${facility.fileSettlingTime} ms</li>
                    <li>Uses file locking - ${facility.useFileLocks}</li>
                    <li>Client uses timer - ${facility.useTimer}</li>
                    <li>Client uses full screen - ${facility.useFullScreen}</li>
                    <li>Dummy facility - ${facility.dummy}</li>
                  </ul>
                </li>
              </c:forEach>
            </ul>
          </li>
        </ul>
    </body>
</html>