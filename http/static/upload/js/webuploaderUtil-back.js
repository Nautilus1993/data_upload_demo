jQuery(function() {
    var $ = jQuery,
        $thelist = $('#thelist'),
        $btn = $('#ctlBtn'),
        // pathText = $('#pathText').val(),
        state = 'pending',
        contextPath = 'http://10.2.29.115:8080/HTTPServer',
        // contextPath = 'http://10.2.29.115:18060/HTTPServer',,
        //分片大小
        chunkSize = 2 * 1024 * 1024,
        //token
        token = getToken(),

        pathText = '' ,
        //本次上传任务ID
        taskId = '',
        filelist = new Array(),

        //上传信息
        $cabin = $('#cabin'),
        $type = $('#type'),
        $getTime = $('#getTime'),
        $desc = $('#desc'),
        $label =  document.getElementsByName('label'),

        uploader;

    // var $btn = $('#ctlBtn');
    // var $thelist = $('#thelist');
    // var chunkSize = 2 * 1024 * 1024;

    // HOOK 这个必须要再uploader实例化前面
    WebUploader.Uploader.register({
        'before-send-file': 'beforeSendFile',
        'before-send': 'beforeSend',
        'add-file': 'addFiles'
    }, {
        beforeSendFile: function (file) {
            // console.log("beforeSendFile");
            // Deferred对象在钩子回掉函数中经常要用到，用来处理需要等待的异步操作。
            var task = new $.Deferred();
            $.post(contextPath+"/checkFile", {path:pathText, name: file.name},
                function (data) {
                    // console.log(data.data);
                    if(data.code == 200) {
                        var status = data.data.code;
                        task.resolve();
                        if (status == 404) {
                            // 文件不存在，那就正常流程
                        }
                        else if (status == 206) {
                            // 部分已经上传到服务器了，但是差几个模块。
                            file.missChunks = data.data.missChunks;
                        }
                    }else{
                        $('#' + file.id).find('p.state').html("<font color='red'>"+data.msg+"</font>");
                    }
                });
            return $.when(task);
        },
        beforeSend: function (block) {
            var task = new $.Deferred();
            var file = block.file;
            var missChunks = file.missChunks;
            var blockChunk = block.chunk;
            console.log("当前分块号：" + blockChunk);
            if (missChunks !== null && missChunks !== undefined && missChunks !== '') {
                console.log("还没上传的分片号有:" + missChunks);
                var flag = true;
                for (var i = 0; i < missChunks.length; i++) {
                    if (blockChunk == missChunks[i]) {
                        console.log(file.name + "->分块号：" + blockChunk + ":还没上传，现在继续上传。");
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    task.reject();
                } else {
                    task.resolve();
                }
            } else {
                task.resolve();
            }
            return $.when(task);
        },
        addFiles: function( files ) {

            // 遍历files中的文件, 过滤掉不满足规则的。
            console.log("检查有重复的文件");
            //初始化上传任务
        }
    });

    // 实例化
    var uploader = WebUploader.create({
        pick: {
            id: '#picker',
            multiple:true,
            label: '点击选择文件'
        },
        formData: {
            md5: '',
            chunkSize: chunkSize,
            path: pathText,
            token:getToken(),
            id:taskId
        },
        //dnd: '#dndArea',
        //paste: '#uploader',
        swf: contextPath+'/upload/js/Uploader.swf',
        chunked: true,
        chunkSize: chunkSize, // 字节 1M分块
        threads: 5,
        server: contextPath+'/file/upload',
        auto: false,
        //dnd:"#dndArea",
        // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
        disableGlobalDnd: true,
        fileNumLimit: 100,
        fileSizeLimit: 50 * 1024 * 1024 * 1024 * 1024,    // 50 G
        fileSingleSizeLimit: 50 * 1024 * 1024 * 1024    // 50 M
    });

    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
         console.log("fileQueued");
        $thelist.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info" style="cursor:pointer">' + file.name + '</h4>' +
            '<p class="state">等待上传...</p>' +
            '</div>');
        filelist.push({"name":file.name,"size":file.size});
        // console.log(file.ext) // 获取文件的后缀
        // console.log(file.size) // 获取文件的大小
        // console.log(file.name);
        // var html = '<div id="' + file.id + '" class="item"><span>文件名：'+file.name+'</span><span data-file_id="'+file.id+'" class="btn-delete">删除</span><span data-file_id="'+file.id+'" class="btn-retry">重试</span><span data-file_path="'+file.path+'" data-file_name="'+file.name+'" class="btn-download">下载</span><p class="state">等待上传...</p><div class="percentage '+file.id+'" style="width: 0%;"></div></div>';
        // $thelist.append(html);
    });


    //当某个文件的分块在发送前触发，主要用来询问是否要添加附带参数，大文件在开起分片上传的前提下此事件可能会触发多次。
    uploader.onUploadBeforeSend = function (obj, data) {
        //  console.log("onUploadBeforeSend");
        var file = obj.file;
        data.md5 = file.md5 || '';
        data.path = pathText;
    };
    // 上传中
    uploader.on('uploadProgress', function (file, percentage) {
        var width = $('.item').width();
        $('.'+file.id).width(width*percentage);
        getProgressBar(file, percentage, "FILE", "上传进度");
    });

    uploader.on('uploadFinished',function () {
        alert("上传任务结束调用:");
        // $.post(contextPath+"/task/upload/end",{token:getToken(),
        // taskId:taskId},function (data) {
        //     alert(data.message+data.data);
        // });
    });
    uploader.on('uploadStart',function (file) {
        $.post(contextPath+"/file/firstUpload/",{token:getToken(),
            path:pathText,
            fileName:file.name},function(data) {
            //TODO:提示:更新文件状态成功
            alert("上传文件第一次调用:"+file.name+data.message+data.data);
        });
    });

    uploader.on('stopUpload',function (file) {
        uploader.stop(true);
        $.post(contextPath+"/file/stopUpload/",{token:getToken(),
            path:pathText,
            fileName:file.name},function(data) {
            //TODO:提示:更新文件状态成功
            alert("上传文件第一次调用:"+file.name+data.message+data.data);
        });
    });

    /**
     *  生成进度条封装方法
     * @param file 文件
     * @param percentage 进度值
     * @param id_Prefix id前缀
     * @param titleName 标题名
     */
    function getProgressBar(file, percentage, id_Prefix, titleName) {
        var $li = $('#' + file.id), $percent = $li.find('#' + id_Prefix + '-progress-bar');
        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div id="' + id_Prefix + '-progress" class="progress progress-striped active">' +
                '<div id="' + id_Prefix + '-progress-bar" class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>'
            ).appendTo($li).find('#' + id_Prefix + '-progress-bar');
        }
        var progressPercentage = parseInt(percentage * 100) + '%';
        $percent.css('width', progressPercentage);
        $percent.html(titleName + ':' + progressPercentage);
    }

    // 上传返回结果
    uploader.on('uploadSuccess', function (file,response) {
        $.post(contextPath+"/file/endUpload/",{token:getToken(),
            path:pathText,
            fileName:file.name,
            updateStatus:12//已上传
        },function(data) {
            //if(data.data.code==200){
                //文件成功上传系列操作
                var text = ""
                var path = ""
                if (file.pass) {
                    text = "文件秒传功能，文件已上传。"
                    path = file.path;
                    fileClick(file.id,path);
                }else{
                    console.log(response)
                    if(response.code == 200){
                        text = data.message;
                        path = response.data.path;
                        fileClick(file.id,path);
                    }else{
                        text = "<font color='red'>"+response.messag+"</font>";
                    }
                }
            // }else{
            //
            // }

            $('#' + file.id).find('p.state').html(text);
        });



    });
    uploader.on('uploadError', function (file) {
        $.post(contextPath+"/file/endUpload/",{token:getToken(),
            path:pathText,
            fileName:file.name,
            updateStatus:13//上传错误
        },function(data) {
            var text=data.message;
            $('#' + file.id).find('p.state').html('<font color="red">text</font>');
        });

    });
    uploader.on('uploadComplete', function (file) {
        // 隐藏进度条
        fadeOutProgress(file, 'MD5');
        fadeOutProgress(file, 'FILE');
        //清空队列
        // uploader.reset();
        uploader.refresh();
    });

    uploader.on('all', function (type) {
        if (type === 'startUpload') {
            state = 'uploading';
        } else if (type === 'stopUpload') {
            state = 'paused';
        } else if (type === 'uploadFinished') {
            state = 'done';
        }
        if (state === 'uploading') {
            $btn.text('暂停上传');
        } else {
            $btn.text('开始上传');
        }

    });
    $btn.on('click', function () {
        if (state === 'uploading') {
            uploader.stop();
        } else {
            //标签
            var labelList=new Array();
            for(var i=0;i<$label.length;i++){        //for循环遍历数组
                if($label[i].checked){
                    labelList.push({"id":$label[i].value});
                }
            }
            //时间控件
            //将datetime-local转换为Date
            x = $getTime.val();
            var now = new Date();
            if(x!=""){
                now.setFullYear(parseInt(x.substring(0, 4)));
                now.setMonth(parseInt(x.substring(5, 7)) - 1);
                now.setDate(parseInt(x.substring(8, 10)));
                now.setHours(parseInt(x.substring(11, 13)));
                now.setMinutes(parseInt(x.substring(14, 16)));
            }
            $.post(contextPath+"/task/upload/start",{
                cabin:$cabin.val(),
                dataCenter:"zh",
                type:"archive",
                data:$type.val(),
                uploadUser:"admin",
                time:now,
                desc:$desc.val(),
                labelList:JSON.stringify(labelList),
                token:getToken(),
                fileList:JSON.stringify(filelist)},function (data) {
                //TODO:pathText赋值
                if(data.code==200&&data.data.result.code==200){
                    pathText = data.data.tempDir;
                    taskId = data.data.taskId;
                    uploader.upload();
                }else if (data.data!=null&&data.data.result!=null){
                    alert("文件校验失败？？？"+data.data.result.data+":"+data.data.result.message);
                }
            })
        }
    });

    // //取消上传
    // $thelist.on('click', '.btn-delete', function() {
    //     // 从文件队列中删除某个文件id
    //     file_id = $(this).data('file_id');
    //     // uploader.removeFile(file_id); // 标记文件状态为已取消
    //     uploader.removeFile(file_id, true); // 从queue中删除
    //     console.log(uploader.getFiles());
    // });
    // //重试上传，重试指定文件，或者从出错的文件开始重新上传
    // $thelist.on('click', '.btn-retry', function() {
    //     uploader.retry($(this).data('file_id'));
    // });
    // //下载指定文件
    // $thelist.on('click', '.btn-download', function() {
    //     var path = $(this).data('file_path')
    //     var name = $(this).data('file_name')
    //     // var url = "${request.contextPath}/download?path="+path;
    //     var url = contextPath+"/download?path="+path+"&name="+name;
    //     download(url);
    // });

    /**
     * 隐藏进度条
     * @param file 文件对象
     * @param id_Prefix id前缀
     */
    function fadeOutProgress(file, id_Prefix) {
        $('#' + file.id).find('#' + id_Prefix + '-progress').fadeOut();
    }

    $("#picker").click(function(){
        filelist = new Array();
        $(".info").text("");
        $(".state").text("");
        uploader.reset();
        $(".progress progress-striped active").fadeOut();
    });

    function download(url){
        // var iframe = document.createElement("iframe");
        // document.body.appendChild(iframe);
        iframe.src = encodeURI(url);
        iframe.style.display = "none";
    }

    function fileClick(fileId,path){
        $('#' + fileId).click(function(){
            // var url = "${request.contextPath}/download?path="+path;
            // download(url);
            // var action = "${request.contextPath}/download";
            var action = contextPath+"/file/download";
            downloadTemplate(action,"path",path);
        });
    }

    function downloadTemplate(action, type, value){
        var form = document.createElement('form');
        document.body.appendChild(form);
        form.style.display = "none";
        form.action = action;
        form.method = 'post';

        var newElement = document.createElement("input");
        newElement.setAttribute("type","hidden");
        newElement.name = type;
        newElement.value = value;
        form.appendChild(newElement);

        form.submit();
    }
    /*
     * 从Cookies、localstorage中获取token
     */
    function getToken(){
        var strcookie = document.cookie;//获取cookie字符串
        var arrcookie = strcookie.split("; ");//分割
        //遍历匹配
        for ( var i = 0; i < arrcookie.length; i++) {
            var arr = arrcookie[i].split("=");
            if (arr[0] == "token"){
                return arr[1];
            }
        }
        var lst = localStorage.getItem(token);
        if (!lst){
            lst = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLotoXnuqfnrqHnkIblkZgiLCJvcmdhbml6YXRpb25Db2RlIjpudWxsLCJjcmVhdGVkIjoxNTk0MjY0MDAxMzM0LCJleHAiOjE1OTQzNTA0MDEsInVzZXJJZCI6ImFkbWluIn0.7BYdR0auexL26_uPNM94r8mPvDzeEcVhKfIrGA3Eflde3uTuufAeKTE1O8leL8_sR9-bXB-MrFisIhDDFRUTdg";
        }
        return lst;
    }
});