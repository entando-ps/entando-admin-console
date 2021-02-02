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
package org.entando.entando.apsadmin.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.opensymphony.xwork2.Action;
import org.junit.jupiter.api.Test;

/**
 * @author f.deidda
 */
class TestUserProfileFinderAction extends ApsAdminBaseTestCase {
	
    @Test
	public void testSearchSuperUser() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "list");
            String result = this.executeAction();
            assertEquals(Action.SUCCESS, result);
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testSearchGenericUser() throws Throwable {
        try {
            this.setUserOnSession("editor");
            this.initAction("/do/User", "list");
            String result = this.executeAction();
            assertEquals("apslogin", result);
        } catch (Throwable t) {
            throw t;
        }
    }
	
    @Test
	public void testInsertUsername() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("username", "admin2");
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            this.executeAction();
            assertEquals("admin2", action.getUsername());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testSearch_1() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(8, result.size());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testSearch_2() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("withProfile", "1");
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(5, result.size());
            assertEquals("editorCoach", result.get(0));
            assertEquals("editorCustomers", result.get(1));
            assertEquals("mainEditor", result.get(2));
            assertEquals("pageManagerCoach", result.get(3));
            assertEquals("supervisorCoach", result.get(4));
        } catch (Throwable t) {
            throw t;
        }
    }
    
    /*
	 * Filter by attribute
	 * 
     * Field Text
     * INPUT_FIELD: "<ATTRIBUTE_NAME>_textFieldName"
     * 
     * Field DATA
     * START INPUT_FIELD: "<ATTRIBUTE_NAME>_dateStartFieldName"
     * END INPUT_FIELD: "<ATTRIBUTE_NAME>_dateEndFieldName"
     */
    @Test
	public void testFindByName() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "Rick");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("editorCoach", result.get(0));
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testNotName() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "notadmin");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertTrue(result.isEmpty());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testFindSurname() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "on");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("editorCoach", result.get(0));
            assertEquals("pageManagerCoach", result.get(1));
        } catch (Throwable t) {
            throw t;
        }
    }
	
    @Test
	public void testNotSurname() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "notadmin");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assert (result.isEmpty());
        } catch (Throwable t) {
            throw t;
        }
    }
	
    @Test
	public void testFindByDateRange() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("birthdate_dateStartFieldName", "02/03/1945");
            this.addParameter("birthdate_dateEndFieldName", "04/09/2000");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals("editorCustomers", result.get(0));
            assertEquals("mainEditor", result.get(1));
            assertEquals("pageManagerCoach", result.get(2));
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testNotDateInRange() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("birthdate_dateStartFieldName", "25/11/1947");
            this.addParameter("birthdate_dateEndFieldName", "20/05/1952");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertTrue(result.isEmpty());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testFindEmail() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("email_textFieldName", "@mailinator.com");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(4, result.size());
            assertEquals("editorCoach", result.get(0));
            assertEquals("editorCustomers", result.get(1));
            assertEquals("mainEditor", result.get(2));
            assertEquals("pageManagerCoach", result.get(3));
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testNotEmailFind() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("email_textFieldName", "notmail");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertTrue(result.isEmpty());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testFindCrossAttribute() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("email_textFieldName", "@mailinator.com");
            this.addParameter("birthdate_dateStartFieldName", "02/03/1945");
            this.addParameter("birthdate_dateEndFieldName", "03/09/2000");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("editorCustomers", result.get(0));
            assertEquals("mainEditor", result.get(1));
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testFindNotCrossAttribute() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "Smith");
            this.addParameter("birthdate_dateStartFieldName", "02/03/1945");
            this.addParameter("birthdate_dateEndFieldName", "03/09/2000");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> result = action.getSearchResult();
            assertTrue(result.isEmpty());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testGetEntityPrototypes() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            String result = this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<IApsEntity> currentEntityPrototypes = ((UserProfileFinderAction) action).getEntityPrototypes();
            assertEquals(Action.SUCCESS, result);
            assertEquals(3, currentEntityPrototypes.size());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testChangeEntityType() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("entityTypeCode", "TEST");
            String result = this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            assertEquals(Action.SUCCESS, result);
            assertEquals("TEST", ((UserProfileFinderAction) action).getEntityTypeCode());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
	public void testGetEmail() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/User", "search");
            this.addParameter("fullname_textFieldName", "Raimond");
            this.addParameter("entityTypeCode", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
            String result = this.executeAction();
            UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
            List<String> searchResult = action.getSearchResult();
            assertEquals(Action.SUCCESS, result);
            assertNotNull(searchResult);
            String email = ((UserProfileFinderAction) action).getEmailAttributeValue(searchResult.get(0));
            assertEquals("raimond.stevenson@mailinator.com", email);
        } catch (Throwable t) {
            throw t;
        }
    }
    
	/*
	 * Filter by role
	 * 
     * Field Text
     * INPUT_FIELD: "<ROLE_NAME>_textFieldName"
     * 
     * Field DATA
     * START INPUT_FIELD: "<ROLE_NAME>_dateStartFieldName"
     * END INPUT_FIELD: "<ROLE_NAME>_dateEndFieldName"
     */
	@Test
	public void testSearchByRole() throws Throwable {
		String fullNameRole = SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_FULL_NAME;
		try {
			this.setUserOnSession("admin");
			this.initAction("/do/User", "search");
			this.addParameter(fullNameRole + "_textFieldName", "se");
			String result = this.executeAction();
			UserProfileFinderAction action = (UserProfileFinderAction) this.getAction();
			List<String> searchResult = action.getSearchResult();
			assertEquals(Action.SUCCESS, result);
			assertNotNull(searchResult);
			String[] expected = {"editorCustomers", "mainEditor"};
			assertEquals(expected.length, searchResult.size());
			for (int i = 0; i < expected.length; i++) {
				String expectedUsername = expected[i];
				assertEquals(expectedUsername, searchResult.get(i));
			}
		} catch (Throwable t) {
			throw t;
		}
	}
	
}
