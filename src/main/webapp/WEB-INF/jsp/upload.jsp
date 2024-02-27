<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <script>
            function handleNext() {
                document.querySelector('.loader-container').style.display = 'flex';
                const textarea = document.getElementById("filesTextarea");
                const lines = textarea.value.split(/\r?\n/);
                const fileList = [];

                for (let i = 0; i < lines.length; i++) {
                    const line = lines[i].trim();
                    if (line !== "") {
                        fileList.push(line);
                    }
                }

                if (fileList.length > 0) {
                    const fileListString = fileList.join(",");

                    window.location.href = "workflow.do?historyId=${historyId}&fileList=" + encodeURIComponent(fileListString);
                } else {
                    alert("Please enter at least one file link.");
                }
            }
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
            <div class="space-container">
                <button type="button" class="back" onclick="history.back()">Back</button>
                <button type="button" class="submit" onclick="handleNext()">Next</button>
            </div>
        </div>
    </body>
</html>