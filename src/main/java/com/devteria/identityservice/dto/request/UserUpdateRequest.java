package com.devteria.identityservice.dto.request;

import com.devteria.identityservice.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min= 18, message = "INVALID_DOB")
    LocalDate dob;

    List<String> roles;
}
