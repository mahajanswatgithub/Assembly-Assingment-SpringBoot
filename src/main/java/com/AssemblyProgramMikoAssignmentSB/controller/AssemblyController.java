package com.AssemblyProgramMikoAssignmentSB.controller;

import com.AssemblyProgramMikoAssignmentSB.service.AssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/assembly-program")
public class AssemblyController {

    @Autowired
    private AssemblyService assemblyService;


    /**
     * Executes the program using the provided parameters.
     *
     * @param  program   a map containing the program parameters
     * @return          a ResponseEntity with the result of the program execution
     */

    @PostMapping("/execute")
    public ResponseEntity<?> executeProgram(@RequestParam Map<String, String> program) {

         return assemblyService.executeProgram(program);
    }
}
