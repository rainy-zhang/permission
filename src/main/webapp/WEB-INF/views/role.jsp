<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>角色</title>
    <jsp:include page="/common/backend_common.jsp" />
    <link rel="stylesheet" href="/ztree/zTreeStyle.css" type="text/css">
    <link rel="stylesheet" href="/assets/css/bootstrap-duallistbox.min.css" type="text/css">
    <script type="text/javascript" src="/ztree/jquery.ztree.all.min.js"></script>
    <script type="text/javascript" src="/assets/js/jquery.bootstrap-duallistbox.min.js"></script>
    <style type="text/css">
        .bootstrap-duallistbox-container .moveall, .bootstrap-duallistbox-container .removeall {
            width: 50%;
        }
        .bootstrap-duallistbox-container .move, .bootstrap-duallistbox-container .remove {
            width: 49%;
        }
    </style>
</head>
<body class="no-skin" youdao="bind" style="background: white">
<input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5"/>
<div class="page-header">
    <h1>
        角色管理
        <small>
            <i class="ace-icon fa fa-angle-double-right"></i>
            维护角色与用户, 角色与权限关系
        </small>
    </h1>
</div>
<div class="main-content-inner">
    <div class="col-sm-3">
        <div class="table-header">
            角色列表&nbsp;&nbsp;
            <a class="green" href="#">
                <i class="ace-icon fa fa-plus-circle orange bigger-130 role-add"></i>
            </a>
        </div>
        <div id="roleList"></div>
    </div>
    <div class="col-sm-9">
        <div class="tabbable" id="roleTab">
            <ul class="nav nav-tabs">
                <li class="active">
                    <a data-toggle="tab" href="#roleAclTab">
                        角色与权限
                    </a>
                </li>
                <li>
                    <a data-toggle="tab" href="#roleUserTab">
                        角色与用户
                    </a>
                </li>
            </ul>
            <div class="tab-content">
                <div id="roleAclTab" class="tab-pane fade in active">
                    <ul id="roleAclTree" class="ztree"></ul>
                    <button class="btn btn-info saveRoleAcl" type="button">
                        <i class="ace-icon fa fa-check bigger-110"></i>
                        保存
                    </button>
                </div>

                <div id="roleUserTab" class="tab-pane fade" >
                    <div class="row">
                        <div class="box1 col-md-6">待选用户列表</div>
                        <div class="box1 col-md-6">已选用户列表</div>
                    </div>
                    <select multiple="multiple" size="10" name="roleUserList" id="roleUserList" >
                    </select>
                    <div class="hr hr-16 hr-dotted"></div>
                    <button class="btn btn-info saveRoleUser" type="button">
                        <i class="ace-icon fa fa-check bigger-110"></i>
                        保存
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="dialog-role-form" style="display: none;">
    <form id="roleForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td><label for="roleName">名称</label></td>
                <td>
                    <input type="text" name="name" id="roleName" autocomplete="off" value="" class="text ui-widget-content ui-corner-all">
                    <input type="hidden" name="id" id="roleId"/>
                </td>
            </tr>
            <tr>
                <td><label for="roleStatus">状态</label></td>
                <td>
                    <select id="roleStatus" name="status" data-placeholder="状态" style="width: 150px;">
                        <option value="1">可用</option>
                        <option value="0">冻结</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label for="roleType">类型</label></td>
                <td>
                    <select id="roleType" name="type" data-placeholder="类型" style="width: 150px;">
                        <option value="1">管理员</option>
                        <option value="2">其他</option>
                    </select>
                </td>
            </tr>
            <td><label for="roleRemark">备注</label></td>
            <td><textarea name="remark" id="roleRemark" autocomplete="off" class="text ui-widget-content ui-corner-all" rows="3" cols="25"></textarea></td>
            </tr>
        </table>
    </form>
