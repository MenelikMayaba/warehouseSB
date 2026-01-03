package com.aCompany.wms.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderForm {

    @NotBlank(message = "Workflow status is required")
    private String workflowStatus;

    @NotBlank(message = "Invoice priority is required")
    @Pattern(regexp = "PRIORITY|ACCURATE|UNUSED", message = "Invalid invoice priority")
    private String invoicePriority;

    public OrderForm() {
        // default values to avoid nulls in views
        this.workflowStatus = "PLANNING";
        this.invoicePriority = "ACCURATE";
    }

    public OrderForm(String workflowStatus, String invoicePriority) {
        this.workflowStatus = workflowStatus;
        this.invoicePriority = invoicePriority;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getInvoicePriority() {
        return invoicePriority;
    }

    public void setInvoicePriority(String invoicePriority) {
        this.invoicePriority = invoicePriority;
    }

    @Override
    public String toString() {
        return "OrderForm{" +
                "workflowStatus='" + workflowStatus + '\'' +
                ", invoicePriority='" + invoicePriority + '\'' +
                '}';
    }
}
