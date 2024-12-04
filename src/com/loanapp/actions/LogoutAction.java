package com.loanapp.actions;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport {
    
    @Override
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);

        if (session != null) {
            session.invalidate();
        }
        
        return "success";
    }
}
