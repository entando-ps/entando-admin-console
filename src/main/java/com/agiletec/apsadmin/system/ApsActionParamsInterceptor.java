/*
 * Copyright 2013-Present Entando Corporation (http://www.entando.com) All rights reserved.
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
package com.agiletec.apsadmin.system;

import com.agiletec.apsadmin.util.ApsRequestParamsUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

/**
 * Interceptor per la gestione degli "Aps Action Params", 
 * parametri inseriti nei nomi delle action invocate.
 * L'interceptor intercetta i parametri il cui nome è strutturato secondo la sintassi:<br />
 * &#60;ACTION_NAME&#62;?&#60;PARAM_NAME_1&#62;=&#60;PARAM_VALUE_1&#62;;&#60;PARAM_NAME_2&#62;=&#60;PARAM_VALUE_2&#62;;....;&#60;PARAM_NAME_N&#62;=&#60;PARAM_VALUE_N&#62;
 * <br />
 * L'interceptor effettua il parsing della stringa inserendo i parametri estratti nella richiesta corrente 
 * in maniera tale che vengano intercettati dal successivo "Parameters Interceptor" di default del sistema.
 * @author E.Santoboni
 */
public class ApsActionParamsInterceptor extends AbstractInterceptor {
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String entandoActionName = ApsRequestParamsUtil.extractEntandoActionName(request);
		if (null != entandoActionName) {
			this.createApsActionParam(entandoActionName, invocation);
		}
		return invocation.invoke();
	}
	
	private void createApsActionParam(String entandoActionName, ActionInvocation invocation) {
		Map parameters = invocation.getInvocationContext().getParameters();
		HttpServletRequest request = ServletActionContext.getRequest();
		Properties properties = ApsRequestParamsUtil.extractApsActionParameters(entandoActionName);
		Iterator<Object> iter = properties.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next().toString();
			Object value = properties.getProperty(key);
			request.setAttribute(key, value);
			parameters.put(key, value);
		}
	}
	
}
