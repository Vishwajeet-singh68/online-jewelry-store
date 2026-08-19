package com.jewelry.auth.mapper;

import com.jewelry.auth.dto.request.RegisterRequest;
import com.jewelry.auth.dto.response.UserResponse;
import com.jewelry.auth.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T00:32:11+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( request.getFirstName() );
        user.lastName( request.getLastName() );
        user.email( request.getEmail() );
        user.password( request.getPassword() );
        user.phoneNumber( request.getPhoneNumber() );

        return user.build();
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.roles( mapRoles( user.getRoles() ) );
        userResponse.id( user.getId() );
        userResponse.firstName( user.getFirstName() );
        userResponse.lastName( user.getLastName() );
        userResponse.email( user.getEmail() );
        userResponse.phoneNumber( user.getPhoneNumber() );
        if ( user.getAccountStatus() != null ) {
            userResponse.accountStatus( user.getAccountStatus().name() );
        }

        return userResponse.build();
    }
}