</div>
<script id="roleListTemplate" type="x-tmpl-mustache">
<ol class="dd-list">
    {{#roleList}}
        <li class="dd-item dd2-item role-name" id="role_{{id}}" href="javascript:void(0)" data-id="{{id}}">
            <div class="dd2-content" style="cursor:pointer;">
            {{name}}
            <span style="float:right;">
                <a class="green role-edit" href="#" data-id="{{id}}" >
                    <i class="ace-icon fa fa-pencil bigger-100"></i>
                </a>
                &nbsp;
                <a class="red role-delete" href="#" data-id="{{id}}" data-name="{{name}}">
                    <i class="ace-icon fa fa-trash-o bigger-100"></i>
                </a>
            </span>
            </div>
        </li>
    {{/roleList}}
</ol>
</script>

<script id="selectedUsersTemplate" type="x-tmpl-mustache">
{{#userList}}
    <option value="{{id}}" selected="selected">{{username}}</option>
{{/userList}}
</script>

<script id="unSelectedUsersTemplate" type="x-tmpl-mustache">
{{#userList}}
    <option value="{{id}}">{{username}}</option>
{{/userList}}
</script>

<script type="text/javascript">
    $(function(){
        var roleMap = {}; //用于存储角色列表
        var lastRoleId = -1; //用于记录上次点击的roleId
        var selectFirstTab = true;  //是否选择的是第一个tab
        var hashMultiSelect = false; //是否是多选

        var roleListTemplate = $("#roleListTemplate").html();
        Mustache.parse(roleListTemplate);
        var selectedUsersTemplate = $("#selectedUsersTemplate").html();
        Mustache.parse(selectedUsersTemplate);
        var unSelectedUsersTemplate = $("#unSelectedUsersTemplate").html();
        Mustache.parse(unSelectedUsersTemplate);

        // zTree
        <!-- 树结构相关 开始 -->
        var zTreeObj = [];  //用来存储权限点列表
        var modulePrefix = 'm_';    //权限模块前缀
        var aclPrefix = 'a_';   //权限点前缀
        var nodeMap = {};   //用来存储权限模块列表

        var setting = {
            check: {
                enable: true,
                chkDisabledInherit: true,
                chkboxType: {"Y": "ps", "N": "ps"}, //auto check 父节点 子节点
                autoCheckTrigger: true
            },
            data: {
                simpleData: {
                    enable: true,
                    rootPId: 0
                }
            },
            callback: {
                onClick: onClickTreeNode
            }
        };

        function onClickTreeNode(e, treeId, treeNode) { // 绑定单击事件
            var zTree = $.fn.zTree.getZTreeObj("roleAclTree");
            zTree.expandNode(treeNode);
        }
        <!-- ztree结束 -->

        loadRoleList();

        //加载角色列表
        function loadRoleList(){
            $.ajax({
                url: "/sys/role/list.json",
                success: function(result){
                    if(result.ret){
                        var rendered = Mustache.render(roleListTemplate, {roleList: result.data});
                        $("#roleList").html(rendered);
                        $.each(result.data, function(i,role){
                            roleMap[role.id] = role;
                        });
                        bindRoleClick();
                        $(".role-name:first").trigger("click");
                    }else{
                        showMessage("加载角色列表", result.msg, false);
                    }
                }
            });
        }
        //绑定角色添加事件
        $(".role-add").click(function(e){
            $("#dialog-role-form").dialog({
                model: true,
                title: "新增角色",
                open: function(event, ui){
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    $("#roleForm")[0].reset();
                    $("#roleId").val('');
                },
                buttons: {
                    "添加": function(e){
                        //拦截默认的点击事件
                        e.preventDefault();
                        updateRole(true, function(data){
                            $("#dialog-role-form").dialog("close");
                            loadRoleList();
                        },function(data){
                            showMessage("新增角色", data.msg, false);
                        });
                    },
                    "取消": function(){
                        $("#dialog-role-form").dialog("close");
                    }
                }
            });
        });

        //绑定角色点击事件
        function bindRoleClick() {
            //修改角色事件
            $(".role-edit").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var roleId = $(this).attr("data-id");
                $("#dialog-role-form").dialog({
                    model: true,
                    title: "修改角色",
                    open: function(event, ui){
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        $("#roleForm")[0].reset();
                        $("#roleId").val(roleId);
                        var targetRole = roleMap[roleId];
                        if(targetRole){
                            $("#roleType").val(targetRole.type);
                            $("#roleName").val(targetRole.name);
                            $("#roleRemark").val(targetRole.remark);
                            $("#roleStatus").val(targetRole.status);
                        }
                    },
                    buttons: {
                        "更新": function(e){
                            //拦截默认的点击事件
                            e.preventDefault();
                            updateRole(false, function(data){
                                $("#dialog-role-form").dialog("close");
                                loadRoleList();
                            },function(data){
                                showMessage("修改角色", data.msg, false);
                            });
                        },
                        "取消": function(){
                            $("#dialog-role-form").dialog("close");
                        }
                    }
                });
            });

            $(".role-name").click(function(e){
                e.preventDefault();
                e.stopPropagation();
                var roleId = $(this).attr("data-id");
                handleRoleSelected(roleId);
            });
        }
        //处理点击角色名称
        function handleRoleSelected(roleId){
            if(lastRoleId != -1){   //代表已经点击过角色名称,需要移除掉上次点击的样式,
                var lastRole = $("#role_" + lastRoleId + " .dd2-content:first");
                lastRole.removeClass("btn-yellow");
                lastRole.removeClass("no-hover");
            }
            var currentRole = $("#role_" + roleId + " .dd2-content:first");
            currentRole.addClass("btn-yellow");
            currentRole.addClass("no-hover");
            lastRoleId = roleId;

            $("#roleTab a:first").trigger("click");
            if(selectFirstTab){   //如果当前选中的是第一个tab,加载权限相关内容
                loadRoleAcl(roleId);
            }
        }

        //加载角色的权限列表
        function loadRoleAcl(selectRoleId){
            if(selectRoleId == -1){
                return;
            }
            $.ajax({
                url: "/sys/role/roleTree.json",
                data: {
                    roleId: selectRoleId
                },
                type: "POST",
                success: function(result){
                    if(result.ret){
                        renderRoleTree(result.data);
                    }else{
                        showMessage("加载角色权限", result.msg, false);
                    }
                }
            });
        }
        //获取当前ztree中选中的节点的Id
        function getTreeSelectedId() {
            var treeObj = $.fn.zTree.getZTreeObj("roleAclTree");
            var nodes = treeObj.getCheckedNodes(true);   //获取所有选中的节点,返回的是一个数组
            var v = "";
            for(var i = 0; i < nodes.length; i++){
                if(nodes[i].id+"".startsWith(aclPrefix)) {
                    if(nodes[i].dataId){
                        v += "," + nodes[i].dataId;
                    }
                }
            }
            return v.length > 0 ? v.substring(1) : v;
        }

        //保存角色与权限点的关系
        $(".saveRoleAcl").click(function(e){
            e.preventDefault();
            if(lastRoleId == -1){
                showMessage("保存角色与权限点的关系", "请选择要操作的角色", false);
                return;
            }
            $.ajax({
                url: "/sys/role/changeAcls.json",
                data: {
                    roleId: lastRoleId,
                    aclIds: getTreeSelectedId()
                },
                type: "POST",
                success: function(result){
                    if(result.ret){
                        showMessage("保存角色与权限点的关系", "操作成功", true);
                    }else{
                        showMessage("保存角色与权限点的关系", result.msg, false);
                    }
                }
            });
        });

        //渲染角色下的权限列表
        function renderRoleTree(aclModuleList){
            zTreeObj = [];
            recrusivePrepareTreeData(aclModuleList);
            for(var key in nodeMap){
                zTreeObj.push(nodeMap[key]);
            }
            //初始化ztree
            $.fn.zTree.init($("#roleAclTree"), setting, zTreeObj);
        }
        //使用递归将数据组装到Ztree中
        function recrusivePrepareTreeData(aclModuleList) {
            if(aclModuleList && aclModuleList.length > 0){
                $(aclModuleList).each(function(i, aclModule){
                    var hashChecked = false;
                    //组装权限点
                    if(aclModule.aclDtoList && aclModule.aclDtoList.length > 0){
                        $(aclModule.aclDtoList).each(function(i, acl){
                            zTreeObj.push({
                                id: acl.id,
                                pId: modulePrefix + acl.aclModuleId,
                                name: acl.name + (acl.type == 1 ? "菜单" : ""),
                                chkDisabled: !acl.hashAcl,
                                checked: acl.checked,
                                dataId: acl.id
                            });
                            if(acl.checked){
                                hashChecked = true;
                            }
                        });
                    }
                    //组装权限模块
                    nodeMap[modulePrefix + aclModule.id] = {
                        id: modulePrefix + aclModule.id,
                        pId: modulePrefix + aclModule.parentId,
                        name: aclModule.name,
                        open: hashChecked
                    };
                    if((aclModule.aclModuleDtoList && aclModule.aclModuleDtoList.length > 0) || (aclModule.aclDtoList && aclModule.aclDtoList.length > 0)){
                        //使上层权限模块的open属性也为true
                        var tempAclModule = nodeMap[modulePrefix + aclModule.id];
                        while(hashChecked && tempAclModule){
                            if(tempAclModule){
                                nodeMap[tempAclModule.id] = {
                                    id: tempAclModule.id,
                                    pId: tempAclModule.pId,
                                    name: tempAclModule.name,
                                    open: true,
                                    checked: true
                                }
                            }
                            tempAclModule = nodeMap[tempAclModule.pId];
                        }

                        recrusivePrepareTreeData(aclModule.aclModuleDtoList);
                    }
                });
            }
        }

        //新增或修改角色
        function updateRole(isCreate, successCallback, failCallback){
            $.ajax({
                url: isCreate ? "/sys/role/save.json" : "/sys/role/update.json",
                data: $("#roleForm").serializeArray(),
                type: "POST",
                success: function(result){
                    if(result.ret){
                        if(successCallback){
                            successCallback(result);
                        }
                    }else{
                        if(failCallback){
                            failCallback(result);
                        }
                    }
                }
            });
        }

        //绑定tab的点击事件
        $("#roleTab a[data-toggle='tab']").on("show.bs.tab", function(e){
            if(lastRoleId == -1){
                showMessage("加载角色关系", "请选择要操作的角色", false);
                return;
            }
            if(e.target.getAttribute("href") == "#roleAclTab"){
                selectFirstTab = true;
                loadRoleAcl(lastRoleId);
            }else {
                selectFirstTab = false;
                loadRoleUser(lastRoleId);
            }
        });
        //加载角色与用户
        function loadRoleUser(selectedRoleId){
            $.ajax({
               url: "/sys/role/users.json",
               data: {
                   roleId: selectedRoleId
               },
               type: "POST",
               success: function(result){
                   if(result.ret){
                       var renderedSelectedUserList = Mustache.render(selectedUsersTemplate, {userList: result.data.selected});
                       var renderedUnselectedUserList = Mustache.render(unSelectedUsersTemplate, {userList: result.data.unselected});
                       $("#roleUserList").html(renderedSelectedUserList + renderedUnselectedUserList);
                       if(!hashMultiSelect){
                           $("select[name='roleUserList']").bootstrapDualListbox({
                               showFilterInputs: false,
                               moveOnSelect: false,
                               infoText: false
                           });
                           hashMultiSelect = true;
                       }else{
                           $("select[name='roleUserList']").bootstrapDualListbox('refresh', true);
                           hashMultiSelect = false;
                       }
                   }else{
                       showMessage("加载用户列表", result.msg, false);
                   }
               }
            });
        }

        //保存角色与用户的关系
        $(".saveRoleUser").click(function(e){
            e.preventDefault();
            if(lastRoleId == -1){
                showMessage("保存角色与用户的关系", "请选择要操作的角色", false);
                return;
            }
            $.ajax({
                url: "/sys/role/changeUsers.json",
                data: {
                    roleId: lastRoleId,
                    userIds: $("#roleUserList").val() ? $("#roleUserList").val().join(",") : ''
                },
                type: "POST",
                success: function(result){
                    if(result.ret){
                        showMessage("保存角色与用户的关系", "操作成功", true);
                    }else{
                        showMessage("保存角色与用户的关系", result.msg, false);
                    }
                }
            });
        });

    });

</script>
</body>
</html>
