// const { ipcRenderer } = require("electron");

// ipcRenderer.on("open-img", (event, filePath) => {
//     // document.getElementById("gmm").setAttribute("src", filePath);
// })

pageReady(function () {
    new Vue({
        el: '#app',
        data () {
            return {
                options: [
                    {
                        label: "sql内容",
                        value: 4
                    },
                    {
                        label: "按sql名称",
                        value: 1
                    },
                    {
                        label: "文件名",
                        value: 2
                    },
                    {
                        label: "作者",
                        value: 3
                    }
                ],
                conditionType: 4,
                conditionValue: "",
                dataList: []
            }
        },
        methods: {
            search() {
                axios.post('http://localhost:8080/fileIndex/search',{
                    conditions: [
                        {
                            type: this.conditionType,
                            value: this.conditionValue
                        }
                    ]
                }).then((response) => {
                    if (response.status === 200) {
                        let resData = response.data;
                        if (resData.status === "000000") {
                            this.dataList = resData.data;
                            this.dataList.forEach(dataItem => {
                                dataItem.content = dataItem.content.replaceAll("\n", "<br/>");
                            });
                        }
                    }
                });
            }
        },
        created () {
        
        }
    })
})