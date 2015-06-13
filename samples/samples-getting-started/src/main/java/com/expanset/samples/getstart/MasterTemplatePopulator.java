package com.expanset.samples.getstart;

import java.util.Map;

import org.glassfish.jersey.server.mvc.Viewable;

import com.expanset.hk2.config.ConfiguredString;
import com.expanset.jersey.mvc.templates.TemplatePopulator;

/**
 * Populates template model by siteTitle variable loaded from the configuration file. 
 */
public class MasterTemplatePopulator implements TemplatePopulator {

	@ConfiguredString("siteTitle")
	private String siteTitle;
	
	@Override
	public void populate(Viewable viewable, Map<String, Object> model) {
		model.put("siteTitle", siteTitle);
	}
}
