$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    /*$.post()是对$.ajax()的简化*/
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        /*不把表单数据转化为字符串*/
        processData: false,
        /*不让jQuery设置类型*/
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    //元素选择器，属性选择器
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}