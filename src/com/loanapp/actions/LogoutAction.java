package com.loanapp.actions;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

public class LogoutAction {
    
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);

        if (session != null) {
            session.invalidate();
        }
        
        return "success";
    }
}
