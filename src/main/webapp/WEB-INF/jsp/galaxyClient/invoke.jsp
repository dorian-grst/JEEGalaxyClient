<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <script type="text/javascript" src="../js/jquery-1.12.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            function redirectToHistoryPage() {
                const queryString = window.location.search;
                const urlParams = new URLSearchParams(queryString);
                const filesURLsValues = urlParams.getAll('filesURLs');
                const filesURLsArray = [];
                filesURLsValues.forEach(value => {
                    filesURLsArray.push(encodeURIComponent(value));
                });
                const galaxyUrl = localStorage.getItem("galaxyUrl");
                const apiKey = localStorage.getItem("apiKey");
                window.location.href = "histories.do?apiKey=" + apiKey + "&galaxyUrl=" + galaxyUrl + "&filesURLs=" + filesURLsArray.join("&filesURLs=");
            }
        </script>


    </head>
    <body>
        <div class="container">
            <c:if test="${empty error}">
                <h1>Your files have been successfully uploaded !</h1>
                <div class="text-container">
                    <p>You can now launch the workflow </p>
                    <a href="https://usegalaxy.eu/workflows/run?id=${workflowId}" target="_blank">here</a>
                </div>
                <p class="warn">⚠️ Don't forget to change your history to have access to the file you just uploaded</p>
            </c:if>
            <c:if test="${not empty error}">
                <h1>An error occurred while uploading the files...</h1>
                <p class="error">${error}</p>
            </c:if>
            <div class="right-container">
                <button type="button" class="submit" onclick="redirectToHistoryPage()">Close</button>
            </div>
        </div>
    </body>
</html>
