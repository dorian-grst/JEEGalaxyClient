<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Workflow Invoked</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
    function redirectToIndexPage() {
        window.location.href = "index.do";
    }
    </script>
</head>
<body>
    <div class="container">
        <h1>Workflow Invoked !</h1>
        <p>The workflow has been successfully invoked.</p>
        <p>You can find the results of each job in your history at :</p>
        <a href="https://usegalaxy.eu/">https://usegalaxy.eu/</a>
        <div class="right-container">
            <button type="button" class="submit" onclick="redirectToIndexPage()">Next</button>
        </div>
    </div>
</body>
</html>
