package com.AssemblyProgramMikoAssignmentSB.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AssemblyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveResult(String key, String result) {
        redisTemplate.opsForValue().set(key, result);
    }

    public String getResult(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean checkKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    private final List<String> responseList = new ArrayList<>();

    public ResponseEntity<?> executeProgram(Map<String, String> program) {

        // Check if program is empty
        if (program.isEmpty()) {
            log.warn("Empty program");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }

        log.info("Program execution started : {}", program);

        for (Map.Entry<String, String> entry : program.entrySet()) {

            String value = entry.getValue().toUpperCase();

            String[] values = value.split("[\\s,#]+");

            if (values.length < 2 || values.length > 3) {
                log.warn("Invalid number of arguments {}", Arrays.asList(values));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
            }

            switch (values[0]) {
                case "MV":
                    if (handleMvOperation(values[1], values[2]).equals("Failure")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
                    }
                    break;
                case "ADD":
                    if (handleAddOperation(values[1], values[2]).equals("Failure")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
                    }
                    break;
                case "SHOW":
                    if (handleShowOperation(values[1]).equals("Failure")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
                    }
                    break;
                default:
                    log.warn("Invalid operation");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
            }
        }
        if (!responseList.isEmpty()) {
            for (String response : responseList) {
                log.info(response);
            }
            log.info("Program executed successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        }
        log.info("Program executed successfully");
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    private String handleShowOperation(String value) {
        if (value.matches("REG[1-9]\\d*")) {
            if (checkKey(value)) {
                log.info("Register found {}", value);
                if (!responseList.contains(value)) {
                    responseList.add("Key: " + value + ", Value: " + getResult(value));
                }
                return "Success";
            } else {
                log.warn("Register does not exist {}", value);
                return "Failure";
            }

        } else if (value.matches("REG")) {
            log.info(" printing All registers");
            Set<String> keys = redisTemplate.keys("*");

            if (keys != null) {
                for (String key : keys) {
                    String keyValue = redisTemplate.opsForValue().get(key);
                    if (!responseList.contains(key)) {
                        responseList.add("Key: " + key + ", Value: " + keyValue);
                    }
                }
            }
            return "Success";
        }
        log.warn("Invalid register {}", value);
        return "Failure";

    }

    private String handleAddOperation(String register, String registerOrValue) {

        if (BothAreRegOrNot(List.of(register, registerOrValue))) {
            if (BothRegAreSame(register, registerOrValue)) {
                log.warn("Both registers are same {} - {}", register, registerOrValue);
                return "Failure";
            }
            if (checkKey(register)) {
                if (checkKey(registerOrValue)) {
                    int value = Integer.parseInt(getResult(registerOrValue));
                    int registerValue = Integer.parseInt(getResult(register));
                    saveResult(register, String.valueOf(registerValue + value));
                    log.info("Register {} is updated with value {}", register, registerValue + value);
                    return "Success";
                } else {
                    log.warn("Register 2 does not exist {}", registerOrValue);
                    return "Failure";
                }
            } else {
                log.warn("Register 1 does not exist {}", register);
                return "Failure";
            }
        } else {
            if (register.matches("REG[1-9]\\d*")) {

                if (valueIsValid(registerOrValue)) {
                    saveResult(register,
                            String.valueOf(Integer.parseInt(getResult(register)) + Integer.parseInt(registerOrValue)));
                    log.info("Register {} is updated with value {}", register, getResult(register));
                    return "Success";

                } else {
                    log.warn("Invalid value {}", registerOrValue);
                    return "Failure";

                }
            } else {
                log.warn("Invalid register {}", register);
                return "Failure";
            }

        }

    }

    private String handleMvOperation(String register, String value) {
        if (BothAreRegOrNot(List.of(register, value))) {
            log.warn("Both are registers we need 1 reg and 1 value for MV operation {} - {}", register, value);
            return "Failure";
        }
        if (!register.matches("REG[1-9]\\d*")) {
            log.warn("Invalid register {}", register);
            return "Failure";
        }
        if (!valueIsValid(value)) {
            log.warn("Invalid value {}", value);
            return "Failure";
        }
        if (checkKey(register)) {
            log.warn("Register already exist moving register to new value {} - {}", register, value);
        } else {
            log.info("Register does not exist, creating new register{} - {}", register, value);
        }
        saveResult(register, value);
        return "Success";
    }

    private boolean BothAreRegOrNot(List<String> values) {
        for (String value : values) {
            if (!value.matches("REG[1-9]\\d*")) {
                return false;
            }
        }
        return true;
    }

    private boolean BothRegAreSame(String reg1, String reg2) {
        return reg1.equals(reg2);
    }

    private boolean valueIsValid(String value) {
        int num = Integer.parseInt(value);
        return num >= 0;
    }

}
