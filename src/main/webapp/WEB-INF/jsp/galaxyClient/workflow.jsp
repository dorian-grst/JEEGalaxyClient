<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            function prepareNextPage() {
                document.querySelector('#upload').style.display = 'flex';
                const selectedWorkflow = document.querySelector('input[name="workflowId"]:checked');
                if (selectedWorkflow) {
                    const workflowId = selectedWorkflow.value;
                    const historyId = "${historyId}";
                    const form = document.getElementById("workflowForm");
                    form.historyId.value = historyId;
                    form.workflowId.value = workflowId;
                    form.submit();
                } else {
                    alert("Please select a workflow before proceeding.");
                }
            }

            function redirectToHistoryPageAndCreateWorkflow(galaxyUrl, apiKey) {
                document.querySelector('#redirect').style.display = 'flex';
                window.open("https://usegalaxy.eu/workflows/create", "_blank");
                window.location.href = "histories.do?galaxyUrl=" + encodeURIComponent(galaxyUrl) + "&apiKey=" + encodeURIComponent(apiKey);
            }
        </script>
    </head>
    <body>
        <div id="upload" class="loader-container">
            <div class="loader"></div>
            <h3>Uploading files...</h3>
            <div class="text-container">
                <p>Check the progress</p>
                <a href="https://usegalaxy.eu" target="_blank">here</a>
            </div>
        </div>
        <div id="redirect" class="loader-container">
            <div class="loader"></div>
            <h3>Redirect...</h3>
        </div>
        <div class="container">
            <h1>Select a workflow before the upload</h1>
            <h2>Selected files :</h2>
            <div style="width: 100%;">
                <ul>
                    <c:forEach var="file" items="${fileList}">
                        <li style="word-wrap: break-word">${file}</li>
                    </c:forEach>
                </ul>
            </div>
            <h2>Select a compatible workflow to launch:</h2>
            <form action="invoke.do" method="get">
                <c:forEach var="workflow" items="${compatibleWorkflows}">
                    <label class="inputs-label">
                        <input type="radio" name="workflowId" value="${workflow.id}">
                            ${workflow.name}
                    </label>
                </c:forEach>
                <c:if test="${empty compatibleWorkflows}">
                    <p>No compatible workflow.</p>
                </c:if>
                <input type="hidden" id="historyId" name="historyId" value="${historyId}">
                <c:forEach var="file" items="${fileList}">
                    <input type="hidden" name="fileList" value="${file}">
                </c:forEach>
                <div class="space-container">
                    <button type="button" class="back" onclick="history.back()">Back</button>
                    <c:if test="${not empty compatibleWorkflows}">
                        <button type="submit" class="submit" onclick="prepareNextPage()">Upload</button>
                    </c:if>
                    <c:if test="${empty compatibleWorkflows}">
                        <button type="button" class="submit" onclick="redirectToHistoryPageAndCreateWorkflow('${galaxyUrl}', '${apiKey}')">Create a Workflow</button>
                    </c:if>
                </div>
            </form>
        </div>
    </body>
</html>

