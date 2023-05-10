Vue.config.keyCodes = {
    f1: 112
}

/**
 * 函数防抖
 */
Vue.prototype.debounce = function (fn, delay = 200) {
    // 记录上一次的延时器
    let timer = null;
    return function() {
        const args = arguments;
        const that = this;
        // 清除上一次延时器
        clearTimeout(timer)
        timer = setTimeout(function() {
            fn.apply(that, args)
        }, delay);
    }
}

var commonEventBus = new Vue();

Object.defineProperties(Vue.prototype, {
    $eventBus: { // 事件总线
        get: function () {
            return commonEventBus
        }
    }
})

//  RESPONSE 响应异常拦截
axios.interceptors.response.use(response => {
    return new Promise((resolve, reject) => {
        if (response.status === 200 && response.data) {
            let busData = response.data;
            if (busData.status !== '000000') {
                commonEventBus.$message.error(busData.data);
                return reject(response);
            }
            return resolve(response);
        }
        return reject(response);
    });
}, response => {
    return new Promise((resolve, reject) => {
        //==============  错误处理  ====================
        if (response && response.response) {
            switch (response.response.status) {
                case 400: response.message = '请求错误(400)'; break;
                case 401: response.message = '未授权，请重新登录(401)'; break;
                case 403: response.message = '拒绝访问(403)'; break;
                case 404: response.message = '请求出错(404)'; break;
                case 408: response.message = '请求超时(408)'; break;
                case 500: response.message = '服务器错误(500)'; break;
                case 501: response.message = '服务未实现(501)'; break;
                case 502: response.message = '网络错误(502)'; break;
                case 503: response.message = '服务不可用(503)'; break;
                case 504: response.message = '网络超时(504)'; break;
                case 505: response.message = 'HTTP版本不受支持(505)'; break;
                default: response.message = `连接出错(${response.response.status})!`;
            }
        } else {
            response.message = '连接服务器失败!'
        }
        commonEventBus.$message.error(response.message);
        return reject(response);
    });
});

//  RESPONSE 响应异常拦截
// axios.interceptors.response.use(response=> {
//     if (response.status === 200 && response.data) {
//         let busData = response.data;
//         if (busData.status !== '000000') {
//             commonEventBus.$message.error(busData.data);
//             return Promise.reject(response);
//         }
//         return Promise.resolve(response);
//     }
//     return Promise.reject(response);
// }, err=> {
//     //==============  错误处理  ====================
//     if (err && err.response) {
//         switch (err.response.status) {
//             case 400: err.message = '请求错误(400)'; break;
//             case 401: err.message = '未授权，请重新登录(401)'; break;
//             case 403: err.message = '拒绝访问(403)'; break;
//             case 404: err.message = '请求出错(404)'; break;
//             case 408: err.message = '请求超时(408)'; break;
//             case 500: err.message = '服务器错误(500)'; break;
//             case 501: err.message = '服务未实现(501)'; break;
//             case 502: err.message = '网络错误(502)'; break;
//             case 503: err.message = '服务不可用(503)'; break;
//             case 504: err.message = '网络超时(504)'; break;
//             case 505: err.message = 'HTTP版本不受支持(505)'; break;
//             default: err.message = `连接出错(${err.response.status})!`;
//         }
//     } else {
//         err.message = '连接服务器失败!'
//     }
//     commonEventBus.$message.error(err.message);
//
//     return Promise.resolve(err);
// });

// 页面准备
function pageReady(callBack) {
    let isLoaded = false
    let loadedFun = function () {
        if (!isLoaded) {
            isLoaded = true;
            callBack();
        }
    }
    window.onload = loadedFun;
    if ('addEventListener' in document){
        document.addEventListener('DOMContentLoaded', loadedFun, false)//false代表在冒泡阶段触发，true在捕获阶段触发
    }
    document.onreadystatechange = function(){
        if(document.readyState === 'complete'){
            loadedFun();
        }
    }
}