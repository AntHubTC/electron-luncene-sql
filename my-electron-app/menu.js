const { Menu, BrowserWindow, dialog }  = require("electron");

// 菜单设置
const menu = Menu.buildFromTemplate([{
    label: "文件",
    submenu: [
        {
            label: "打开",
            properties: ['openFile'],
            click: function () {
                const win = BrowserWindow.getFocusedWindow()
                dialog.showOpenDialog(win, {
                    title: "打开郭超的脑袋",
                    defaultPath: 'C:\\Users\\xcm656\\Pictures\\小花台',
                    filters: [{name: "图片类型", extensions:["png", "jpg"]}],
                    buttonLabel: "打开郭超的脑袋"
                }).then(result => {
                    const imgConst = result.filePaths[0];
                    // win.webContents.executeJavaScript("document.getElementById('gmm').setAttribute('src', 'C:\\Users\\xcm656\\Pictures\\小花台\\小花台2.png")
                    win.webContents.send("open-img", imgConst)
                }).catch(e => {
                    console.error(e)
                })
            }
        },
        {
            label: "子菜单2"
        },
        {
            label: "子菜单3"
        }
    ]
}]);

Menu.setApplicationMenu(menu)
// win = BrowserWindow.getFocusedWindow()
// win.setMenu(menu)