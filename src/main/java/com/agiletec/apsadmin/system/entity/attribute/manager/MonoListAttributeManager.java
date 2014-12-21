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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;

/**
 * Manager class for the 'Monolist' Attributes
 * @author E.Santoboni
 */
public class MonoListAttributeManager extends AbstractAttributeManager {

	@Override
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        List<AttributeInterface> attributes = ((MonoListAttribute) attribute).getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInterface attributeElement = attributes.get(i);
            AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
            elementTracer.setMonoListElement(true);
            elementTracer.setListIndex(i);
            AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement);
            if (elementManager != null) {
                elementManager.updateAttribute(attributeElement, elementTracer, request);
            }
        }
    }

}