<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag"%>
<title>Data Grabber</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag"%>
	<div class="container-fluid">
		<div class="hero-unit">
			<h1>CMM Data Grabber</h1>
			<p id="about">The Data Grabber transfers files written to the instrument's 
				S: drive into the MIRAGE data repository.</p>
		</div>
		<div class="row">
			<div class="span6">
				<h2>Start or End Instrument Sessions</h2>
				<p>If the ACLS login on the instrument is not working, you can use
					the buttons below to start or end a Session.</p>
			</div>
			<div class="span6">
				<h2>Claim Data</h2>
				<p>If someone uses an instrument while not logged in, the
					Data Grabber cannot tell who owns the data files that were created.
					The button below allows you to "claim" data files as your own.</p>
			</div>
		</div>
		<div class="row">
			<div class="span6">
				<p>
					<a class="btn btn-large btn-primary" 
					   href="/paul/facilitySelect?next=facilityLogin">Start a session</a> 
					<a class="btn btn-large btn-primary" 
					   href="/paul/facilitySelect?next=facilityLogout">End a session</a>
				</p>
			</div>
			<div class="span6">
				<p>
					<a class="btn btn-large btn-primary" 
					   href="/paul/facilitySelect?next=claimDatasets">Claim data</a>
				</p>
			</div>
		</div>
		<div class="row">
			<div class="span6">
				<h2>MIRAGE</h2>
				<p>The MIRAGE data repository manages the data files produced by
					many of CMM's electron microscopes, and other instruments. You can
					login to MIRAGE from any web browser, search and view your files,
					and download them to your PC or laptop.</p>
				
			</div>
			<div class="span6">
				<h2>ACLS</h2>
				<p>ACLS is CMM's laboratory management system and accounting
					system. You need to use ACLS to make instrument session bookings.</p>
				
			</div>
		</div>
		<div class="row">
			<div class="span6">
				<p>
					<a class="btn btn-large" href="/paul/mirage">Go to MIRAGE</a>
				</p>
			</div>
			<div class="span6">
				<p>
					<a class="btn btn-large" href="/paul/acls">Go to ACLS</a>
				</p>
			</div>
		</div>
	</div>
	<!-- /container -->
	<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag"%>
</body>
</html>