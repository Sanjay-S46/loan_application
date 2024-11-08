package com.loanapp.interceptors;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class RoleBasedInterceptor implements Interceptor{

    private User user; 

    @Override
    public void init() {
        System.out.println("Role based auth initialized");
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        user = (User) session.getAttribute("userSession");

        String userType = user.getUserType();
        String namespace = invocation.getProxy().getNamespace();
        
        if ((namespace.equals("/admin") && !userType.equals("admin")) ||
            (namespace.equals("/verifier") && !userType.equals("verifier"))) {
            // Unauthorized access
            return "unauthorized"; 
        }

        return invocation.invoke();

    }

    @Override
    public void destroy() {
        System.out.println("Role based auth destroyed");
    }

    
}
