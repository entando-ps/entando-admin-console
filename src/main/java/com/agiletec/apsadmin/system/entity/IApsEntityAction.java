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
package com.agiletec.apsadmin.system.entity;

/**
 * @author E.Santoboni
 */
public interface IApsEntityAction {
	
	/**
	 * Create a new entity.
	 * @return The result code.
	 */
	public String createNew();
	
	/**
	 * View an existing entity.
	 * @return The result code.
	 */
	public String view();
	
	/**
	 * Edit an existing entity.
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Save an entity.
	 * @return The result code.
	 */
	public String save();
	
}
