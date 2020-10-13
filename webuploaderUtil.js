document.write("<script type='text/javascript' src='./upload/js/base64.js'></script>");
jQuery(function () {
    var $ = jQuery,
        $thelist = $('#thelist .table'),
        $btn = $('#ctlBtn'),
        $btnAddHTYForm = $('#addHTYForm'),
        $btnAddDataResultAchieveReportForm = $('#addDataResultAchieveReportForm'),
        state = 'pending',
        contextPath = 'http://10.2.29.115:18090/HTTPServer',
        //contextPath = 'http://192.168.109.214:18050/HTTPServer',
        //分片大小
        chunkSize = 2 * 1024 * 1024,
        pathText = '',
        token = '',
        //本次上传任务ID
        taskId = '',
        filelist = new Array(),
        //上传信息
        $token = $('#token'),
        $user = $('#user'),
        $group = $('#group'),
        $cabin = $('#cabin'),
        $center = $('#center'),
        $MainType = $('#MainType'),
        $type = $('#type'),
        $getTime = $('#getTime'),
        $desc = $('#desc'),
        $label = $('.check-menu-item'),
        formInfo = new Array(),
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
            $.post(contextPath + "/checkFile", {path: pathText, name: file.name,token:$token.val()},
                function (data) {
                    // console.log(data.data);
                    if (data.code == 200) {
                        var status = data.data.code;
                        task.resolve();
                        if (status == 404) {
                            // 文件不存在，那就正常流程
                        } else if (status == 206) {
                            // 部分已经上传到服务器了，但是差几个模块。
                            file.missChunks = data.data.missChunks;
                        }
                    } else {
                        $('#' + file.id).find('p.state').html("<font color='red'>" + data.msg + "</font>");
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
        addFiles: function (files) {

            // 遍历files中的文件, 过滤掉不满足规则的。
            console.log("检查有重复的文件");
            //初始化上传任务
        }
    });

    // 实例化
    var uploader = WebUploader.create({
        pick: {
            id: '#picker',
            multiple: true,
            label: '点击选择文件'
        },
        formData: {
            md5: '',
            chunkSize: chunkSize,
            path: pathText,
            token:token,
            id: taskId
        },
        //dnd: '#dndArea',
        //paste: '#uploader',
        swf: contextPath + '/upload/js/Uploader.swf',
        chunked: true,
        chunkSize: chunkSize, // 字节 1M分块
        threads: 5,
        server: contextPath + '/file/upload',
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
        console.log(file.name + " IN Queued");
        filelist.push({"name": file.name, "size": file.size,"lastModifyTime":file.lastModifiedDate});
        $thelist.append('<tr id="' +
            file.id + '" class="file-item">' +
            '<td width="5%" class="file-num">file.id</td>' + '<td class="file-name">' +
            file.name + '</td>' + '<td width="20%" class="file-size">' +
            (file.size / 1024 / 1024).toFixed(1) + 'M' + '</td>' +
            '<td width="20%" class="file-pro">0%</td>' +
            '<td class="file-status">等待上传</td>' +
            '<td width="20%" class="file-manage"><a class="stop-btn" href="javascript:;">终止</a><a class="remove-this" href="javascript:;">取消</a></td>' +
            '</tr>');
        //暂停上传的文件
        $thelist.on('click', '.stop-btn', function () {
            uploader.stop(true);
        })
        //删除上传的文件
        $thelist.on('click', '.remove-this', function () {
            if ($(this).parents(".file-item").attr('id') == file.id) {
                uploader.removeFile(file);
                for (var i = 0; i < filelist.length; i++) {
                    if (filelist[i].name == file.name) {
                        filelist.splice(i, 1);
                        break;
                    }
                }
                $(this).parents(".file-item").remove();
            }
        })


        // $thelist.append('<div id="' + file.id + '" class="item">' +
        //     '<h4 class="info" style="cursor:pointer">' + file.name + '</h4>' +
        //     '<p class="state">等待上传...</p>' +
        //     '</div>');
        // filelist.push({"name":file.name,"size":file.size});
        // console.log(file.ext) // 获取文件的后缀
        // console.log(file.size) // 获取文件的大小
        // console.log(file.name);
        // var html = '<div id="' + file.id + '" class="item"><span>文件名：'+file.name+'</span><span data-file_id="'+file.id+'" class="btn-delete">删除</span><span data-file_id="'+file.id+'" class="btn-retry">重试</span><span data-file_path="'+file.path+'" data-file_name="'+file.name+'" class="btn-download">下载</span><p class="state">等待上传...</p><div class="percentage '+file.id+'" style="width: 0%;"></div></div>';
        // $thelist.append(html);


    });
// 文件上传过程中创建进度条实时显示
    uploader.on('uploadProgress', function (file, percentage) {
        $("td.file-pro").text("");
        var $li = $('#' + file.id).find('.file-pro'),
            $percent = $li.find('.file-progress .progress-bar');
        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="file-progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>' + '<br/><div class="per">0%</div>').appendTo($li).find('.progress-bar');
        }
        $li.siblings('.file-status').text('上传中');
        $li.find('.per').text((percentage * 100).toFixed(2) + '%');
        $percent.css('width', percentage * 100 + '%');
    });
    // 文件上传成功
    // uploader.on( 'uploadSuccess', function( file ) {
    //     $( '#'+file.id ).find('.file-status').text('已上传');
    // });
    // 文件上传失败，显示上传出错
    // uploader.on( 'uploadError', function( file ) {
    //     $( '#'+file.id ).find('.file-status').text('上传出错');
    // });
    // 完成上传完后将视频添加到视频列表，显示状态为：转码中
    uploader.on('uploadComplete', function (file) {
        // $( '#'+file.id ).find('.file-progress').fadeOut();
    });


    //当某个文件的分块在发送前触发，主要用来询问是否要添加附带参数，大文件在开起分片上传的前提下此事件可能会触发多次。
    uploader.onUploadBeforeSend = function (obj, data) {
        //  console.log("onUploadBeforeSend");
        var file = obj.file;
        data.md5 = file.md5 || '';
        data.path = pathText;
        data.token = token;
    };
    // 上传中
    // uploader.on('uploadProgress', function (file, percentage) {
    //     var width = $('.item').width();
    //     $('.'+file.id).width(width*percentage);
    //     getProgressBar(file, percentage, "FILE", "上传进度");
    // });

    uploader.on('uploadFinished', function () {
        //alert("上传任务结束调用:");
        console.log("上传任务结束调用:");
        // $.post(contextPath+"/task/upload/end",{token:getToken(),
        // taskId:taskId},function (data) {
        //     alert(data.message+data.data);
        // });
    });
    uploader.on('uploadStart', function (file) {
        $.post(contextPath + "/file/firstUpload/", {
            token: token,
            path: pathText,
            fileName: file.name
        }, function (data) {
            //TODO:提示:更新文件状态成功
            //alert("上传文件第一次调用:" + file.name + data.message + data.data);
            console.log("上传文件第一次调用:" + file.name + data.message + data.data);
        });
    });

    uploader.on('stopUpload', function (file) {
        uploader.stop(true);
        alert(file + "暂停上传")
        // $.post(contextPath+"/file/stopUpload/",{token:getToken(),
        //     path:pathText,
        //     fileName:file.name},function(data) {
        //     //TODO:提示:更新文件状态成功
        //     alert("上传文件第一次调用:"+file.name+data.message+data.data);
        // });
    });

    /**
     *  生成进度条封装方法
     * @param file 文件
     * @param percentage 进度值
     * @param id_Prefix id前缀
     * @param titleName 标题名
     */
    // function getProgressBar(file, percentage, id_Prefix, titleName) {
    //     var $li = $('#' + file.id), $percent = $li.find('#' + id_Prefix + '-progress-bar');
    //     // 避免重复创建
    //     if (!$percent.length) {
    //         $percent = $('<div id="' + id_Prefix + '-progress" class="progress progress-striped active">' +
    //             '<div id="' + id_Prefix + '-progress-bar" class="progress-bar" role="progressbar" style="width: 0%">' +
    //             '</div>' +
    //             '</div>'
    //         ).appendTo($li).find('#' + id_Prefix + '-progress-bar');
    //     }
    //     var progressPercentage = parseInt(percentage * 100) + '%';
    //     $percent.css('width', progressPercentage);
    //     $percent.html(titleName + ':' + progressPercentage);
    // }

    // 上传返回结果
    uploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).find('.file-status').text('已上传');
        //如果要上传的是航天中心的数据，需要在上传成功的时候将元数据归档
        var i=0;
        for(i =0;i<formInfo.length;i++){
            if(formInfo[i].fileFullName == file.name){
                break;
            }
        }
        //如何已经遍历完没有找到所寻的数据
        if(i==formInfo.length){
            i=0;
        }
        $.post(contextPath + "/file/endUpload/", {
            token: token,
            path: pathText,
            fileName: file.name,
            updateStatus: 12,//已上传
            formInfo:formInfo.length>0?JSON.stringify(formInfo[i]):null
        }, function (data) {
            //if(data.data.code==200){
            //文件成功上传系列操作
            var text = ""
            var path = ""
            if (file.pass) {
                text = "文件秒传功能，文件已上传。"
                path = file.path;
                fileClick(file.id, path);
            } else {
                console.log(response)
                if (response.code == 200) {
                    text = data.message;
                    path = response.data.path;
                    fileClick(file.id, path);
                } else {
                    text = "<font color='red'>" + response.messag + "</font>";
                }
            }
            // }else{
            //
            // }

            $('#' + file.id).find('p.state').html(text);
        });


    });
    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('.file-status').text('上传出错');
        $.post(contextPath + "/file/endUpload/", {
            token: token,
            path: pathText,
            fileName: file.name,
            updateStatus: 13//上传错误
        }, function (data) {
            var text = data.message;
            $('#' + file.id).find('p.state').html('<font color="red">text</font>');
        });

    });
    uploader.on('uploadComplete', function (file) {
        // 隐藏进度条
        // fadeOutProgress(file, 'MD5');
        // fadeOutProgress(file, 'FILE');
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
            var labelList = new Array();
            for (var i = 0; i < $label.length; i++) {        //for循环遍历数组
                if ($label[i].checked) {
                    labelList.push({"id": $label[i].value});
                }
            }
            //时间控件
            //将datetime-local转换为Date
            x = $getTime.val();
            var now = new Date();
            if (x != null) {
                now.setFullYear(parseInt(x.substring(0, 4)));
                now.setMonth(parseInt(x.substring(5, 7)) - 1);
                now.setDate(parseInt(x.substring(8, 10)));
                now.setHours(parseInt(x.substring(11, 13)));
                now.setMinutes(parseInt(x.substring(14, 16)));
            }
            var formInfos = $('form').serializeArray();
            if(formInfos.length>0){
                var firtsData = formInfos[0].name
                var obj={}
                formInfo = new Array();
                obj[formInfos[0].name]=formInfos[0].value;
                for(var i =1;i<formInfos.length;i++){
                    if (firtsData==formInfos[i].name){
                        formInfo.push(obj);
                        obj={}
                    }
                    obj[formInfos[i].name]=formInfos[i].value;
                }
                formInfo.push(obj);
            }

            console.log(formInfo)

            $.post(contextPath + "/task/upload/start", {
                cabinName: $cabin.val(),
                dataCenter: $center.val(),
                mainType: $MainType.val(),
                subType: $type.val(),
                uploadUser: $user.val(),
                userGroup:$group.val(),
                getTime:now,
                dataDesc: $desc.val(),
                labelsIds: JSON.stringify(labelList),
                token: $token.val(),
                fileInfoDtos: JSON.stringify(filelist)
            }, function (data) {
                //TODO:pathText赋值
                if (data.code == 200 && data.data.result.code == 200) {
                    pathText = data.data.tempDir;
                    taskId = data.data.taskId;
                    token = $token.val(),
                    uploader.upload();
                } else if (data.data != null && data.data.result != null) {
                    // alert("文件校验失败 " + data.data.result.data + ":" + data.data.result.message);
                    console.log("文件校验失败 " + data.data.result.data + ":" + data.data.result.message);

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

    $("#picker").click(function () {
        filelist = new Array();
        formInfo = new Array();
        $(".info").text("");
        $(".state").text("");
        $(".file-item").text("");

        uploader.reset();
        $(".progress progress-striped active").fadeOut();
    });

    function download(url) {
        // var iframe = document.createElement("iframe");
        // document.body.appendChild(iframe);
        iframe.src = encodeURI(url);
        iframe.style.display = "none";
    }

    /**
     * 点击下载文件--直接从前台获取下载路径
     * @param fileId
     * @param path 下载路径
     */
    function fileClick(fileId, path) {
        $('#' + fileId).click(function () {
            // var url = "${request.contextPath}/download?path="+path;
            // download(url);
            // var action = "${request.contextPath}/download";
            var action = contextPath + "/file/download";
            //path = encodeURI(path);
            //path = encode64(utf16to8(path))
            downloadTemplate(action, "path", encode64(utf16to8(path)));
        });
    }

    function downloadTemplate(action, type, value) {
        var form = document.createElement('form');
        document.body.appendChild(form);
        form.style.display = "none";
        form.action = action;
        form.method = 'post';

        var newElement = document.createElement("input");
        newElement.setAttribute("type", "hidden");
        newElement.name = type;
        newElement.value = value;
        form.appendChild(newElement);

        form.submit();
    }

    //航天员支持中心文件清单表
    $btnAddHTYForm.click(function () {
        var form = "<br/>\n" +
            "        <p>ID: <input type=\"text\" name=\"id\"  style=\"display:none\" /></p>\n" +
            "        <p>序号: <input type=\"text\" name=\"fileId\" style=\"display:none\" /></p>\n" +
            "        <p>收文号: <input type=\"text\" name=\"recvCode\" value=\"1\"/></p>\n" +
            "        <p>文件名: <input type=\"text\" name=\"fileFullName\" /></p>\n" +
            "        <p>文件编号: <input type=\"text\" name=\"fileCode\" value=\"1\"/></p>\n" +
            "        <p>原发文号: <input type=\"text\" name=\"orignalCode\" value=\"1\"/></p>\n" +
            "        <p>文件时间: <input type=\"datetime-local\" name=\"fileTime\" /></p>\n" +
            "        <p>密级: <input type=\"text\" name=\"secretLevel\" value=\"1\" /></p>\n" +
            "        <p>发文单位: <input type=\"text\" name=\"fileDep\" value=\"1\" /></p>\n" +
            "        <p>页数: <input type=\"text\" name=\"pages\" value=\"1\"/></p>\n" +
            "        <p>编写人: <input type=\"text\" name=\"author\" value=\"1\"/></p>\n" +
            "        <p>状态: <input type=\"text\" name=\"state\" value=\"1\"/></p>\n" +
            "        <p>备注: <input type=\"text\" name=\"comment\" value=\"1\"/></p>"
        $('#formHTY').append(form);
    })
    //数据成果 表单添加
    $btnAddDataResultAchieveReportForm.click(function () {
        var form = "<br/>\n" +
            "        <p>ID: <input type=\"text\" name=\"id\"  style=\"display:none\" /></p>\n" +
            "        <p>序号: <input type=\"text\" name=\"fileId\" style=\"display:none\" /></p>\n" +
            "        <p>任务代号: <input type=\"text\" name=\"mission\" value=\"1\"/></p>\n" +
            "        <p>舱段代号: <input type=\"text\" name=\"section\" value=\"1\"/></p>\n" +
            "        <p>数据成果类型: <input type=\"text\" name=\"achieveKind\" value=\"1\"/></p>\n" +
            "        <p>数据成果名称: <input type=\"text\" name=\"achieveName\" value=\"1\"/></p>\n" +
            "        <p>数据成果来源: <input type=\"text\" name=\"achieveFrom\" value=\"1\" /></p>\n" +
            "        <p>数据成果版权方: <input type=\"text\" name=\"achieveCopyright\" value=\"1\" /></p>\n" +
            "        <p>创建人: <input type=\"text\" name=\"founder\" value=\"1\"/></p>\n" +
            "        <p>创建时间: <input type=\"datetime-local\" name=\"founderTime\" /></p>\n" +
            "        <p>数据成果格式: <input type=\"text\" name=\"format\" value=\"1\"/></p>\n" +
            "        <p>成果描述: <input type=\"text\" name=\"descOfAchievs\" value=\"1\"/></p>\n"
        $('#formDataAchieve').append(form);
    })
});