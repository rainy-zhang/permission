<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>部门管理</title>
    <jsp:include page="/common/backend_common.jsp"/>
    <jsp:include page="/common/page.jsp"/>
</head>
<body class="no-skin" youdao="bind" style="background: white">
<input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5"/>

<div class="page-header">
    <h1>
        用户管理
        <small>
            <i class="ace-icon fa fa-angle-double-right"></i>
            维护部门与用户关系
        </small>
    </h1>
</div>
<div class="main-content-inner">
    <div class="col-sm-3">
        <div class="table-header">
            部门列表&nbsp;&nbsp;
            <a class="green" href="#">
                <i class="ace-icon fa fa-plus-circle orange bigger-130 dept-add"></i>
            </a>
        </div>
        <div id="deptList">
        </div>
    </div>
    <div class="col-sm-9">
        <div class="col-xs-12">
            <div class="table-header">
                用户列表&nbsp;&nbsp;
                <a class="green" href="#">
                    <i class="ace-icon fa fa-plus-circle orange bigger-130 user-add"></i>
                </a>
            </div>
            <div>
                <div id="dynamic-table_wrapper" class="dataTables_wrapper form-inline no-footer">
                    <div class="row">
                        <div class="col-xs-6">
                            <div class="dataTables_length" id="dynamic-table_length"><label>
                                展示
                                <select id="pageSize" name="dynamic-table_length" aria-controls="dynamic-table" class="form-control input-sm">
                                    <option value="10">10</option>
                                    <option value="25">25</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                </select> 条记录 </label>
                            </div>
                        </div>
                    </div>
                    <table id="dynamic-table" class="table table-striped table-bordered table-hover dataTable no-footer" role="grid" aria-describedby="dynamic-table_info" style="font-size:14px">
                        <thead>
                        <tr role="row">
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                姓名
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                所属部门
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                邮箱
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                电话
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                状态
                            </th>
                            <th class="sorting_disabled" rowspan="1" colspan="1" aria-label=""></th>
                        </tr>
                        </thead>
                        <tbody id="userList"></tbody>
                    </table>
                    <div class="row" id="userPage"><%-- 分页信息 --%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%-- 编辑部门时的弹出层 --%>
<div id="dialog-dept-form" style="display: none;">
    <form id="deptForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td style="width: 80px;"><label for="parentId">上级部门</label></td>
                <td>
                    <select id="parentId" name="parentId" data-placeholder="选择部门" style="width: 200px;"></select>
                    <input type="hidden" name="id" id="deptId"/>
                </td>
            </tr>
            <tr>
                <td><label for="deptName">名称</label></td>
                <td><input type="text" name="name" id="deptName" value="" autocomplete="off" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="deptSeq">顺序</label></td>
                <td><input type="number" name="seq" id="deptSeq" value="1" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="deptRemark">备注</label></td>
                <td><textarea name="remark" id="deptRemark" autocomplete="off" class="text ui-widget-content ui-corner-all" rows="3" cols="25"></textarea></td>
            </tr>
        </table>
    </form>
</div>
<%-- 编辑用户时的弹出层 --%>
<div id="dialog-user-form" style="display: none;">
    <form id="userForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td style="width: 80px;"><label for="parentId">所在部门</label></td>
                <td>
                    <select id="deptSelectId" name="deptId" data-placeholder="选择部门" style="width: 200px;"></select>
                </td>
            </tr>
            <tr>
                <td><label for="userName">名称</label></td>
                <input type="hidden" name="id" id="userId"/>
                <td><input type="text" name="username" id="userName" autocomplete="off" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="userEmail">邮箱</label></td>
                <td><input type="email" name="email" id="userEmail" autocomplete="off" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="userTelephone">电话</label></td>
                <td><input type="text" name="telephone" id="userTelephone" autocomplete="off" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="userStatus">状态</label></td>
                <td>
                    <select id="userStatus" name="status" data-placeholder="选择状态" style="width: 150px;">
                        <option value="1">正常</option>
                        <option value="0">冻结</option>
                        <option value="2">删除</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>
