/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.plugins.jacms.apsadmin.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * @author E.Santoboni
 */
public class CmsPageActionUtil {

	private static final Logger _logger = LoggerFactory.getLogger(CmsPageActionUtil.class);
	
	@Deprecated
	public static boolean isContentPublishableOnPage(Content publishingContent, IPage page) {
		return isContentPublishableOnPageOnline(publishingContent, page);
	}
	
	public static boolean isContentPublishableOnPageOnline(Content publishingContent, IPage page) {
		return isContentPublishableOnPage(publishingContent, page, page.getOnlineMetadata());
	}
	
	public static boolean isContentPublishableOnPageDraft(Content publishingContent, IPage page) {
		return isContentPublishableOnPage(publishingContent, page, page.getDraftMetadata());
	}
	
	public static boolean isContentPublishableOnPage(Content publishingContent, IPage page, PageMetadata metadata) {
		if (publishingContent.getMainGroup().equals(Group.FREE_GROUP_NAME) || publishingContent.getGroups().contains(Group.FREE_GROUP_NAME)) {
			return true;
		}
		//tutti i gruppi posseduti dalla pagina devono essere contemplati nel contenuto.
		List<String> pageGroups = new ArrayList<String>();
		pageGroups.add(page.getGroup());
		if (metadata != null && null != metadata.getExtraGroups()) {
			pageGroups.addAll(metadata.getExtraGroups());
		}
		List<String> contentGroups = getContentGroups(publishingContent);
		for (int i = 0; i < pageGroups.size(); i++) {
			String pageGroup = pageGroups.get(i);
			if (!pageGroup.equals(Group.ADMINS_GROUP_NAME) && !contentGroups.contains(pageGroup)) return false;
		}
		return true;
	}
	
	@Deprecated
	public static boolean isPageLinkableByContent(IPage page, Content content) {
		return isPageLinkableByContent(page, page.getOnlineMetadata(), content);
	}
	
	public static boolean isPageLinkableByContentOnline(IPage page, Content content) {
		return isPageLinkableByContent(page, page.getOnlineMetadata(), content);
	}
	
	public static boolean isPageLinkableByContentDraft(IPage page, Content content) {
		return isPageLinkableByContent(page, page.getDraftMetadata(), content);
	}
	
	public static boolean isPageLinkableByContent(IPage page, PageMetadata metadata, Content content) {
		Collection<String> extraPageGroups = metadata.getExtraGroups();
		if (page.getGroup().equals(Group.FREE_GROUP_NAME) 
				|| (null != extraPageGroups && extraPageGroups.contains(Group.FREE_GROUP_NAME))) {
			return true;
		}
		if (content.getMainGroup().equals(Group.ADMINS_GROUP_NAME)) return true;
		List<String> contentGroups = getContentGroups(content);
		for (int i = 0; i < contentGroups.size(); i++) {
			String contentGroup = contentGroups.get(i);
			if (contentGroup.equals(page.getGroup())) return true;
		}
		return false;
	}
	
	private static List<String> getContentGroups(Content content) {
		List<String> contentGroups = new ArrayList<String>();
		contentGroups.add(content.getMainGroup());
		if (null != content.getGroups()) {
			contentGroups.addAll(content.getGroups());
		}
		return contentGroups;
	}
	
	/**
	 * Check whether the page can publish free content.
	 * @param page The page to check.
	 * @param viewerWidgetCode The code of the viewer widget (optional)
	 * @return True if the page can publish free content, false else.
	 */
	public static boolean isFreeViewerPage(IPage page, String viewerWidgetCode) {
		return isOnlineFreeViewerPage(page, viewerWidgetCode);
	}
	
	public static boolean isDraftFreeViewerPage(IPage page, String viewerWidgetCode) {
		boolean found = false;
		PageMetadata metadata = page.getDraftMetadata();
		Widget[] widgets = page.getDraftWidgets();
		if (metadata != null) {
			found = isFreeViewerPage(metadata.getModel(), widgets, viewerWidgetCode);
		}
		return found;
	}
	
	public static boolean isOnlineFreeViewerPage(IPage page, String viewerWidgetCode) {
		boolean found = false;
		PageMetadata metadata = page.getOnlineMetadata();
		Widget[] widgets = page.getOnlineWidgets();
		if (metadata != null) {
			found = isFreeViewerPage(metadata.getModel(), widgets, viewerWidgetCode);
		}
		return found;
	}
	
	public static boolean isFreeViewerPage(PageModel model, Widget[] widgets, String viewerWidgetCode) {
		try {
			if (model != null && widgets != null) {
				int mainFrame = model.getMainFrame();
				if (mainFrame < 0) return false;
				Widget viewer = widgets[mainFrame];
				if (null == viewer) return false;
				boolean isRightCode = null == viewerWidgetCode || viewer.getType().getCode().equals(viewerWidgetCode);
				String actionName = viewer.getType().getAction();
				boolean isRightAction = null != actionName && actionName.toLowerCase().indexOf("viewer") >= 0;
				List<WidgetTypeParameter> typeParameters = viewer.getType().getTypeParameters();
				if ((isRightCode || isRightAction )  
						&& (null != typeParameters && !typeParameters.isEmpty())
						&& (null == viewer.getConfig() || viewer.getConfig().isEmpty())) {
					return true;
				}
			}
		} catch (Throwable t) {
			_logger.error("Error while checking page for widget '{}'", viewerWidgetCode, t);
			//ApsSystemUtils.logThrowable(t, CmsPageActionUtil.class, "isViewerPage", "Error while checking page '" + page.getCode() + "'");
		}
		return false;
	}
	
}