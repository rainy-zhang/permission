<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限</title>
    <jsp:include page="/common/backend_common.jsp"/>
    <jsp:include page="/common/page.jsp"/>
</head>
<body class="no-skin" youdao="bind" style="background: white">
<input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5"/>

<div class="page-header">
    <h1>
        权限模块管理
        <small>
            <i class="ace-icon fa fa-angle-double-right"></i>
            维护权限模块和权限点关系
        </small>
    </h1>
</div>
<div class="main-content-inner">
    <div class="col-sm-3">
        <div class="table-header">
            权限模块列表&nbsp;&nbsp;
            <a class="green" href="#">
                <i class="ace-icon fa fa-plus-circle orange bigger-130 aclModule-add"></i>
            </a>
        </div>
        <div id="aclModuleList">
        </div>
    </div>
    <div class="col-sm-9">
        <div class="col-xs-12">
            <div class="table-header">
                权限点列表&nbsp;&nbsp;
                <a class="green" href="#">
                    <i class="ace-icon fa fa-plus-circle orange bigger-130 acl-add"></i>
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
                    <table id="dynamic-table" class="table table-striped table-bordered table-hover dataTable no-footer" role="grid"
                           aria-describedby="dynamic-table_info" style="font-size:14px">
                        <thead>
                        <tr role="row">
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                权限名称
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                权限模块
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                类型
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                URL
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                状态
                            </th>
                            <th tabindex="0" aria-controls="dynamic-table" rowspan="1" colspan="1">
                                顺序
                            </th>
                            <th class="sorting_disabled" rowspan="1" colspan="1" aria-label=""></th>
                        </tr>
                        </thead>
                        <tbody id="aclList"></tbody>
                    </table>
                    <div class="row" id="aclPage"> <%-- 分页信息 --%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="dialog-aclModule-form" style="display: none;">
    <form id="aclModuleForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td style="width: 80px;"><label for="parentId">上级模块</label></td>
                <td>
                    <select id="parentId" name="parentId" data-placeholder="选择模块" style="width: 200px;"></select>
                    <input type="hidden" name="id" id="aclModuleId"/>
                </td>
            </tr>
            <tr>
                <td><label for="aclModuleName">名称</label></td>
                <td><input type="text" name="name" autocomplete="off" id="aclModuleName" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="aclModuleSeq">顺序</label></td>
                <td><input type="number" name="seq" id="aclModuleSeq" autocomplete="off" value="1" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="aclModuleStatus">状态</label></td>
                <td>
                    <select id="aclModuleStatus" name="status" data-placeholder="选择状态" style="width: 150px;">
                        <option value="1">正常</option>
                        <option value="0">冻结</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label for="aclModuleRemark">备注</label></td>
                <td><textarea name="remark" id="aclModuleRemark" style="resize: none;" class="text ui-widget-content ui-corner-all" rows="3" cols="25"></textarea></td>
            </tr>
        </table>
    </form>
</div>
<div id="dialog-acl-form" style="display: none;">
    <form id="aclForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td style="width: 80px;"><label for="parentId">所属权限模块</label></td>
                <td>
                    <select id="aclModuleSelectId" name="aclModuleId" data-placeholder="选择权限模块" style="width: 200px;"></select>
                </td>
            </tr>
            <tr>
                <td><label for="aclName">名称</label></td>
                <input type="hidden" name="id" id="aclId"/>
                <td><input type="text" name="name" autocomplete="off" id="aclName" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="aclType">类型</label></td>
                <td>
                    <select id="aclType" name="type" data-placeholder="类型" style="width: 150px;">
                        <option value="1">菜单</option>
                        <option value="2">按钮</option>
                        <option value="3">其他</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label for="aclUrl">URL</label></td>
                <td><input type="text" name="url" id="aclUrl" value="" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="aclStatus">状态</label></td>
                <td>
                    <select id="aclStatus" name="status" data-placeholder="选择状态" style="width: 150px;">
                        <option value="1">正常</option>
                        <option value="0">冻结</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label for="aclSeq">顺序</label></td>
                <td><input type="number" name="seq" id="aclSeq" value="1" class="text ui-widget-content ui-corner-all"></td>
            </tr>
            <tr>
                <td><label for="aclRemark">备注</label></td>
                <td><textarea name="remark" id="aclRemark" class="text ui-widget-content ui-corner-all" rows="3" cols="25"></textarea></td>
            </tr>
        </table>
    </form>
