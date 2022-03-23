package com.pizzaiolo.authorization.controllers;

import com.pizzaiolo.authorization.events.OnResetPasswordEvent;
import com.pizzaiolo.authorization.exceptions.ResourceNotFoundException;
import com.pizzaiolo.authorization.models.dtos.ForgotPasswordDto;
import com.pizzaiolo.authorization.models.dtos.ResetPasswordDto;
import com.pizzaiolo.authorization.models.entities.UserAccount;
import com.pizzaiolo.authorization.models.response.BadRequestResponse;
import com.pizzaiolo.authorization.models.response.SuccessResponse;
import com.pizzaiolo.authorization.services.interfaces.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.pizzaiolo.authorization.utils.Constants.*;

import java.util.HashMap;

import com.pizzaiolo.authorization.models.response.InvalidDataResponse;
import com.pizzaiolo.authorization.models.entities.User;
import com.pizzaiolo.authorization.services.interfaces.UserService;


@Api(tags = SWG_RESPWD_TAG_NAME, description = SWG_RESPWD_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class ResetPasswordController {

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    private final UserAccountService userAccountService;

    public ResetPasswordController(
        UserService userService,
        ApplicationEventPublisher eventPublisher,
        UserAccountService userAccountService
    ) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.userAccountService = userAccountService;
    }

    @ApiOperation(value = SWG_RESPWD_FORGOT_OPERATION, response = SuccessResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = SWG_RESPWD_FORGOT_MESSAGE, response = SuccessResponse.class),
        @ApiResponse(code = 400, message = SWG_RESPWD_FORGOT_ERROR, response = BadRequestResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto)
        throws ResourceNotFoundException {
        User user = userService.findByEmail(forgotPasswordDto.getEmail());
        Map<String, String> result = new HashMap<>();

        if (user == null) {
            result.put(MESSAGE_KEY, NO_USER_FOUND_WITH_EMAIL_MESSAGE);

            return ResponseEntity.badRequest().body(result);
        }

        eventPublisher.publishEvent(new OnResetPasswordEvent(user));

        result.put(MESSAGE_KEY, PASSWORD_LINK_SENT_MESSAGE);

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = SWG_RESPWD_RESET_OPERATION, response = SuccessResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = SWG_RESPWD_RESET_MESSAGE, response = SuccessResponse.class),
        @ApiResponse(code = 400, message = SWG_RESPWD_RESET_ERROR, response = BadRequestResponse.class),
        @ApiResponse(code = 422, message = INVALID_DATA_MESSAGE, response = InvalidDataResponse.class),
    })
    @PostMapping(value = "/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordDto passwordResetDto)
        throws ResourceNotFoundException {
        UserAccount userAccount = userAccountService.findByToken(passwordResetDto.getToken());
        Map<String, String> result = new HashMap<>();

        if (userAccount.isExpired()) {
            result.put(MESSAGE_KEY, TOKEN_EXPIRED_MESSAGE);

            userAccountService.delete(userAccount.getId());

            return ResponseEntity.badRequest().body(result);
        }

        userService.updatePassword(userAccount.getUser().getId(), passwordResetDto.getPassword());

        result.put(MESSAGE_KEY, RESET_PASSWORD_SUCCESS_MESSAGE);

        userAccountService.delete(userAccount.getId());

        return ResponseEntity.badRequest().body(result);
    }
}
