package com.aCompany.wms.aspect;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.repository.StockTransactionRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Pointcut("execution(* com.aCompany.wms.service.*.*(..))")
    public void serviceMethods() {}

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Get current user
        String username = "system";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        // Create audit log
        StockTransaction log = new StockTransaction();
        log.setAction(methodName);
        log.setScannedBy(username);
        log.setScannedAt(LocalDateTime.now());
        log.setNotes("Method: " + className + "." + methodName);

        // You can add more specific logic based on the method name or class
        if (isMethodName(methodName)) {
            // Set appropriate type based on the operation
            if (methodName.contains("Product")) {
                log.setType(TransactionType.PRODUCT_UPDATE);
            } else if (methodName.contains("Order")) {
                log.setType(TransactionType.ORDER_UPDATE);
            }
            // Add more conditions as needed

            transactionRepository.save(log);
        }
    }

    private boolean isMethodName(String methodName) {
        return methodName.startsWith("save") || methodName.startsWith("update") || methodName.startsWith("delete");
    }
}