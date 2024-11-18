package com.loanapp.interceptors;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class AuthInterceptor implements Interceptor{

    @Override
    public void init() {
        System.out.println("AuthInterceptor initialized..");
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(false);

        if (session != null &&  session.getAttribute("userSession")!= null) {
            session.setMaxInactiveInterval(60*60);
            return invocation.invoke();
        } 
        else {
            System.out.println("Session exired.");
            return "error";
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthInterceptor destroyed..");
    }
}