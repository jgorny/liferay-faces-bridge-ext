/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.ext.filter.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.faces.FacesWrapper;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletSecurityException;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;


/**
 * @author  Neil Griffin
 */
public abstract class LiferayActionURLWrapper implements LiferayActionURL, FacesWrapper<LiferayActionURL> {

	public abstract LiferayActionURL getWrapped();

	public void addProperty(String key, String value) {
		getWrapped().addProperty(key, value);
	}

	public Map<String, String[]> getParameterMap() {
		return getWrapped().getParameterMap();
	}

	public PortletMode getPortletMode() {
		return getWrapped().getPortletMode();
	}

	public WindowState getWindowState() {
		return getWrapped().getWindowState();
	}

	public void removePublicRenderParameter(String name) {
		getWrapped().removePublicRenderParameter(name);
	}

	public void setParameter(String name, String value) {
		getWrapped().setParameter(name, value);
	}

	public void setParameter(String name, String[] values) {
		getWrapped().setParameter(name, values);
	}

	public void setParameters(Map<String, String[]> parameters) {
		getWrapped().setParameters(parameters);
	}

	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		getWrapped().setPortletMode(portletMode);
	}

	public void setProperty(String key, String value) {
		getWrapped().setProperty(key, value);
	}

	public void setSecure(boolean secure) throws PortletSecurityException {
		getWrapped().setSecure(secure);
	}

	public void setWindowState(WindowState windowState) throws WindowStateException {
		getWrapped().setWindowState(windowState);
	}

	@Override
	public String toString() {
		return getWrapped().toString();
	}

	public void write(Writer out) throws IOException {
		getWrapped().write(out);
	}

	public void write(Writer out, boolean escapeXML) throws IOException {
		getWrapped().write(out, escapeXML);
	}
}
