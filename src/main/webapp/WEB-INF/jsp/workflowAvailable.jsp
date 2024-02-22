<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Workflow Available</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
    function prepareNextPage() {
        document.querySelector('.loader-container').style.display = 'flex';
        const selectedWorkflow = document.querySelector('input[name="selectedWorkflow"]:checked');
        if (selectedWorkflow) {
            const selectedWorkflowId = selectedWorkflow.value;
            const historyId = "${historyId}";
            const form = document.getElementById("workflowForm");
            form.historyId.value = historyId;
            form.selectedWorkflowId.value = selectedWorkflowId;
            form.submit();
        } else {
            alert("Please select a workflow before proceeding.");
        }
    }
    </script>
</head>

<body>
	<div class="loader-container">
		<div class="loader"></div>
		<h3>Invoking workflow...</h3>
		<div class="text-container">
			<h4>Check the progress at :</h4>
			<a href="https://usegalaxy.eu/" target="_blank">https://usegalaxy.eu/</a>
		</div>
	</div>
    <div class="container">
        <h1>Workflows Available</h1>
        <h2>Selected Datasets:</h2>
        <ul>
	       	<c:forEach var="dataset" items="${selectedDatasets}">
	           	<li>${dataset.name}</li>
	       	</c:forEach>
        </ul>
        <h2>Select compatible Workflow to launch:</h2>
        <form action="invoked.do" method="get">
            <c:forEach var="workflow" items="${compatibleWorkflows}">
                <label class="inputs-label">
                    <input type="radio" name="selectedWorkflow" value="${workflow.id}">
                    ${workflow.name}
                </label>
            </c:forEach>
            <c:if test="${empty compatibleWorkflows}">
            	<p>No compatible workflow.</p>
        	</c:if>
            <input type="hidden" id="historyId" name="historyId" value="${historyId}">
            <c:forEach var="dataset" items="${selectedDatasetsIds}">
        		<input type="hidden" name="selectedDatasetsIds" value="${dataset}">
    		</c:forEach>
            <div class="space-container">
                <button type="button" class="back" onclick="history.back()">Back</button>
                <c:if test="${not empty compatibleWorkflows}">
            		<button type="submit" class="submit" onclick="prepareNextPage()">Next</button>
        		</c:if>
            </div>
        </form>
    </div>
</body>
</html>

