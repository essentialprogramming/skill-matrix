package com.security;

import com.spring.ApplicationContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.util.SimpleMethodInvocation;

import java.lang.reflect.Method;

@Slf4j
public class SecurityChecker {

    //----------------------------------------------START SecurityObject-----------------------------------------------
    private static class SecurityObject {
        public void triggerCheck() { /*NOP*/ }
    }

    private static final Method triggerCheckMethod;
    private static final SecurityObject securityObject = new SecurityObject();

    static {
        Method method = null;
        try {
            method = securityObject.getClass().getMethod("triggerCheck");
        } catch (NoSuchMethodException e) {
            //IGNORE
        }
        triggerCheckMethod = method;
    }
    //----------------------------------------------END SecurityObject-----------------------------------------------


    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final MethodSecurityExpressionHandler expressionHandler = createExpressionHandler();


    public static boolean hasAuthorities(Authentication authentication, String securityExpression) {
        if (log.isDebugEnabled()) {
            log.debug("Checking security expression [" + securityExpression + "]...");
        }

        final EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
                authentication, new SimpleMethodInvocation(securityObject, triggerCheckMethod));

        final boolean checkResult = ExpressionUtils.evaluateAsBoolean(
                parser.parseExpression(securityExpression), evaluationContext);

        if (log.isDebugEnabled()) {
            log.debug("Check result: " + checkResult);
        }

        return checkResult;
    }

    protected static MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        //expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());// NOT needed yet

        expressionHandler.setApplicationContext(ApplicationContextFactory.getSpringApplicationContext());
        //expressionHandler.setApplicationContext(ContextLoader.getCurrentWebApplicationContext()); //Alternative solution to retrieve bean

        return expressionHandler;
    }
}