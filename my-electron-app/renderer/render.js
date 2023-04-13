const { ipcRenderer } = require("electron");

ipcRenderer.on("open-img", (event, filePath) => {
    document.getElementById("gmm").setAttribute("src", filePath);
})