</div>

<script id="aclModuleListTemplate" type="x-tmpl-mustache">
<ol class="dd-list ">
    {{#aclModuleList}}
        <li class="dd-item dd2-item aclModule-name {{displayClass}}" id="aclModule_{{id}}" href="javascript:void(0)" data-id="{{id}}">
            <div class="dd2-content" style="cursor:pointer;">
            {{name}}
            &nbsp;
            <a class="green {{#showDownAngle}}{{/showDownAngle}}" href="#" data-id="{{id}}" >
                <i class="ace-icon fa fa-angle-double-down bigger-120 sub-aclModule"></i>
            </a>
            <span style="float:right;">
                <a class="green aclModule-edit" href="#" data-id="{{id}}" >
                    <i class="ace-icon fa fa-pencil bigger-100"></i>
                </a>
                &nbsp;
                <a class="red aclModule-delete" href="#" data-id="{{id}}" data-name="{{name}}">
                    <i class="ace-icon fa fa-trash-o bigger-100"></i>
                </a>
            </span>
            </div>
        </li>
    {{/aclModuleList}}
</ol>
</script>

<script id="aclListTemplate" type="x-tmpl-mustache">
{{#aclList}}
<tr role="row" class="acl-name odd" data-id="{{id}}"><!--even -->
    <td><a href="#" class="acl-edit" data-id="{{id}}">{{name}}</a></td>
    <td>{{showAclModuleName}}</td>
    <td>{{showType}}</td>
    <td>{{url}}</td>
    <td>{{#bold}}{{showStatus}}{{/bold}}</td>
    <td>{{seq}}</td>
    <td>
        <div class="hidden-sm hidden-xs action-buttons">
            <a class="green acl-edit" href="#" data-id="{{id}}">
                <i class="ace-icon fa fa-pencil bigger-100"></i>
            </a>
            <a class="red acl-role" href="#" data-id="{{id}}">
                <i class="ace-icon fa fa-flag bigger-100"></i>
            </a>
        </div>
    </td>
</tr>
{{/aclList}}
</script>

<script type="text/javascript">
    $(function(){
        var aclModuleList;   //树形权限模块列表
        var aclModuleMap = {};   //存储权限模块信息的map
        var aclMap = {};   //存储权限信息的map
        var optionStr = "";
        var lastClickAclModuleId = -1;  //当前点击的权限模块Id

        var aclModuleListTemplate = $("#aclModuleListTemplate").html();
        Mustache.parse(aclModuleListTemplate);

        var aclListTemplate = $("#aclListTemplate").html();
        Mustache.parse(aclListTemplate);

        loadAclModuleTree();

        //加载权限模块树
        function loadAclModuleTree(){
            $.ajax({
                url: "/sys/aclModule/tree.json",
                success: function(result){
                    if(result.ret){
                        aclModuleList = result.data;
                        var rendered = Mustache.render(aclModuleListTemplate, {
                            aclModuleList: result.data,
                            "showDownAngle": function() {
                                return function(text, render){
                                    return (this.aclModuleDtoList && this.aclModuleDtoList.length > 0) ? "" : "hidden";
                                }
                            },
                            "displayClass": function(){
                                return "";
                            }
                        });
                        $("#aclModuleList").html(rendered);
                        recursiveRenderAclModule(result.data);
                        bindAclModuleClick();
                        $(".aclModule-name:first").trigger("click");
                    }else{
                        showMessage("加载权限模块", result.msg, false);
                    }
                }
            });
        }

        //使用递归将权限模块封装成树形结构
        function recursiveRenderAclModule(aclModuleList) {
            if(aclModuleList && aclModuleList.length > 0){
                $.each(aclModuleList, function(i, aclModule){
                    aclModuleMap[aclModule.id] = aclModule;
                    if(aclModule.aclModuleDtoList && aclModule.aclModuleDtoList.length > 0){
                        var rendered = Mustache.render(aclModuleListTemplate, {
                            aclModuleList: aclModule.aclModuleDtoList,
                            "showDownAngle": function() {
                                return function(text, render){
                                    return (this.aclModuleDtoList && this.aclModuleDtoList.length > 0) ? "" : "hidden";
                                }
                            },
                            "displayClass": function(){
                                return "hidden";
                            }
                        });
                        $("#aclModule_" + aclModule.id).append(rendered);
                        recursiveRenderAclModule(aclModule.aclModuleDtoList);
                    }
                });
            }
        }


        //添加事件
        $(".aclModule-add").click(function(){
            $("#dialog-aclModule-form").dialog({
                model: true,
                title: "新增模块",
                open: function(event, ui){
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "<option value='0'>-</option>";
                    recursiveRenderAclModuleSelect(aclModuleList, 1);
                    $("#aclModuleForm")[0].reset();
                    $("#aclModuleId").val('');
                    $("#parentId").html(optionStr);
                },
                buttons: {
                    "添加": function(e){
                        //拦截默认的点击事件
                        e.preventDefault();
                        updateAclModule(true, function(data){
                            $("#dialog-aclModule-form").dialog("close");
                        },function(data){
                            showMessage("新增模块", data.msg, false);
                        });
                    },
                    "取消": function(){
                        $("#dialog-aclModule-form").dialog("close");
                    }
                }
            });
        });
        //绑定相关的点击操作
        function bindAclModuleClick(){
            //点击上级部门展示下级部门列表
            $(".sub-aclModule").click(function(e){
               e.preventDefault();
               e.stopPropagation();
               $(this).parent().parent().parent().children().children(".aclModule-name").toggleClass("hidden");
               if($(this).is(".fa-angle-double-down")){
                   $(this).removeClass("fa-angle-double-down").addClass("fa-angle-double-up");
               }else{
                   $(this).removeClass("fa-angle-double-up").addClass("fa-angle-double-down");
               }
            });

            $(".aclModule-delete").click(function(e){
               e.preventDefault();
               e.stopPropagation();
               var aclModuleId = $(this).attr("data-id");
               var aclModuleName = $(this).attr("data-name");
               if(confirm("确定要删除【"+aclModuleName+"】吗？")){
                   $.ajax({
                      url: "/sys/aclModule/delete.json",
                      data: {
                          id: aclModuleId
                      },
                      success: function(result){
                          if(result.ret){
                              showMessage("删除权限模块", "删除权限模块【"+aclModuleName+"】成功!", true);
                              loadAclModuleTree();
                          }else{
                              showMessage("删除权限模块", result.msg, false);
                          }
                      }
                   });
               }
            });

            //修改事件
            $(".aclModule-edit").click(function(e){
                e.preventDefault();
                e.stopPropagation();
                var aclModuleId = $(this).attr("data-id");
                $("#dialog-aclModule-form").dialog({
                    model: true,
                    title: "修改权限模块",
                    open: function(event, ui){
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        optionStr = "<option value='0'>-</option>";
                        recursiveRenderAclModuleSelect(aclModuleList, 1);
                        $("#aclModuleForm")[0].reset();
                        $("#parentId").html(optionStr);
                        var targetAclModule = aclModuleMap[aclModuleId];
                        if(targetAclModule){
                            $("#aclModuleId").val(aclModuleId);
                            $("#parentId").val(targetAclModule.parentId);
                            $("#aclModuleName").val(targetAclModule.name);
                            $("#aclModuleSeq").val(targetAclModule.seq);
                            $("#aclModuleRemark").val(targetAclModule.remark);
                            $("#aclModuleStatus").val(targetAclModule.status);
                        }
                    },
                    buttons: {
                        "更新": function(e){
                            //拦截默认的点击事件
                            e.preventDefault();
                            updateAclModule(false, function(data){
                                $("#dialog-aclModule-form").dialog("close");
                            },function(data){
                                showMessage("修改权限模块", data.msg, false);
                            });
                        },
                        "取消": function(){
                            $("#dialog-aclModule-form").dialog("close");
                        }
                    }
                });
            });

            //点击权限模块名称事件
            $(".aclModule-name").click(function(e){
               e.preventDefault();
               e.stopPropagation();
               var aclModuleId = $(this).attr("data-id");
                handleAclModuleSelect(aclModuleId);
            });
        }

        //处理点击权限模块
        function handleAclModuleSelect(aclModuleId) {
            if(lastClickAclModuleId != -1){
                var lastAclModule = $("#aclModule_" + lastClickAclModuleId + " .dd2-content:first");
                lastAclModule.removeClass("btn-yellow");
                lastAclModule.removeClass("no-hover");
            }
            var currentAclModule = $("#aclModule_" + aclModuleId + " .dd2-content:first");
            currentAclModule.addClass("btn-yellow");
            currentAclModule.addClass("no-hover");
            lastClickAclModuleId = aclModuleId;
            //加载权限模块下的权限列表
            loadAclList(aclModuleId);
        }

        //根据权限模块Id加载权限模块下的权限列表
        function loadAclList(aclModuleId){
            var url = "/sys/acl/page.json?aclModuleId=" + aclModuleId;
            var pageSize = $("#pageSize").val();
            var pageNo = $("#aclPage .pageNo").val();
            $.ajax({
               url: url,
               data: {
                   pageSize: pageSize,
                   pageNo: pageNo
               },
               type: "POST",
               success: function(result){
                   if(result.ret){
                       renderAclListAndPage(result, url);
                   }else{
                       showMessage("加载权限列表", result.msg, false);
                   }
               }
            });
        }

        //渲染权限分页列表
        function renderAclListAndPage(result, url){
            if(result.data.total > 0){
                var rendered = Mustache.render(aclListTemplate, {
                    aclList: result.data.data,
                    "showAclModuleName": function() {
                        return aclModuleMap[this.aclModuleId].name;
                    },
                    "showType": function() {
                        return this.type == 1 ? "菜单" : (this.type == 2 ? "按钮" : "其他");
                    },
                    "showStatus": function(){
                        return this.status == 1 ? "正常" : "冻结";
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
                $("#aclList").html(rendered);
                bindAclClick();
                $.each(result.data.data, function(i,acl){
                   aclMap[acl.id] = acl;
                });
            }else{
                $("#aclList").html("");
            }
            var pageSize = $("#pageSize").val();
            var pageNo = $("#aclPage .pageNo").val() || 1;
            var currentSize = result.data.total > 0 ? result.data.data.length : 0;
            renderPage(url, result.data.total, pageNo, pageSize, currentSize, "aclPage", renderAclListAndPage);
        }

        //点击添加按钮时的事件
        $(".acl-add").click(function(){
            $("#dialog-acl-form").dialog({
                model: true,
                title: "新增权限",
                open: function(event, ui){
                    $("#aclForm")[0].reset();
                    $("#aclId").val('');
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "";
                    recursiveRenderAclModuleSelect(aclModuleList, 1);
                    $("#aclModuleSelectId").html(optionStr);
                },
                buttons: {
                    "添加": function(e){
                        //拦截默认的点击事件
                        e.preventDefault();
                        updateAcl(true, function(data){
                            $("#dialog-acl-form").dialog("close");
                            loadAclList(lastClickAclModuleId);
                        },function(data){
                            showMessage("新增权限", data.msg, false);
                        });
                    },
                    "取消": function(){
                        $("#dialog-acl-form").dialog("close");
                    }
                }
            });
        });
        //绑定权限点相关点击事件
        function bindAclClick(){
            //点击查看权限点详情的点击事件
            $(".acl-role").click(function(e){
               e.preventDefault();
               e.stopPropagation();
               var aclId = $(this).attr("data-id");
               $.ajax({
                  url: "/sys/acl/acls.json",
                  data: {
                      id: aclId
                  },
                  success: function(result){
                      if(result.ret){
                          console.log(result);
                      }else{
                          showMessage("获取角色列表", result.msg, false);
                      }
                  }
               });
            });

            //点击修改时的事件
            $(".acl-edit").click(function(e){
                //拦截默认的点击事件
                e.preventDefault();
                //阻止冒泡事件
                e.stopPropagation();
                var aclId = $(this).attr("data-id");
                $("#dialog-acl-form").dialog({
                    model: true,
                    title: "修改权限",
                    open: function(event, ui){
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        optionStr = "";
                        recursiveRenderAclModuleSelect(aclModuleList, 1);
                        $("#aclForm")[0].reset();
                        $("#aclModuleSelectId").html(optionStr);
                        var targetAcl = aclMap[aclId];
                        if(targetAcl){
                            $("#aclId").val(targetAcl.id);
                            $("#aclModuleSelectId").val(targetAcl.aclModuleId);
                            $("#aclName").val(targetAcl.name);
                            $("#aclType").val(targetAcl.type);
                            $("#aclStatus").val(targetAcl.status);
                            $("#aclSeq").val(targetAcl.seq);
                            $("#aclUrl").val(targetAcl.url);
                            $("#aclRemark").val(targetAcl.remark);
                        }
                    },
                    buttons: {
                        "更新": function(e){
                            //拦截默认的点击事件
                            e.preventDefault();
                            updateAcl(false, function(data){
                                $("#dialog-acl-form").dialog("close");
                                loadAclList(lastClickAclModuleId);
                            },function(data){
                                showMessage("修改用户", data.msg, false);
                            });
                        },
                        "取消": function(){
                            $("#dialog-acl-form").dialog("close");
                        }
                    }
                });
            });
        }

        //新增或修改acl
        function updateAcl(isCreate, successCallback, failCallback){
            $.ajax({
               url: isCreate ? "/sys/acl/save.json" : "/sys/acl/update.json",
               data: $("#aclForm").serializeArray(),
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

        //点击选择模块时, 使用递归将部门列表封装
        function recursiveRenderAclModuleSelect(aclModuleList, level){
            level = level | 0;
            if(aclModuleList && aclModuleList.length > 0){
                $(aclModuleList).each(function(i,aclModule){
                    aclModuleMap[aclModule.id] = aclModule;
                    var blank = "";
                    if (level > 1) {
                        for(var j = 3; j <= level; j++){
                            blank += "..";
                        }
                        blank += "∟";
                    }
                    optionStr += Mustache.render("<option value='{{id}}'>{{name}}</option>", {id: aclModule.id, name: blank + aclModule.name});
                    if(aclModule.aclModuleDtoList && aclModule.aclModuleDtoList.length > 0){
                        recursiveRenderAclModuleSelect(aclModule.aclModuleDtoList, level + 1);
                    }
                });
            }
        }

        //新增或修改权限模块
        function updateAclModule(isCreate, successCallback, failCallback){
            $.ajax({
               url: isCreate ? "/sys/aclModule/save.json" : "/sys/aclModule/update.json",
               data: $("#aclModuleForm").serializeArray(),
               success: function(result){
                   if(result.ret){
                       loadAclModuleTree(); //执行成功后重新加载权限模块树
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

    });
</script>

</body>
</html>
