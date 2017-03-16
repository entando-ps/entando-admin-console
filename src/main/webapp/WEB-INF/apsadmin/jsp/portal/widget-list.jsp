<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="/apsadmin-form" %>
<%@ taglib prefix="wp" uri="/aps-core" %>
<%@ taglib uri="/apsadmin-core" prefix="wpsa" %>

<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/entando-widget-icons.css"/>

<ol class="breadcrumb page-tabs-header breadcrumb-position">
    <li><s:text name="title.uxPatterns" /></li>
    <li class="page-title-container"><s:text name="title.widgetManagement" /></li>
</ol>

<h1 class="page-title-container">
    <span class="page-title-big"><s:text name="title.widgetManagement" /></span>
</h1>
    
<div id="main" role="main">

    <s:if test="hasFieldErrors()">
        <div class="alert alert-danger alert-dismissable">
            <button class="close" data-dismiss="alert"><span class="icon fa fa-times"></span></button>
            <h2 class="h4 margin-none"><s:text name="message.title.ActionErrors" /></h2>
            <ul class="margin-base-vertical">
                <s:iterator value="actionErrors">
                    <li><s:property escape="false" /></li>
                    </s:iterator>
                    <s:iterator value="fieldErrors">
                        <s:iterator value="value">
                        <li><s:property escape="false" /></li>
                        </s:iterator>
                    </s:iterator>
            </ul>
        </div>
    </s:if>
    <div class="row">
        <div class="form-group col-md-12">
            <a class="btn btn-primary pull-right" href="<s:url namespace="/do/Portal/WidgetType" action="newWidget" />">
                <s:text name="label.add" />&#32;<s:text name="label.widget" />
            </a>
        </div>
    </div>       
    <s:set var="pluginTitleCheck" value="'false'" />
    <s:set var="showletFlavours" value="showletFlavours" />
    <s:set var="showletTypeApiMappingsVar" value="showletTypeApiMappings" />

    <!--HEADER Start -->
    <div class="list-group list-view-pf">
        <div class="list-group-item table-header-custom">
            <div class="list-view-pf-checkbox">
                <span class="badge bold" title="<s:text name="title.widgetManagement.howmanypages.long" />">N</span>&#32;
            </div>
            <div class="list-view-pf-actions bold">
                Actions
            </div>
            <div class="list-view-pf-main-info">
                <div class="list-view-pf-left">
                    <span class="fa fa-cubes list-view-pf-icon-sm"></span>
                </div>
                <div class="list-view-pf-body">
                    <div class="list-view-pf-description">
                        <div class="list-group-item-heading bold">
                            Widget Name
                        </div>
                        <div class="list-group-item-text bold">
                            WIdget Group
                        </div>
                    </div>
                    <div class="list-view-pf-additional-info bold">
                        Widget Code
                    </div>
                </div>
            </div>
        </div>
        <!--HEADER End-->

        <s:iterator var="showletFlavour" value="#showletFlavours">
            <s:set var="firstType" value="%{#showletFlavour.get(0)}"></s:set>
            <s:iterator var="showletType" value="#showletFlavour" >
                <s:set var="showletUtilizers" value="getShowletUtilizers(#showletType.key)" ></s:set>
                <s:set var="concreteShowletTypeVar" value="%{getShowletType(#showletType.key)}"></s:set>

                    <!-- list item start-->
                    <div class="list-group-item">
                        <div class="list-view-pf-checkbox">
                            <span class="badge" title="<s:text name="title.widgetManagement.howmanypages.long" />: <s:property value="#showletType.value" />"><s:property value="#showletUtilizers.size()" /></span>&#32;
                    </div>
                    <div class="list-view-pf-actions">
                        <s:if test="#showletUtilizers != null && #showletUtilizers.size() > 0">
                            <a href="<s:url namespace="/do/Portal/WidgetType" action="viewWidgetUtilizers"><s:param name="widgetTypeCode" value="#showletType.key" /></s:url>" title="<s:text name="title.widgetManagement.howmanypages.goToSee" />: <s:property value="#showletType.value" />" class="btn btn-default"><span class="icon fa fa-info"></span></a>
                            </s:if>
                            <wp:ifauthorized permission="superuser">
                                <s:if test="#concreteShowletTypeVar.isLogic()">
                                    <s:set var="relatedApiMethodVar" value="#showletTypeApiMappingsVar[#concreteShowletTypeVar.parentType.code]" />
                                </s:if>
                                <s:elseif test="null != #concreteShowletTypeVar.typeParameters && #concreteShowletTypeVar.typeParameters.size() > 0">
                                    <s:set var="relatedApiMethodVar" value="#showletTypeApiMappingsVar[#concreteShowletTypeVar.code]" />
                                </s:elseif>
                                <s:if test="null != #relatedApiMethodVar">
                                    <s:if test="#concreteShowletTypeVar.isLogic()">
                                        <s:url action="newService" namespace="/do/Api/Service" var="newServiceUrlVar">
                                            <s:param name="resourceName" value="#relatedApiMethodVar.resourceName" />
                                            <s:param name="namespace" value="#relatedApiMethodVar.namespace" />
                                            <s:param name="widgetTypeCode" value="#concreteShowletTypeVar.code" />
                                        </s:url>
                                    </s:if>
                                    <s:else>
                                        <s:url action="newService" namespace="/do/Api/Service" var="newServiceUrlVar">
                                            <s:param name="resourceName" value="#relatedApiMethodVar.resourceName" />
                                            <s:param name="namespace" value="#relatedApiMethodVar.namespace" />
                                        </s:url>
                                    </s:else>
                                <a href="<s:property value="#newServiceUrlVar" escape="false" />" title="<s:text name="note.api.apiMethodList.createServiceFromMethod" />: <s:property value="#relatedApiMethodVar.methodName" />" class="btn btn-default"><span class="icon fa fa-code-fork"></span></a>
                                    <s:set var="newServiceUrlVar" value="null" />
                                </s:if>
                                <s:set var="relatedApiMethodVar" value="null" />

                            <s:if test="null != #concreteShowletTypeVar.typeParameters && #concreteShowletTypeVar.typeParameters.size() > 0">
                                <a href="<s:url namespace="/do/Portal/WidgetType" action="newUserWidget"><s:param name="parentShowletTypeCode" value="#showletType.key" /></s:url>" title="<s:text name="label.userWidget.new.from" />: <s:property value="#showletType.value" />" class="btn btn-default"><span class="icon fa fa-puzzle-piece"></span></a>
                                </s:if>
                                <s:if test="#firstType.optgroup == 'userShowletCode' && !#concreteShowletTypeVar.isLocked() && (#showletUtilizers == null || #showletUtilizers.size() == 0)">
                                <a href="<s:url namespace="/do/Portal/WidgetType" action="trash"><s:param name="widgetTypeCode" value="#showletType.key" /></s:url>" title="<s:text name="label.remove" />: <s:property value="#showletType.value" />" class="btn btn-warning" ><span class="icon fa fa-times-circle-o"></span></a>
                                </s:if>
                            </wp:ifauthorized>
                            <wpsa:hookPoint key="core.showletType.list.table.td" objectName="hookPointElements_core_showletType_list_table_td">
                                <s:iterator value="#hookPointElements_core_showletType_list_table_td" var="hookPointElement">
                                    <wpsa:include value="%{#hookPointElement.filePath}"></wpsa:include>
                                </s:iterator>
                            </wpsa:hookPoint>
                    </div>
                    <div class="list-view-pf-main-info">
                        <div class="list-view-pf-left">
                            <span class="fa fa-default list-view-pf-icon-sm widget-icon fa-<s:property value="#showletType.key" />"></span>
                        </div>
                        <div class="list-view-pf-body">
                            <div class="list-view-pf-description">
                                <div class="list-group-item-heading">
                                    <a href="<s:url namespace="/do/Portal/WidgetType" action="edit"><s:param name="widgetTypeCode" value="#showletType.key" /></s:url>" title="<s:text name="label.configWidget" />: <s:property value="#showletType.value" />" ><span class="icon fa fa-cog"></span>
                                        <s:property value="#showletType.value" /></a>
                                    <s:if test="%{#concreteShowletTypeVar.mainGroup != null && !#concreteShowletTypeVar.mainGroup.equals('free')}"><span class="text-muted icon fa fa-lock"></span></s:if>
                                    </div>
                                    <div class="list-group-item-text">
                                    <s:if test="%{#firstType.optgroup == 'stockShowletCode'}">
                                        <s:text name="title.widgetManagement.widgets.stock" />
                                    </s:if>
                                    <s:elseif test="%{#firstType.optgroup == 'customShowletCode'}">
                                        <s:text name="title.widgetManagement.widgets.custom" />
                                    </s:elseif>
                                    <s:elseif test="%{#firstType.optgroup == 'userShowletCode'}">
                                        <s:text name="title.widgetManagement.widgets.user" />
                                    </s:elseif>
                                    <s:else>
                                        <s:if test="#pluginTitleCheck.equals('false')">
                                            <span class="sr-only"><s:text name="title.widgetManagement.widgets.plugin" /></span>&#32;
                                        </s:if>
                                        <s:set var="pluginTitleCheck" value="'true'" ></s:set>
                                        <wpsa:set var="pluginPropertyName" value="%{getText(#firstType.optgroup + '.name')}" />
                                        <wpsa:set var="pluginPropertyCode" value="%{getText(#firstType.optgroup + '.code')}" />
                                        <s:text name="#pluginPropertyName" />
                                    </s:else> 
                                </div>
                            </div>
                            <div class="list-view-pf-additional-info">
                                <s:property value="#showletType.key" />
                            </div>
                        </div>
                    </div>
                </div>
                <!-- list item end-->


            </s:iterator>
            <s:set var="showletUtilizers"></s:set>
                </ul>
        </s:iterator>

    </div>

</div>
