package com.aCompany.wms.util;

public class WorkflowUtil {

    public static String nextWorkflowStatus(String currentStatus) {
        switch (currentStatus) {
            case "PLANNING":
                return "PICKING";
            case "PICKING":
                return "PACKING";
            case "PACKING":
                return "DISPATCHED";
            default:
                return currentStatus;
        }
    }

    public static boolean canTransition(String fromStatus, String toStatus) {
        return nextWorkflowStatus(fromStatus).equals(toStatus);
    }
}

