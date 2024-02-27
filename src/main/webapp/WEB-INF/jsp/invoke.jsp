<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <script>
            function redirectToHistoryPage(galaxyUrl, apiKey) {
                window.location.href = "histories.do?galaxyUrl=" + encodeURIComponent(galaxyUrl) + "&apiKey=" + encodeURIComponent(apiKey);
            }
        </script>


    </head>
    <body>
        <div class="container">
            <h1>Your files have been successfully uploaded !</h1>
            <div class="text-container">
                <p>You can now launch the workflow </p>
                <a href="https://usegalaxy.eu/workflows/run?id=${workflowId}" target="_blank">here</a>
            </div>
            <p class="warn">⚠️ Don't forget to change your history to have access to the file you just uploaded</p>
            <div class="right-container">
                <button type="button" class="submit" onclick="redirectToHistoryPage('${galaxyUrl}', '${apiKey}')">Close</button>
            </div>
        </div>
    </body>
</html>
