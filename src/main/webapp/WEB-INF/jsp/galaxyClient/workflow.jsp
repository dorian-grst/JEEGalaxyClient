<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <script type="text/javascript" src="../js/jquery-1.12.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            const galaxyUrl = localStorage.getItem("galaxyUrl");
            const apiKey = localStorage.getItem("apiKey");

            $(document).ready(function () {
                $('#next_upload').click(function () {
                    const selectedWorkflow = $('input[name="workflowId"]:checked');
                    if (selectedWorkflow.length === 1) {
                        $('#uploadLoader').css('display', 'flex');
                        const workflowId = selectedWorkflow.val();
                        const queryString = window.location.search;
                        const urlParams = new URLSearchParams(queryString);
                        const filesURLsValues = urlParams.getAll('filesURLs');
                        const filesURLsArray = [];
                        filesURLsValues.forEach(value => {
                            filesURLsArray.push(encodeURIComponent(value));
                        });

                        window.location.href = "invoke.do?apiKey=" + apiKey + "&galaxyUrl=" + galaxyUrl + "&workflowId=" + workflowId + "&historyId=${historyId}&filesURLs=" + filesURLsArray.join("&filesURLs=");
                    } else {
                        alert("Please select a workflow before proceeding.");
                    }
                });

                $('#redirect_histories').click(function () {
                    window.open("https://usegalaxy.eu/workflows/create", "_blank");
                    window.location.href = "histories.do?galaxyUrl=" + encodeURIComponent(galaxyUrl) + "&apiKey=" + encodeURIComponent(apiKey);
                });
                $('#back_upload').click(function () {
                    history.back();
                });
            });
        </script>
    </head>
    <body>
        <div id="uploadLoader" class="loader-container">
            <div class="loader"></div>
            <h3>Uploading files...</h3>
            <div class="text-container">
                <p>Check the progress</p>
                <a href="https://usegalaxy.eu" target="_blank">here</a>
            </div>
        </div>
        <div class="container">
            <h1>Select a workflow before the upload</h1>
            <h2>Selected files :</h2>
            <div style="width: 100%;">
                <ul>
                    <c:forEach var="file" items="${filesURLs}">
                        <li style="word-wrap: break-word">${file}</li>
                    </c:forEach>
                </ul>
            </div>
            <h2>Select a compatible workflow to launch:</h2>
            <form>
                <c:forEach var="workflow" items="${compatibleWorkflows}">
                    <label class="inputs-label">
                        <input type="radio" name="workflowId" value="${workflow.id}">
                            ${workflow.name}
                    </label>
                </c:forEach>
                <c:if test="${empty compatibleWorkflows}">
                    <p class="error">No compatible workflow.</p>
                </c:if>
                <input type="hidden" id="historyId" name="historyId" value="${historyId}">
                <c:forEach var="file" items="${fileList}">
                    <input type="hidden" name="fileList" value="${file}">
                </c:forEach>
                <div class="space-container">
                    <button id="back_upload" type="button" class="back">Back</button>
                    <c:if test="${not empty compatibleWorkflows}">
                        <button id="next_upload" type="button" class="submit">Upload</button>
                    </c:if>
                    <c:if test="${empty compatibleWorkflows}">
                        <button id="redirect_histories" type="button" class="submit">Create a Workflow</button>
                    </c:if>
                </div>
            </form>
        </div>
    </body>
</html>
