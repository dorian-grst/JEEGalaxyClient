<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <script type="text/javascript" src="../js/jquery-1.12.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            $(document).ready(function () {
                const queryString = window.location.search;
                const urlParams = new URLSearchParams(queryString);
                const filesURLsValues = urlParams.getAll('filesURLs');
                const filesURLsArray = [];
                filesURLsValues.forEach(value => {
                    filesURLsArray.push(encodeURIComponent(value));
                });
                if (filesURLsValues.length > 0) {
                    const textarea = document.getElementById("filesTextarea");
                    filesURLsValues.forEach(url => {
                        textarea.value += url + "\n";
                    });
                }
                $('#next_workflow').click(function () {
                    const textarea = document.getElementById("filesTextarea");
                    const galaxyUrl = localStorage.getItem("galaxyUrl");
                    const apiKey = localStorage.getItem("apiKey");
                    const lines = textarea.value.split(/\r?\n/);
                    const filesURLsTextArea = [];
                    for (let i = 0; i < lines.length; i++) {
                        const line = lines[i].trim();
                        if (line !== "") {
                            filesURLsTextArea.push(line);
                        }
                    }
                    if (filesURLsTextArea.length > 0) {
                        document.querySelector('.loader-container').style.display = 'flex';
                        window.location.href = "workflow.do?apiKey=" + apiKey + "&galaxyUrl=" + galaxyUrl + "&historyId=${historyId}&filesURLs=" + filesURLsTextArea.join("&filesURLs=");
                    } else {
                        alert("Please enter at least one file link.");
                    }
                });
                $('#back_workflow').click(function () {
                    history.back();
                });
            });
        </script>

    </head>
    <body>
        <div class="loader-container">
            <div class="loader"></div>
            <h3>Finding compatible workflows...</h3>
        </div>
        <div class="container">
            <h1>Select files to upload</h1>
            <h4>Write in the textarea the absolute path or url of the files you want to upload</h4>
            <p>📝 Use one line per file as in the placeholders</p>
            <textarea id="filesTextarea" rows="5" placeholder='https://application/example/file.fasta &#10;/home/usr/example.txt'></textarea>
            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <div class="space-container">
                <button id="back_workflow" type="button" class="back">Back</button>
                <button id="next_workflow" type="button" class="submit">Next</button>
            </div>
        </div>
    </body>
</html>