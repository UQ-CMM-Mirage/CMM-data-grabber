<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber Queue Diagnostics</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<div class="row-fluid"><h1>Queue Diagnostics for ${facilityName}</h1></div>
		<div class="row-fluid">
			Grabber status : ${status.status} <br> 
			Grabber message : ${status.message} <br> 
			Grabber LWM timestamp : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${status.grabberLWMTimestamp}"/> <br>
			Grabber HWM timestamp : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${status.grabberHWMTimestamp}"/> <br>
			Timestamp of last queued Dataset : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${catchupTimestamp}"/> <br>
			LWM / HWM used in analysis : 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${lwmTimestamp}"/> / 
				<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${hwmTimestamp}"/>
		</div>
		<div class="row-fluid"><h2>Statistics</h2></div>
		<div class="row-fluid">
		<table class="table table-striped table-condensed">
			<thead>
				<tr><th class="span2">Timespan</th>
					<th class="span2">All Datasets in "instrument data"</th>
					<th class="span2">Unmatched Datasets on "instrument data"</th>
					<th class="span2">All Datasets in queues</th>
					<th class="span2">Dataset groups with multiples in queues</th>
					<th class="span2">Unmatched Dataset groups in queues</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>all</td>
					<td>${analysis.all.datasetsInFolder}</td>
					<td>${analysis.all.datasetsUnmatchedInFolder}</td>
					<td>${analysis.all.datasetsInDatabase}</td>
					<td>${analysis.all.groupsWithDuplicatesInDatabase}</td>
					<td>${analysis.all.groupsUnmatchedInDatabase}</td>
				</tr>
				<c:if test="${!empty analysis.beforeLWM}">
					<tr>
						<td>&#x2264; LWM</td>
						<td>${analysis.beforeLWM.datasetsInFolder}</td>
						<td>${analysis.beforeLWM.datasetsUnmatchedInFolder}</td>
						<td>${analysis.beforeLWM.datasetsInDatabase}</td>
						<td>${analysis.beforeLWM.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.beforeLWM.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterLWM}">
					<tr>
						<td>&gt; LWM</td>
						<td>${analysis.afterLWM.datasetsInFolder}</td>
						<td>${analysis.afterLWM.datasetsUnmatchedInFolder}</td>
						<td>${analysis.afterLWM.datasetsInDatabase}</td>
						<td>${analysis.afterLWM.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.afterLWM.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.beforeHWM}">
					<tr>
						<td>&#x2264; HWM</td>
						<td>${analysis.beforeHWM.datasetsInFolder}</td>
						<td>${analysis.beforeHWM.datasetsUnmatchedInFolder}</td>
						<td>${analysis.beforeHWM.datasetsInDatabase}</td>
						<td>${analysis.beforeHWM.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.beforeHWM.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterHWM}">
					<tr>
						<td>&gt; HWM</td>
						<td>${analysis.afterHWM.datasetsInFolder}</td>
						<td>${analysis.afterHWM.datasetsUnmatchedInFolder}</td>
						<td>${analysis.afterHWM.datasetsInDatabase}</td>
						<td>${analysis.afterHWM.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.afterHWM.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.beforeQEnd}">
					<tr>
						<td>&#x2264; current Queue End</td>
						<td>${analysis.beforeQEnd.datasetsInFolder}</td>
						<td>${analysis.beforeQEnd.datasetsUnmatchedInFolder}</td>
						<td>${analysis.beforeQEnd.datasetsInDatabase}</td>
						<td>${analysis.beforeQEnd.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.beforeQEnd.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
				<c:if test="${!empty analysis.afterQEnd}">
					<tr>
						<td>&gt; current Queue End</td>
						<td>${analysis.afterQEnd.datasetsInFolder}</td>
						<td>${analysis.afterQEnd.datasetsUnmatchedInFolder}</td>
						<td>${analysis.afterQEnd.datasetsInDatabase}</td>
						<td>${analysis.afterQEnd.groupsWithDuplicatesInDatabase}</td>
						<td>${analysis.afterQEnd.groupsUnmatchedInDatabase}</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		</div>
		<c:if test="${analysis.problems.nosProblems > 0}">
				<div class="row-fluid"><h2>Problems</h2></div>
				<div class="row-fluid">
					<table class="table table-striped table-condensed">
						<thead>
							<tr>
								<th class="span4">Problem type</th>
								<th class="span2">Count</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Metadata file missing</td>
								<td>${analysis.problems.metadataMissing}
							</tr>
							<tr>
								<td>Metadata file size wrong</td>
								<td>${analysis.problems.metadataSize}
							</tr>
							<tr>
								<td>Grabbed Datafile missing</td>
								<td>${analysis.problems.fileMissing}
							</tr>
							<tr>
								<td>Grabbed Datafile size doesn't match recorded size</td>
								<td>${analysis.problems.fileSize}
							</tr>
							<tr>
								<td>Grabbed Datafile size doesn't match current file size
									in S:</td>
								<td>${analysis.problems.fileSize2}
							</tr>
							<tr>
								<td>Grabbed Datafile hash doesn't match recorded hash</td>
								<td>${analysis.problems.fileHash}
							</tr>
							<tr>
								<td>Grabbed Datafile hash doesn't match current file hash
									in S:</td>
								<td>${analysis.problems.fileHash2}
							</tr>
							<tr>
								<td>Unexpected IO Errors</td>
								<td>${analysis.problems.ioError}
							</tr>
						</tbody>
					</table>
				</div>
				<div class="row-fluid">
					<table class="table table-striped table-condensed">
						<thead>
							<tr>
								<th class="span1">Dataset Id</th>
								<th class="span1">Problem Type</th>
								<th class="span5">Details</th>
								<th class="span2">&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${analysis.problems.problems}" var="problem">
								<tr >
									<c:choose>
									    <c:when test="${empty prevId || prevId != problem.dataset.id}">
									    	<td><a href="/paul/datasets/${problem.dataset.id}">
									    		${problem.dataset.id}</a></td>
										</c:when>
										<c:otherwise><td>&nbsp;</td></c:otherwise>
									</c:choose>
									<td>${problem.type}</td>
									<td>${problem.details}</td>
									<c:choose>
									    <c:when test="${empty prevId || prevId != problem.dataset.id}">
									    	<td>
												<form class="well,form-horizontal"
													  style="margin: 0px 0px 0px"
												      action="/paul/datasets/${problem.dataset.id}">
													<input type="hidden" name="returnTo" 
												   		   value="/paul/queueDiagnostics/${facilityName}">
													<button class="btn" type="submit" name="regrab">
														Regrab Dataset
													</button>
													<button class="btn" type="submit" name="delete">Delete</button>
													<button class="btn" type="submit" name="archive">Archive</button>
												</form>
											</td>
										</c:when>
										<c:otherwise><td>&nbsp;</td></c:otherwise>
									</c:choose>
								</tr>
								<c:set var="prevId" value="${problem.dataset.id}"/>
							</c:forEach>
						</tbody>
					</table>
				</div>
		</c:if>
		<div class="row-fluid">
			<h2>Missing or duplicated Datasets (between LWM and HWM)</h2>
		</div>
		<div class="row-fluid">
			<div class="span4">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th colspan="2">Datasets missing from queues</th>
						</tr>
						<tr>
							<th class="span4">Dataset Details</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="group" items="${analysis.grouped}">
							<c:if test="${empty group.allInDatabase}">
								<tr>
									<td>${group.inFolder.sourceFilePathnameBase} | <fmt:formatDate
											pattern="yyyy-MM-dd'T'HH:mm:ss"
											value="${group.inFolder.lastFileTimestamp}" />
									</td>
									<td>
										<form class="well,form-horizontal" style="margin: 0px 0px 0px"
											action="/paul/datasets/" method=post>
											<input type="hidden" name="pathnameBase"
												value="${group.inFolder.sourceFilePathnameBase}"> <input
												type="hidden" name="facilityName" value="${facilityName}">
											<input type="hidden" name="returnTo"
												value="/paul/queueDiagnostics/${facilityName}">
											<button class="btn" type="submit" name="grab">Grab
												Dataset</button>
										</form>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="span8">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th colspan="2">Datasets in queues</th>
						</tr>
						<tr>
							<th class="span5">Dataset Details</th>
							<th class="span3">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="group" items="${analysis.grouped}">
							<c:if test="${group.unmatchedInDatabase || group.duplicatesInDatabase}">
							    <c:set var="seenMatched" value=""/>
								<c:forEach var="dataset" items="${group.allDecorated}">
									<tr>
										<c:choose>
											<c:when test="${dataset.inFolder}">
												<td>In-folder version -
													${dataset.sourceFilePathnameBase} <br> <fmt:formatDate
														pattern="yyyy-MM-dd'T'HH:mm:ss"
														value="${dataset.lastFileTimestamp}" />
												</td>
											</c:when>
											<c:otherwise>
												<td>
													<c:choose>
														<c:when test="${dataset.unmatched}">Unmatched - </c:when>
														<c:when test="${empty seenMatched}">Matched - </c:when>
														<c:otherwise>Duplicate - </c:otherwise>
													</c:choose>
													<a href="/paul/datasets/${dataset.id}">Dataset
														#${dataset.id}</a> - ${dataset.sourceFilePathnameBase} <br>
													<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
														value="${dataset.lastFileTimestamp}" />
												</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${dataset.inFolder}">
												<td>&nbsp;</td>
											</c:when>
											<c:when test="${dataset.unmatched}">
												<td>
													<form class="well,form-horizontal"
														style="margin: 0px 0px 0px"
														action="/paul/datasets/${dataset.id}" method=post>
														<input type="hidden" name="returnTo"
															value="/paul/queueDiagnostics/${facilityName}">
														<button class="btn" type="button"
														    ${!dataset.inFolder ? 'disabled' : ''}
															onClick="document.location = '/paul/datasets/${dataset.id}' + 
														             '?regrab&returnTo=/paul/queueDiagnostics/${facilityName}'">
															Regrab Dataset</button>
														<button class="btn" type="submit" name="delete">Delete</button>
														<button class="btn" type="submit" name="archive">Archive</button>
													</form>
												</td>
											</c:when>
											<c:when test="${group.duplicatesInDatabase}">
												<td><form class="well,form-horizontal"
														style="margin: 0px 0px 0px"
														action="/paul/datasets/${dataset.id}" method=post>
														<input type="hidden" name="returnTo"
															value="/paul/queueDiagnostics/${facilityName}">
														<button class="btn" type="submit" name="delete">Delete</button>
													 </form>
											    </td>
											    <c:set var="seenMatched" value="yes"/>
											</c:when>
											<c:otherwise>
												<td></td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="row-fluid"><h2>Actions</h2></div>
		<form method="post">
			<div class="row-fluid">
				<c:set var="isohwm">
					<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${hwmTimestamp}" />
				</c:set>
				New HWM: <input type="text" name="hwmTimestamp" value="${isohwm}">
				<button type="submit" name="setHWM">Change the HWM</button>
			</div>
			<div class="row-fluid">
				<c:set var="isolwm">
					<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss"
						value="${lwmTimestamp}" />
				</c:set>
				New LWM: <input type="text" name="lwmTimestamp" value="${isolwm}">
				<button type="submit" name="setLWM">Change the LWM</button>
			</div>
			<div class="row-fluid">
				<button type="submit" name="analyse">Reanalyse with
					proposed LWM / HWM</button>
			</div>
		</form>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>