<%-- 部门列表模板 --%>
<script id="deptListTemplate" type="x-tmpl-mustache">
<ol class="dd-list">
    {{#deptList}}
        <li class="dd-item dd2-item dept-name" id="dept_{{id}}" href="javascript:void(0)" data-id="{{id}}">
            <div class="dd2-content" style="cursor:pointer;">
            {{name}}
            <span style="float:right;">
                <a class="green dept-edit" href="#" data-id="{{id}}" >
                    <i class="ace-icon fa fa-pencil bigger-100"></i>
                </a>
                &nbsp;
                <a class="red dept-delete" href="#" data-id="{{id}}" data-name="{{name}}">
                    <i class="ace-icon fa fa-trash-o bigger-100"></i>
                </a>
            </span>
            </div>
        </li>
    {{/deptList}}
</ol>
</script>
<%-- 用户列表模板 --%>
<script id="userListTemplate" type="x-tmpl-mustache">
{{#userList}}
<tr role="row" class="user-name odd" data-id="{{id}}"><!--even -->
    <td><a href="#" class="user-edit" data-id="{{id}}">{{username}}</a></td>
    <td>{{showDeptName}}</td>
    <td>{{email}}</td>
    <td>{{telephone}}</td>
    <td>{{#bold}}{{showStatus}}{{/bold}}</td> <!-- 此处套用函数对status做特殊处理 -->
    <td>
        <div class="hidden-sm hidden-xs action-buttons">
            <a class="green user-edit" href="#" data-id="{{id}}">
                <i class="ace-icon fa fa-pencil bigger-100"></i>
            </a>
            <a class="red user-acl" href="#" data-id="{{id}}">
                <i class="ace-icon fa fa-flag bigger-100"></i>
            </a>
        </div>
    </td>
</tr>
{{/userList}}
</script>

<script type="text/javascript">
    $(function() {
        var deptList;   //树形部门列表
        var deptMap = {};   //存储部门信息的map
        var userMap = {};   //存储用户信息的map
        var optionStr = "";
        var lastClickDeptId = -1; //当前所点击的部门Id

        var deptListTemplate = $("#deptListTemplate").html();
        Mustache.parse(deptListTemplate);

        var userListTemplate = $("#userListTemplate").html();
        Mustache.parse(userListTemplate);

        loadDeptTree();

        //加载部门树
        function loadDeptTree(){
            $.ajax({
                url: "/sys/dept/tree.json",
                success: function(result){
                    if(result.ret == true){
                        deptList = result.data;
                        //渲染顶级部门
                        var rendered = Mustache.render(deptListTemplate, {deptList: result.data});
                        $("#deptList").html(rendered);
                        recursiveRenderDept(result.data);
                        bindDeptClick();
                        $(".dept-name:first").trigger("click");
                    }else {
                        showMessage("加载部门列表", result.msg, false);
                    }
                }
            });
        }

        //递归渲染部门列表树, 遍历每个部门下的子部门
        function recursiveRenderDept(deptList){
            if(deptList && deptList.length > 0){
                $(deptList).each(function(i,dept){
                    deptMap[dept.id] = dept;
                    if(dept.deptList.length > 0){
                        var render = Mustache.render(deptListTemplate, {deptList: dept.deptList});
                        $("#dept_"+dept.id).append(render);
                        recursiveRenderDept(dept.deptList);
                    }
                });
            }
        }

        //点击添加部门按钮的事件
        $(".dept-add").click(function(e){
            $("#dialog-dept-form").dialog({
                model: true,
                title: "新增部门",
                open: function(event, ui){
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "<option value='0'>-</option>";
                    recursiveRenderDeptSelect(deptList, 1);
                    $("#deptForm")[0].reset();
                    $("#deptId").val('');
                    $("#parentId").html(optionStr);
                },
                buttons: {
                    "添加": function(e){
                        //拦截默认的点击事件
                        e.preventDefault();
                        updateDept(true, function(data){
                            $("#dialog-dept-form").dialog("close");
                        },function(data){
                            showMessage("新增部门", data.msg, false);
                        });
                    },
                    "取消": function(){
                        $("#dialog-dept-form").dialog("close");
                    }
                }
            });
        });
        //绑定部门相关的事件
        function bindDeptClick() {
            //删除部门事件
            $(".dept-delete").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var deptId = $(this).attr("data-id");
                var deptName = $(this).attr("data-name");
                if(confirm("确定要删除部门【"+deptName+"】吗？")){
                    $.ajax({
                       url: "/sys/dept/delete.json",
                       data: {
                           id: deptId
                       },
                       success: function(result) {
                           if(result.ret){
                               showMessage("删除部门", "删除【"+deptName+"】部门成功", true);
                               loadDeptTree();
                           }else{
                               showMessage("删除部门", result.msg, false);
                           }
                       }
                    });
                }
            });

            //修改部门事件
            $(".dept-edit").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var deptId = $(this).attr("data-id");
                $("#dialog-dept-form").dialog({
                    model: true,
                    title: "修改部门",
                    open: function(event, ui){
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        optionStr = "<option value='0'>-</option>";
                        recursiveRenderDeptSelect(deptList, 1);
                        $("#deptForm")[0].reset();
                        $("#parentId").html(optionStr);
                        $("#deptId").val(deptId);
                        var targetDept = deptMap[deptId];
                        if(targetDept){
                            $("#parentId").val(targetDept.parentId);
                            $("#deptName").val(targetDept.name);
                            $("#deptSeq").val(targetDept.seq);
                            $("#deptRemark").val(targetDept.remark);
                        }
                    },
                    buttons: {
                        "更新": function(e){
                            //拦截默认的点击事件
                            e.preventDefault();
                            updateDept(false, function(data){
                                $("#dialog-dept-form").dialog("close");
                            },function(data){
                                showMessage("修改部门", data.msg, false);
                            });
                        },
                        "取消": function(){
                            $("#dialog-dept-form").dialog("close");
                        }
                    }
                });
            });

            //点击部门名称时的事件
            $(".dept-name").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var deptId = $(this).attr("data-id");
                //处理选中效果
                handlerDeptSelected(deptId);
            });
        }

        //点击选择部门时, 使用递归将部门列表封装
        function recursiveRenderDeptSelect(deptList, level){
            level = level | 0;
            if(deptList && deptList.length > 0){
                $(deptList).each(function(i,dept){
                    deptMap[dept.id] = dept;
                    var blank = "";
                    if (level > 1) {
                        for(var j = 3; j <= level; j++){
                            blank += "..";
                        }
                        blank += "∟";
                    }
                    optionStr += Mustache.render("<option value='{{id}}'>{{name}}</option>", {id: dept.id, name: blank + dept.name});
                    if(dept.deptList && dept.deptList.length > 0){
                        recursiveRenderDeptSelect(dept.deptList, level + 1);
                    }
                });
            }
        }

        //新增或修改部门 isCreate=true是为新增 false为修改
        function updateDept(isCreate, successCallback, failCallback) {
            $.ajax({
                url: isCreate ? "/sys/dept/save.json" : "/sys/dept/update.json",
                data: $("#deptForm").serializeArray(),
                type: "POST",
                success: function(result){
                    if(result.ret){
                        //重新加载部门树
                        loadDeptTree();
                        //执行添加成功后执行的方法
                        if(successCallback){
                            successCallback(result);
                        }
                    }else{
                        if(failCallback){
                            //执行添加失败时的方法
                            failCallback(result);
                        }
                    }
                }

            });
        }

        //处理点击部门名称时的选中效果
        function handlerDeptSelected(deptId){
            if(lastClickDeptId != -1){
                var lastDept = $("#dept_" + lastClickDeptId + " .dd2-content:first");
                lastDept.removeClass("btn-yellow");
                lastDept.removeClass("no-hover");
            }
            var currentDept = $("#dept_" + deptId + " .dd2-content:first");
            currentDept.addClass("btn-yellow");
            currentDept.addClass("no-hover");
            lastClickDeptId = deptId;
            //加载部门下的用户列表
            loadUserList(deptId);
        }

        //点击部门时加载该部门下的用户列表
        function loadUserList(deptId){
            var pageSize = $("#pageSize").val();
            var url = "/sys/user/page.json?deptId="+deptId;
            var pageNo = $("#userPage .pageNo").val() || 1;
            $.ajax({
                url: url,
                data: {
                    pageSize: pageSize,
                    pageNo: pageNo
                },
                success: function(result){
                    if(result.ret){
                        renderUserListAndPage(result, url);
                    }else{
                        showMessage("加载用户列表", result.msg, false);
                    }
                }
            });
        }

        //渲染用户列表和分页
        function renderUserListAndPage(result, url){
            if(result.ret){
                if(result.data.total > 0){
                    var rendered = Mustache.render(userListTemplate, {
                        userList: result.data.data,
                        "showDeptName": function(){
                            return deptMap[this.deptId].name;
                        },
                        "showStatus": function(){
                            return this.status == 1 ? '正常' : (this.status == 0 ? '冻结' : '删除');
                        },
                        "bold": function(){
                            return function(text, render){
                                var status = render(text);
                                if(status == '正常'){
                                    return "<span class='label label-sm label-success'>正常</span>";
                                }else if(status == '冻结'){
                                    return "<span class='label label-sm label-warning'>冻结</span>";
                                }else{
                                    return "<span class='label'>删除</span>";
                                }
                            }
                        }
                    });
                    $("#userList").html(rendered);
                    //绑定用户相关事件
                    bindUserClick();
                    $.each(result.data.data, function(i,user){
                        userMap[user.id] = user;
                    });
                }else{
                    $("#userList").html('');
                }
                var pageSize = $("#pageSize").val();
                var pageNo = $("#userPage .pageNo").val() || 1;
                var currentSize = result.data.total > 0 ? result.data.data.length : 0;
                renderPage(url, result.data.total, pageNo, pageSize, currentSize, "userPage", renderUserListAndPage);
            }
        }

        //点击添加用户按钮时的事件
        $(".user-add").click(function(){
            $("#dialog-user-form").dialog({
                model: true,
                title: "新增用户",
                open: function(event, ui){
                    $("#userForm")[0].reset();
                    $("#userId").val('');
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "";
                    recursiveRenderDeptSelect(deptList, 1);
                    $("#deptSelectId").html(optionStr);
                },
                buttons: {
                    "添加": function(e){
                        //拦截默认的点击事件
                        e.preventDefault();
                        updateUser(true, function(data){
                            $("#dialog-user-form").dialog("close");
                            loadUserList(lastClickDeptId);
                            showMessage("新增用户", "新增用户成功,密码已发送至邮箱,请注意查收", true);
                        },function(data){
                            showMessage("新增用户", data.msg, false);
                        });
                    },
                    "取消": function(){
                        $("#dialog-user-form").dialog("close");
                    }
                }
            });
        });
        //绑定用户相关的点击事件
        function bindUserClick(){
            //点击查看用户详情的事件
            $(".user-acl").click(function(e){
               e.preventDefault();
               e.stopPropagation();
               var userId = $(this).attr("data-id");
               $.ajax({
                  url: "/sys/user/acls.json",
                  data: {
                      id: userId
                  },
                  success: function(result){
                      if(result.ret){
                          console.log(result);
                      }else{
                          showMessage("加载用户权限", result.msg, false);
                      }
                  }
               });
            });

            //点击修改用户时的事件
            $(".user-edit").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var userId = $(this).attr("data-id");
                $("#dialog-user-form").dialog({
                    model: true,
                    title: "修改用户",
                    open: function(event, ui){
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        optionStr = "";
                        recursiveRenderDeptSelect(deptList, 1);
                        $("#userForm")[0].reset();
                        $("#deptSelectId").html(optionStr);
                        var targetUser = userMap[userId];
                        if(targetUser){
                            $("#userId").val(targetUser.id);
                            $("#deptSelectId").val(targetUser.deptId);
                            $("#userName").val(targetUser.username);
                            $("#userEmail").val(targetUser.email);
                            $("#userTelephone").val(targetUser.telephone);
                            $("#userStatus").val(targetUser.status);
                        }
                    },
                    buttons: {
                        "更新": function(e){
                            //拦截默认的点击事件
                            e.preventDefault();
                            updateUser(false, function(data){
                                $("#dialog-user-form").dialog("close");
                                loadUserList(lastClickDeptId);
                            },function(data){
                                showMessage("修改用户", data.msg, false);
                            });
                        },
                        "取消": function(){
                            $("#dialog-user-form").dialog("close");
                        }
                    }
                });
            });
        }

        //新增或修改用户 isCreate=true是为新增 false为修改
        function updateUser(isCreate, successCallback, failCallback) {
            $.ajax({
                url: isCreate ? "/sys/user/save.json" : "/sys/user/update.json",
                data: $("#userForm").serializeArray(),
                type: "POST",
                success: function(result){
                    if(result.ret){
                        //重新加载用户列表
                        loadUserList();
                        //执行添加成功后执行的方法
                        if(successCallback){
                            successCallback(result);
                        }
                    }else{
                        if(failCallback){
                            //执行添加失败时的方法
                            failCallback(result);
                        }
                    }
                }

            });
        }



    });

</script>
</body>
</html>
