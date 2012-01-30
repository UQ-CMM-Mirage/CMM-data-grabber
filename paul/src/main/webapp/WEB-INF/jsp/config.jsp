<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Paul Configuration</title>
    </head>
    <body>
        <h1>Paul Configuration</h1>
        <ul>
          <li>Proxy host - ${config.proxyHost} : ${config.proxyPort}</li>
          <li>Server host - ${config.serverHost} : ${config.serverPort}</li>
          <li>Base file URL - ${config.baseFileUrl}</li>
          <li>Capture directory - ${config.captureDirectory}</li>
          <li>Clients use 'project' - ${config.useProject}</li>
          <li>Facility recheck interval - ${config.facilityRecheckInterval}</li>
          <li>Feed parameters
            <ul>
              <li>Feed URL - ${config.feedUrl}</li>
              <li>Feed id - ${config.feedId}</li>
              <li>Feed title - ${config.feedTitle}</li>
              <li>Feed author - ${config.feedAuthor}</li>
              <li>Feed author email - ${config.feedAuthorEmail}</li>
            </ul>
          </li>
          <li>Facilities:
            <ul>
              <c:forEach items="${config.facilities}" var="facility">
                <li>${facility.facilityName} - ${facility.facilityDescription}
                  <ul>
                    <li>DNS name / IP address - ${facility.address}</li>
                    <li>Drive name - ${facility.driveName}</li>
                    <li>Folder name - ${facility.folderName}</li>
                    <li>Access name - ${facility.accessName}</li>
                    <li>Access password - ${facility.accessPassword}</li>
                    <li>Datafile templates: ${facility.datafileTemplates.isEmpty() ? 'none' : ''}
                      <ul>
                        <c:forEach items="${facility.datafileTemplates}" var="template">
                      	  <li>Pattern - '${template.filePattern}', 
                      	      mimeType - '${template.filePattern}',
                      	      optional - ${template.optional}</li>
                      	</c:forEach>
                      </ul>
                    </li>
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