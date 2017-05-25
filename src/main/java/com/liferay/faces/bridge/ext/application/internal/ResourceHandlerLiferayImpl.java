/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.ext.application.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceHandlerLiferayImpl extends ResourceHandlerWrapper {

	// Private Constants
	private static final boolean PRIMEFACES_DETECTED = ProductFactory.getProduct(Product.Name.PRIMEFACES).isDetected();
	private static final Set<String> PRIMEFACES_JQUERY_PLUGIN_JS_RESOURCES;
	private static final boolean RICHFACES_DETECTED = ProductFactory.getProduct(Product.Name.RICHFACES).isDetected();

	static {

		// This list of resources was obtained by building Primefaces and searching the target/ directory for js files
		// containg "typeof\\s+define\\s*=(=+)\\s*[\"']function[\"']|[\"']function[\"']\\s*=(=+)\\s*typeof\\s+define".
		Set<String> primefacesJQueryPluginResources = new HashSet<String>();
		primefacesJQueryPluginResources.add("diagram/diagram.js");
		primefacesJQueryPluginResources.add("fileupload/fileupload.js");
		primefacesJQueryPluginResources.add("inputnumber/0-autoNumeric.js");
		primefacesJQueryPluginResources.add("inputnumber/inputnumber.js");
		primefacesJQueryPluginResources.add("jquery/jquery-plugins.js");
		primefacesJQueryPluginResources.add("jquery/jquery.js");
		primefacesJQueryPluginResources.add("knob/1-jquery.knob.js");
		primefacesJQueryPluginResources.add("knob/knob.js");
		primefacesJQueryPluginResources.add("mobile/jquery-mobile.js");
		primefacesJQueryPluginResources.add("moment/moment.js");
		primefacesJQueryPluginResources.add("mousewheel/jquery.mousewheel.min.js");
		primefacesJQueryPluginResources.add("photocam/photocam.js");
		primefacesJQueryPluginResources.add("push/push.js");
		primefacesJQueryPluginResources.add("raphael/raphael.js");
		primefacesJQueryPluginResources.add("schedule/schedule.js");
		primefacesJQueryPluginResources.add("texteditor/texteditor.js");
		primefacesJQueryPluginResources.add("touch/touchswipe.js");
		PRIMEFACES_JQUERY_PLUGIN_JS_RESOURCES = Collections.unmodifiableSet(primefacesJQueryPluginResources);
	}

	// Private Data Members
	private ResourceHandler wrappedResourceHandler;

	// Final Data Members
	private final Set<String> resourcesWithDisabledAMDLoader;

	public ResourceHandlerLiferayImpl(ResourceHandler wrappedResourceHandler) {

		this.wrappedResourceHandler = wrappedResourceHandler;

		Set<String> resourcesWithDisabledAMDLoader = Collections.emptySet();
		FacesContext startupFacesContext = FacesContext.getCurrentInstance();

		if (startupFacesContext != null) {

			ExternalContext externalContext = startupFacesContext.getExternalContext();
			String resourcesWithDisabledAMDLoaderString = externalContext.getInitParameter(
					"com.liferay.faces.bridge.ext.application.disableAMDLoaderForResources");

			if (resourcesWithDisabledAMDLoaderString != null) {

				resourcesWithDisabledAMDLoaderString = resourcesWithDisabledAMDLoaderString.trim();

				String[] resourceIds = resourcesWithDisabledAMDLoaderString.split(",");
				boolean first = true;

				for (String resourceId : resourceIds) {

					if ((resourceId != null) && !"".equals(resourceId)) {

						if (first) {

							resourcesWithDisabledAMDLoader = new HashSet<String>();
							first = false;
						}

						resourceId = resourceId.trim();
						resourcesWithDisabledAMDLoader.add(resourceId);
					}
				}

				if (!resourcesWithDisabledAMDLoader.isEmpty()) {
					resourcesWithDisabledAMDLoader = Collections.unmodifiableSet(resourcesWithDisabledAMDLoader);
				}
			}
		}

		this.resourcesWithDisabledAMDLoader = resourcesWithDisabledAMDLoader;
	}

	@Override
	public Resource createResource(String resourceName) {

		Resource resource = super.createResource(resourceName);

		if (isJavaScriptResource(resource) && !(resource instanceof JSResourceWithDisabledAMDLoaderImpl) &&
				(isJQueryPluginJSResource(null, resourceName) || !isAMDLoaderEnabledForResource(null, resourceName))) {
			resource = new JSResourceWithDisabledAMDLoaderImpl(resource);
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		Resource resource = super.createResource(resourceName, libraryName);

		if (isJavaScriptResource(resource) && !(resource instanceof JSResourceWithDisabledAMDLoaderImpl) &&
				(isJQueryPluginJSResource(libraryName, resourceName) ||
					!isAMDLoaderEnabledForResource(libraryName, resourceName))) {
			resource = new JSResourceWithDisabledAMDLoaderImpl(resource);
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {

		Resource resource = super.createResource(resourceName, libraryName, contentType);

		if (isJavaScriptResource(resource) && !(resource instanceof JSResourceWithDisabledAMDLoaderImpl) &&
				(isJQueryPluginJSResource(libraryName, resourceName) ||
					!isAMDLoaderEnabledForResource(libraryName, resourceName))) {
			resource = new JSResourceWithDisabledAMDLoaderImpl(resource);
		}

		return resource;
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

	public boolean isAMDLoaderEnabledForResource(String libraryName, String resourceName) {

		String resourceId = resourceName;

		if ((libraryName != null) && !"".equals(libraryName)) {
			resourceId = libraryName + ":" + resourceName;
		}

		return !resourcesWithDisabledAMDLoader.contains(resourceId);
	}

	private boolean isJavaScriptResource(Resource resource) {

		if (resource != null) {

			String resourceName = resource.getResourceName();
			String contentType = resource.getContentType();

			return (resourceName.endsWith(".js") || "application/javascript".equals(contentType) ||
					"text/javascript".equals(contentType));
		}
		else {
			return false;
		}
	}

	private boolean isJQueryPluginJSResource(String resourceLibrary, String resourceName) {

		boolean primeFacesJQueryPluginJSResource = PRIMEFACES_DETECTED &&
			((resourceLibrary == null) || resourceLibrary.equals("primefaces")) &&
			PRIMEFACES_JQUERY_PLUGIN_JS_RESOURCES.contains(resourceName);

		boolean richFacesJQueryPluginJSResource = false;

		if (RICHFACES_DETECTED) {

			boolean richfacesResourceLibrary = ("org.richfaces.resource".equals(resourceLibrary) ||
					"org.richfaces.staticResource".equals(resourceLibrary) || "org.richfaces".equals(resourceLibrary));

			richFacesJQueryPluginJSResource = ((resourceLibrary == null) || richfacesResourceLibrary) &&
				(resourceName.endsWith("packed.js") || resourceName.endsWith("jquery.js"));
		}

		return (primeFacesJQueryPluginJSResource || richFacesJQueryPluginJSResource);
	}
}
