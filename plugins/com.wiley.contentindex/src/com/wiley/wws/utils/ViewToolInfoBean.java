package com.wiley.wws.utils;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;
import org.apache.velocity.tools.view.tools.ViewTool;


/**
 * Created by Gareth Wright
 * Date: 11/10/12
 * 
 * Generic ViewToolInfo class that can be implemented as a simple POJO bean.
 * Forces a singleton model for the associated ViewTool, which can be easily wired in. 
 * 
 * The ViewTool is not initialised by default as it is assumed to be initialised 
 * prior to being wired into ViewToolInfoBean, i.e. once and done as ViewTool is a singleton.
 * 
 */
public class ViewToolInfoBean extends ServletToolInfo {

	private ViewTool viewTool;
	private boolean forceInit = false; //Not by default as this is a singleton model. Initialise via bean wiring once.
	
	public ViewToolInfoBean() {
		setScope(ViewContext.APPLICATION);
	}
	
	public Object getInstance(Object initData) {
		if(forceInit){
			viewTool.init(initData);
		}
		return viewTool;
	}
	public ViewTool getViewTool() {
		return viewTool;
	}
	public void setViewTool(ViewTool viewTool) {
		this.viewTool = viewTool;
	}
	public void setForceInit(boolean forceInit) {
		this.forceInit = forceInit;
	}

    public String getClassname () {
    	String className = ViewTool.class.getName();
    	if(viewTool!=null){
    		className = viewTool.getClass().getName();
    	}
        return className;
    }
	
}
