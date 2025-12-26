package com.aCompany.warehouseSB.webControllers;

import com.aCompany.warehouseSB.picking.PickingService;
import com.aCompany.warehouseSB.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/picker")
public class PickerWebController {

    private final PickingService pickingService;

    public PickerWebController(PickingService pickingService) {
        this.pickingService = pickingService;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Get picking statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("assignedPicks", pickingService.countAssignedPicks());
        stats.put("completedToday", pickingService.countCompletedToday());
        stats.put("avgPickTime", pickingService.getAveragePickTime());
        stats.put("accuracy", pickingService.getPickingAccuracy());

        // Get tasks for the picker
        List<PickingTaskDTO> tasks = pickingService.getPickingTasks();

        model.addAttribute("stats", stats);
        model.addAttribute("pickingTasks", tasks);
        return "pickerDashboard";
    }

    @PostMapping("/start-task/{taskId}")
    @ResponseBody
    public ResponseEntity<?> startTask(@PathVariable Long taskId) {
        try {
            pickingService.startPickingTask(taskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/complete-task/{taskId}")
    @ResponseBody
    public ResponseEntity<?> completeTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser) {
        try {
            pickingService.completePickingTask(taskId, currentUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}